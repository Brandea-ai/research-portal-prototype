describe('Dashboard', () => {
  beforeEach(() => {
    cy.login();
  });

  it('should display 4 KPI cards', () => {
    cy.get('.kpi-card').should('have.length', 4);
  });

  it('should display correct KPI labels', () => {
    cy.get('.kpi-card__label').eq(0).should('contain.text', 'Reports');
    cy.get('.kpi-card__label').eq(1).should('contain.text', 'Wertschriften');
    cy.get('.kpi-card__label').eq(2).should('contain.text', 'Analysten');
    cy.get('.kpi-card__label').eq(3).should('contain.text', 'Rating-Änderungen');
  });

  it('should display KPI values as numbers', () => {
    cy.get('.kpi-card__value').each(($el) => {
      const text = $el.text().trim();
      expect(Number(text)).to.be.a('number');
      expect(Number(text)).to.be.greaterThan(0);
    });
  });

  it('should display latest reports section', () => {
    cy.get('.section__title').contains('Neueste Reports');
    cy.get('.report-row').should('have.length.greaterThan', 0);
  });

  it('should display report rows with title and rating', () => {
    cy.get('.report-row').first().within(() => {
      cy.get('.report-row__title').should('exist');
      cy.get('.report-row__rating').should('exist');
      cy.get('.report-row__target').should('contain.text', 'CHF');
    });
  });

  it('should display top analysts section', () => {
    cy.get('.section__title').contains('Top Analysten');
    cy.get('.analyst-card').should('have.length.greaterThan', 0);
  });

  it('should display analyst cards with name and accuracy', () => {
    cy.get('.analyst-card').first().within(() => {
      cy.get('.analyst-card__name').should('exist');
      cy.get('.analyst-card__accuracy').should('exist');
    });
  });

  it('should display coverage overview section', () => {
    cy.get('.section__title').contains('Coverage-Übersicht');
    cy.get('.coverage-stat').should('have.length', 3);
  });
});
