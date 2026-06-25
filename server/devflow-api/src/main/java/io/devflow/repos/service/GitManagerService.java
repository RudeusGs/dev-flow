package io.devflow.repos.service;

import io.devflow.common.config.GitProperties;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.lib.ObjectLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitManagerService {

    private static final Logger log = LoggerFactory.getLogger(GitManagerService.class);
    private final GitProperties gitProperties;

    public GitManagerService(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
        ensureStorageDirectoryExists();
    }

    private void ensureStorageDirectoryExists() {
        try {
            Path storageLoc = gitProperties.getStorageLocation();
            if (!Files.exists(storageLoc)) {
                Files.createDirectories(storageLoc);
                log.info("Created Git storage directory at: {}", storageLoc.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create Git storage directory", e);
            throw new RuntimeException("Could not initialize Git storage directory", e);
        }
    }

    public File getRepositoryDir(String ownerUsername, String repoName) {
        // Validate inputs to prevent path traversal
        validatePathSegment(ownerUsername);
        validatePathSegment(repoName);

        Path resolved = gitProperties.getStorageLocation()
                .resolve(ownerUsername)
                .resolve(repoName + ".git")
                .normalize();

        if (!resolved.startsWith(gitProperties.getStorageLocation())) {
            throw new IllegalArgumentException("Invalid repository path: path traversal detected");
        }

        return resolved.toFile();
    }

    private void validatePathSegment(String segment) {
        if (segment == null || segment.isBlank()
                || segment.contains("..") || segment.contains("/")
                || segment.contains("\\") || segment.contains("\0")) {
            throw new IllegalArgumentException("Invalid path segment: " + segment);
        }
    }

    public void initBareRepository(String ownerUsername, String repoName) {
        File repoDir = getRepositoryDir(ownerUsername, repoName);
        if (repoDir.exists()) {
            log.warn("Git repository directory already exists at: {}", repoDir.getAbsolutePath());
            return;
        }

        try {
            try (Git git = Git.init().setDirectory(repoDir).setBare(true).call()) {
                log.info("Initialized bare Git repository at: {}", repoDir.getAbsolutePath());
            }
        } catch (GitAPIException e) {
            log.error("Failed to initialize bare Git repository", e);
            throw new RuntimeException("Failed to initialize Git repository", e);
        }
    }

    public List<io.devflow.commits.dto.CommitDto> listCommits(String ownerUsername, String repoName, String branchName, int limit) {
        File repoDir = getRepositoryDir(ownerUsername, repoName);
        List<io.devflow.commits.dto.CommitDto> result = new ArrayList<>();
        if (!repoDir.exists()) return result;

        try (Git git = Git.open(repoDir)) {
            Repository repository = git.getRepository();
            ObjectId branchId = repository.resolve(branchName == null ? "HEAD" : branchName);
            if (branchId == null) {
                return result; // Branch not found or repo is empty
            }

            Iterable<RevCommit> commits = git.log().add(branchId).setMaxCount(limit).call();
            for (RevCommit rc : commits) {
                result.add(mapRevCommitToDto(rc, repository));
            }
        } catch (IOException | GitAPIException e) {
            log.error("Failed to list commits", e);
            throw new RuntimeException("Failed to list commits", e);
        }
        return result;
    }

    private io.devflow.commits.dto.CommitDto mapRevCommitToDto(RevCommit rc, Repository repository) {
        PersonIdent author = rc.getAuthorIdent();
        PersonIdent committer = rc.getCommitterIdent();
        return io.devflow.commits.dto.CommitDto.builder()
                .id(rc.getName())
                .commitHash(rc.getName())
                .message(rc.getFullMessage())
                .authorName(author.getName())
                .authorEmail(author.getEmailAddress())
                .committerName(committer.getName())
                .committerEmail(committer.getEmailAddress())
                .authoredAt(author.getWhenAsInstant())
                .committedAt(committer.getWhenAsInstant())
                .build();
    }

    public List<io.devflow.sourcefiles.dto.SourceFileDto> listFiles(String ownerUsername, String repoName, String branchName, String path) {
        File repoDir = getRepositoryDir(ownerUsername, repoName);
        List<io.devflow.sourcefiles.dto.SourceFileDto> result = new ArrayList<>();
        if (!repoDir.exists()) return result;

        try (Git git = Git.open(repoDir);
             Repository repository = git.getRepository();
             RevWalk revWalk = new RevWalk(repository);
             TreeWalk treeWalk = new TreeWalk(repository)) {

            ObjectId branchId = repository.resolve(branchName == null ? "HEAD" : branchName);
            if (branchId == null) {
                return result; // Branch not found or repo is empty
            }

            RevCommit commit = revWalk.parseCommit(branchId);
            RevTree tree = commit.getTree();

            if (path != null && !path.isEmpty()) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(false);
                treeWalk.setFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(path));
                if (!treeWalk.next()) {
                    return result; // Path not found
                }
                if (treeWalk.isSubtree()) {
                    treeWalk.enterSubtree();
                } else {
                    // It's a single file
                    result.add(createSourceFileDto(treeWalk, commit));
                    return result;
                }
            } else {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(false);
            }

            while (treeWalk.next()) {
                result.add(createSourceFileDto(treeWalk, commit));
            }
        } catch (IOException e) {
            log.error("Failed to list files from JGit", e);
            throw new RuntimeException("Failed to list files", e);
        }
        return result;
    }

    public String getFileContent(String ownerUsername, String repoName, String branchName, String path) {
        File repoDir = getRepositoryDir(ownerUsername, repoName);
        if (!repoDir.exists()) return null;

        try (Git git = Git.open(repoDir);
             Repository repository = git.getRepository();
             RevWalk revWalk = new RevWalk(repository);
             TreeWalk treeWalk = new TreeWalk(repository)) {

            ObjectId branchId = repository.resolve(branchName == null ? "HEAD" : branchName);
            if (branchId == null) {
                return null;
            }

            RevCommit commit = revWalk.parseCommit(branchId);
            RevTree tree = commit.getTree();

            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(org.eclipse.jgit.treewalk.filter.PathFilter.create(path));

            if (!treeWalk.next()) {
                return null;
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            return new String(loader.getBytes(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Failed to read file content from JGit", e);
            throw new RuntimeException("Failed to read file content", e);
        }
    }

    private io.devflow.sourcefiles.dto.SourceFileDto createSourceFileDto(TreeWalk treeWalk, RevCommit commit) {
        return io.devflow.sourcefiles.dto.SourceFileDto.builder()
                .id(treeWalk.getObjectId(0).getName())
                .name(treeWalk.getNameString())
                .path(treeWalk.getPathString())
                .fileType(treeWalk.isSubtree() ? io.devflow.sourcefiles.enums.SourceFileType.FOLDER : io.devflow.sourcefiles.enums.SourceFileType.FILE)
                .sizeBytes(0L) // JGit TreeWalk doesn't eagerly load size for blobs
                .commitMessage(commit.getShortMessage())
                .lastModifiedAt(commit.getAuthorIdent().getWhenAsInstant())
                .build();
    }

    public List<io.devflow.branches.dto.BranchDto> listBranches(String ownerUsername, String repoName, String defaultBranchName) {
        File repoDir = getRepositoryDir(ownerUsername, repoName);
        List<io.devflow.branches.dto.BranchDto> result = new ArrayList<>();
        if (!repoDir.exists()) return result;

        try (Git git = Git.open(repoDir)) {
            List<Ref> branches = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            for (Ref ref : branches) {
                String name = ref.getName();
                if (name.startsWith("refs/heads/")) {
                    name = name.substring("refs/heads/".length());
                }
                boolean isDefault = name.equals(defaultBranchName);
                
                result.add(io.devflow.branches.dto.BranchDto.builder()
                        .name(name)
                        .headCommitId(ref.getObjectId().getName())
                        .defaultBranch(isDefault)
                        .protectedBranch(false)
                        .build());
            }
        } catch (IOException | GitAPIException e) {
            log.error("Failed to list branches from JGit", e);
            throw new RuntimeException("Failed to list branches", e);
        }
        return result;
    }

    public void deleteRepository(String ownerUsername, String repoName) {
        File repoDir = getRepositoryDir(ownerUsername, repoName);
        if (repoDir.exists()) {
            deleteDirectoryRecursively(repoDir);
            log.info("Deleted Git repository at: {}", repoDir.getAbsolutePath());
        }
    }

    private void deleteDirectoryRecursively(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectoryRecursively(file);
            }
        }
        if (!directory.delete()) {
            log.warn("Failed to delete directory or file: {}", directory.getAbsolutePath());
        }
    }
}
