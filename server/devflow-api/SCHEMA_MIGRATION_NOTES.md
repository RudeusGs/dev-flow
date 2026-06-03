# Schema Migration Notes

## Git core model cleanup

Flyway migrations should handle these database changes when the migration structure is added:

- `commits`
  - Drop `branch_id`.
  - Drop `parent_commit_id`.
  - Add optional Git metadata columns: `tree_hash`, `author_name`, `author_email`, `committer_name`, `committer_email`, `authored_at`.
  - Replace old branch/parent indexes with repository/date and author indexes.

- `commit_parents`
  - Create a new helper table with UUID `id`, `repository_id`, `commit_id`, `parent_commit_id`, and `parent_order`.
  - Add unique constraints for `(commit_id, parent_commit_id)` and `(commit_id, parent_order)`.

- `branch_commits`
  - Create a new optional materialized history/index table with UUID `id`, `repository_id`, `branch_id`, `commit_id`, `committed_at`, and `created_at`.
  - Add a unique constraint for `(branch_id, commit_id)`.
  - This table is a query helper; the authoritative branch pointer remains `branches.head_commit_id`.

- `source_files`
  - Replace `content_text` with Git/blob storage metadata such as `blob_hash` and `object_storage_key`.

- `file_versions`
  - Add `repository_id`.
  - Make `source_file_id` nullable.
  - Replace `content_text` with `file_mode`, `blob_hash`, and `object_storage_key`.
  - Change uniqueness from `(source_file_id, commit_id)` to `(commit_id, path)`.

- `commit_file_changes` and `pull_request_file_changes`
  - Add `repository_id`.
  - Replace raw `patch_text` with `patch_storage_key`.
  - Add diff metadata such as blob hashes, file modes, and `diff_hunk_count`.

Database-level foreign keys, partial indexes, and advanced check constraints should be added in Flyway rather than through JPA relationship annotations.
