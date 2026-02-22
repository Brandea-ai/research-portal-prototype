describe('Authentication', () => {
  it('should redirect unauthenticated users to login', () => {
    cy.visit('/dashboard');
    cy.url().should('include', '/login');
  });

  it('should login with valid credentials', () => {
    cy.login('analyst', 'analyst');
    cy.url().should('include', '/dashboard');
  });

  it('should show error for invalid credentials', () => {
    cy.visit('/login');
    cy.get('input[formControlName="username"]').type('wrong');
    cy.get('input[formControlName="password"]').type('wrong');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/login');
    cy.get('.login__error').should('contain.text', 'ungültig');
  });

  it('should disable submit when form is invalid', () => {
    cy.visit('/login');
    cy.get('input[formControlName="username"]').type('ab');
    cy.get('input[formControlName="password"]').type('ab');
    cy.get('input[formControlName="password"]').blur();
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should logout and redirect to login', () => {
    cy.login();
    cy.get('.topbar__logout').contains('Abmelden').click();
    cy.url().should('include', '/login');
  });

  it('should login as admin with valid credentials', () => {
    cy.login('admin', 'admin');
    cy.url().should('include', '/dashboard');
  });
});
