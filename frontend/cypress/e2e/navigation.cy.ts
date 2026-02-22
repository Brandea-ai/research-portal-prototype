describe('Navigation', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should navigate between all main views', () => {
    // Dashboard (already there after login)
    cy.get('.kpi-card').should('exist');

    // Reports
    cy.get('.sidebar__link').contains('Reports').click();
    cy.url().should('include', '/reports');
    cy.get('.data-table').should('exist');

    // Securities / Wertschriften
    cy.get('.sidebar__link').contains('Wertschriften').click();
    cy.url().should('include', '/securities');
    cy.get('.data-table').should('exist');

    // Analysts / Analysten
    cy.get('.sidebar__link').contains('Analysten').click();
    cy.url().should('include', '/analysts');
    cy.get('.analyst-grid').should('exist');

    // Back to Dashboard
    cy.get('.sidebar__link').contains('Dashboard').click();
    cy.url().should('include', '/dashboard');
    cy.get('.kpi-card').should('exist');
  });

  it('should highlight active sidebar link for Dashboard', () => {
    cy.get('.sidebar__link--active').should('contain.text', 'Dashboard');
  });

  it('should highlight active sidebar link for Reports', () => {
    cy.get('.sidebar__link').contains('Reports').click();
    cy.get('.sidebar__link--active').should('contain.text', 'Reports');
  });

  it('should highlight active sidebar link for Wertschriften', () => {
    cy.get('.sidebar__link').contains('Wertschriften').click();
    cy.get('.sidebar__link--active').should('contain.text', 'Wertschriften');
  });

  it('should highlight active sidebar link for Analysten', () => {
    cy.get('.sidebar__link').contains('Analysten').click();
    cy.get('.sidebar__link--active').should('contain.text', 'Analysten');
  });

  it('should display sidebar logo', () => {
    cy.get('.sidebar__logo-text').should('contain.text', 'RESEARCH');
    cy.get('.sidebar__logo-sub').should('contain.text', 'PORTAL');
  });

  it('should display topbar with page title and user info', () => {
    cy.get('.topbar__title').should('exist');
    cy.get('.topbar__user').should('exist');
    cy.get('.topbar__logout').should('contain.text', 'Abmelden');
  });

  it('should redirect unknown routes to dashboard', () => {
    cy.visit('/nonexistent-page');
    cy.url().should('include', '/dashboard');
  });
});
