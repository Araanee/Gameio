describe('Authentification & routes protégées', () => {
  it('affiche la page de login', () => {
    cy.visit('/login');
    cy.get('input[type="text"], input[name="username"]').should('exist');
    cy.get('input[type="password"]').should('exist');
  });

  it('redirige vers /login si on accède à /admin sans être connecté', () => {
    cy.visit('/admin');
    cy.url().should('include', '/login');
  });

  it('redirige vers /login si on accède à /propose sans être connecté', () => {
    cy.visit('/propose');
    cy.url().should('include', '/login');
  });

  it('redirige vers /login si on accède à /my-proposals sans être connecté', () => {
    cy.visit('/my-proposals');
    cy.url().should('include', '/login');
  });

  it('se connecte en tant qu\'admin et accède au dashboard', () => {
    cy.intercept('GET', '**/api/auth/me', { username: 'admin', role: 'WEBMASTER' }).as('authMe');
    cy.visit('/login');
    cy.get('input[type="text"]').first().type('admin');
    cy.get('input[type="password"]').type('admin');
    cy.get('button.btn-login').click();
    cy.wait('@authMe');
    cy.url().should('not.include', '/login');
  });
});
