describe('Research Reports', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/reports');
  });

  it('should display reports table with data', () => {
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
  });

  it('should display all 8 table columns', () => {
    cy.get('.data-table thead th').should('have.length', 8);
  });

  it('should show report count in header', () => {
    cy.get('.page__count').should('contain.text', 'Reports');
  });

  it('should filter reports by type', () => {
    cy.get('#filter-type').select('UPDATE');
    cy.get('.data-table tbody tr').each(($row) => {
      cy.wrap($row).find('.type-badge').should('contain.text', 'Update');
    });
  });

  it('should filter reports by rating', () => {
    cy.get('#filter-rating').select('BUY');
    cy.get('.data-table tbody tr').each(($row) => {
      cy.wrap($row).find('.rating').should('contain.text', 'Buy');
    });
  });

  it('should search reports by title', () => {
    cy.get('#filter-search').type('Nestlé');
    cy.wait(500);
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
    cy.get('.data-table tbody tr').first().should('contain.text', 'Nestlé');
  });

  it('should show active filter count and reset button', () => {
    cy.get('#filter-type').select('UPDATE');
    cy.get('.filter-count').should('contain.text', 'Filter aktiv');
    cy.get('.filter-reset').should('contain.text', 'Filter zurücksetzen');
  });

  it('should reset filters when clicking reset button', () => {
    cy.get('#filter-type').select('UPDATE');
    cy.get('.filter-reset').click();
    cy.get('.filter-count').should('not.exist');
  });

  it('should navigate to report detail on row click', () => {
    cy.get('.data-table tbody tr').first().click();
    cy.url().should('match', /\/reports\/\d+/);
    cy.get('.detail-title').should('exist');
  });

  it('should display report detail with rating and target price', () => {
    cy.get('.data-table tbody tr').first().click();
    cy.get('.rating-section').should('exist');
    cy.get('.section-label').contains('Empfehlung');
    cy.get('.section-label').contains('Kursziel');
    cy.get('.price-value').should('contain.text', 'CHF');
  });

  it('should navigate to create form via button', () => {
    cy.get('.btn-create').contains('Neuer Report').click();
    cy.url().should('include', '/reports/new');
  });

  it('should navigate back from detail to reports list', () => {
    cy.get('.data-table tbody tr').first().click();
    cy.get('.back-link').contains('Zurück zur Übersicht').click();
    cy.url().should('include', '/reports');
    cy.url().should('not.match', /\/reports\/\d+/);
  });

  it('should sort by date when clicking date column', () => {
    cy.get('.data-table thead th').contains('Datum').click();
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
  });
});
