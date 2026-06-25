import { Component } from '@angular/core';

@Component({
  selector: 'app-pr-list',
  standalone: true,
  template: `
    <div class="fade-in glass-panel p-4" style="text-align: center; margin-top: 2rem;">
      <h2>Global Pull Requests</h2>
      <p class="text-secondary mt-4">Pull requests are scoped to individual repositories. Please navigate to a repository to view its pull requests.</p>
    </div>
  `,
  styles: [`
    .p-4 { padding: 2rem; }
    .mt-4 { margin-top: 1rem; }
    .text-secondary { color: var(--text-secondary); }
  `]
})
export class PrListComponent {}
