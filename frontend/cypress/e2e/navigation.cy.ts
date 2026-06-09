describe('Navigation générale', () => {
  it('la page 404 redirige vers l\'accueil', () => {
    cy.visit('/cette-page-nexiste-pas');
    cy.url().should('eq', Cypress.config('baseUrl') + '/');
  });

  it('le lien de navigation vers l\'accueil fonctionne', () => {
    cy.visit('/login');
    cy.visit('/');
    cy.url().should('eq', Cypress.config('baseUrl') + '/');
  });
});
