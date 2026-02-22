/// <reference types="cypress" />

declare namespace Cypress {
  interface Chainable {
    login(username?: string, password?: string): Chainable<void>;
  }
}

Cypress.Commands.add('login', (username = 'analyst', password = 'analyst') => {
  cy.visit('/login');
  cy.get('input[formControlName="username"]').type(username);
  cy.get('input[formControlName="password"]').type(password);
  cy.get('button[type="submit"]').click();
  cy.url().should('include', '/dashboard');
});
