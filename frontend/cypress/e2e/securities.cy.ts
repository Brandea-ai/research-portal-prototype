describe('Securities / Wertschriften', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/securities');
  });

  it('should display page title Wertschriften', () => {
    cy.get('.page__title').should('contain.text', 'Wertschriften');
  });

  it('should display securities table with data', () => {
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
  });

  it('should display securities count in header', () => {
    cy.get('.page__count').should('contain.text', 'Titel');
  });

  it('should show ticker and name columns', () => {
    cy.get('.data-table tbody tr').first().within(() => {
      cy.get('.cell-ticker').should('exist');
      cy.get('.cell-name').should('exist');
    });
  });

  it('should filter by sector', () => {
    cy.get('#filter-sector').select('Healthcare');
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
    cy.get('.data-table tbody tr').each(($row) => {
      cy.wrap($row).should('contain.text', 'Healthcare');
    });
  });

  it('should show active filter count when filtering', () => {
    cy.get('#filter-sector').select('Healthcare');
    cy.get('.filter-count').should('contain.text', 'Filter aktiv');
  });

  it('should reset filters', () => {
    cy.get('#filter-sector').select('Healthcare');
    cy.get('.filter-reset').click();
    cy.get('.filter-count').should('not.exist');
  });

  it('should search for a security by ticker', () => {
    cy.get('#filter-search').type('NESN');
    cy.wait(500);
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
    cy.get('.cell-ticker').first().should('contain.text', 'NESN');
  });

  it('should search for a security by name', () => {
    cy.get('#filter-search').type('Novartis');
    cy.wait(500);
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
    cy.get('.cell-name').first().should('contain.text', 'Novartis');
  });

  it('should sort by market cap when clicking column header', () => {
    cy.get('.data-table thead th').contains('Marktkapitalisierung').click();
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
  });

  it('should sort by name when clicking column header', () => {
    cy.get('.data-table thead th').contains('Name').click();
    cy.get('.data-table tbody tr').should('have.length.greaterThan', 0);
  });

  it('should show empty state when search has no results', () => {
    cy.get('#filter-search').type('XXXXXXXXX');
    cy.wait(500);
    cy.get('.empty-state').should('contain.text', 'Keine Wertschriften gefunden');
  });
});
