import { Component, Input, OnInit, inject, ViewEncapsulation, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SourceFileService } from '../../../core/services/source-file.service';
import * as Prism from 'prismjs';
// Add basic languages
import 'prismjs/components/prism-typescript';
import 'prismjs/components/prism-javascript';
import 'prismjs/components/prism-java';
import 'prismjs/components/prism-css';
import 'prismjs/components/prism-markup'; // HTML
import 'prismjs/components/prism-json';
import 'prismjs/components/prism-bash';
import 'prismjs/components/prism-yaml';

@Component({
  selector: 'app-source-file-viewer',
  standalone: true,
  imports: [CommonModule],
  encapsulation: ViewEncapsulation.None, // Needed for Prism CSS to apply
  template: `
    <div class="file-viewer fade-in">
      <div class="file-header flex-between p-3 border-bottom">
        <strong>{{ path }}</strong>
        <button class="btn btn-sm" (click)="close()">Close</button>
      </div>
      
      <div *ngIf="loading" class="flex-center p-4">
        <div class="spinner"></div>
      </div>
      
      <div *ngIf="!loading && !error" class="file-content">
        <pre><code [class]="'language-' + getLanguageClass()">{{ content }}</code></pre>
      </div>
      
      <div *ngIf="error" class="p-4 text-danger text-center">
        Failed to load file content.
      </div>
    </div>
  `,
  styles: [`
    .file-viewer {
      border: 1px solid var(--border-color);
      border-radius: var(--radius-md);
      overflow: hidden;
      background: var(--bg-surface);
    }
    .file-header {
      background: var(--bg-surface);
      border-bottom: 1px solid var(--border-color);
    }
    .btn-sm { padding: 0.25rem 0.5rem; font-size: 0.8rem; }
    .file-content pre {
      margin: 0;
      border-radius: 0;
      border: none;
      background: var(--bg-surface);
      padding: 1rem;
      font-size: 0.875rem;
      line-height: 1.5;
      overflow-x: auto;
    }
    .border-bottom { border-bottom: 1px solid var(--border-color); }
    
    /* Prism Light Theme Fallback overrides */
    code[class*="language-"], pre[class*="language-"] {
      color: var(--text-primary);
      text-shadow: none;
      font-family: Consolas, Monaco, 'Andale Mono', 'Ubuntu Mono', monospace;
    }
    .token.comment, .token.prolog, .token.doctype, .token.cdata { color: #6e7781; }
    .token.punctuation { color: #24292f; }
    .token.namespace { opacity: .7; }
    .token.property, .token.tag, .token.boolean, .token.number, .token.constant, .token.symbol, .token.deleted { color: #0550ae; }
    .token.selector, .token.attr-name, .token.string, .token.char, .token.builtin, .token.inserted { color: #0a3069; }
    .token.operator, .token.entity, .token.url, .language-css .token.string, .style .token.string { color: #cf222e; }
    .token.atrule, .token.attr-value, .token.keyword { color: #cf222e; }
    .token.function, .token.class-name { color: #8250df; }
    .token.regex, .token.important, .token.variable { color: #e36209; }
  `]
})
export class SourceFileViewerComponent implements OnInit, AfterViewChecked {
  @Input({ required: true }) ownerUsername!: string;
  @Input({ required: true }) repoName!: string;
  @Input({ required: true }) branchName!: string;
  @Input({ required: true }) path!: string;
  
  // Optional Output to emit close event
  @Input() onClose?: () => void;

  private sourceFileService = inject(SourceFileService);
  
  content = '';
  loading = true;
  error = false;
  private highlighted = false;

  ngOnInit() {
    this.loadFile();
  }

  ngAfterViewChecked() {
    if (!this.loading && !this.error && !this.highlighted && this.content) {
      Prism.highlightAll();
      this.highlighted = true;
    }
  }

  loadFile() {
    this.loading = true;
    this.error = false;
    this.highlighted = false;
    this.sourceFileService.getFileContent(this.ownerUsername, this.repoName, this.branchName, this.path).subscribe({
      next: (data) => {
        this.content = data;
        this.loading = false;
      },
      error: () => {
        this.error = true;
        this.loading = false;
      }
    });
  }

  getLanguageClass(): string {
    const ext = this.path.split('.').pop()?.toLowerCase();
    switch (ext) {
      case 'ts': return 'typescript';
      case 'js': return 'javascript';
      case 'java': return 'java';
      case 'css': return 'css';
      case 'html': return 'markup';
      case 'json': return 'json';
      case 'yml':
      case 'yaml': return 'yaml';
      case 'sh': return 'bash';
      case 'md': return 'markdown';
      default: return 'none';
    }
  }
  
  close() {
    if (this.onClose) {
      this.onClose();
    }
  }
}
