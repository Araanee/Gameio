describe('Liste des jeux', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('affiche la page d\'accueil avec le titre GameBoard', () => {
    cy.get('h1').contains('GameBoard');
  });

  it('affiche les champs de recherche', () => {
    cy.get('input[placeholder*="Titre"]').should('exist');
    cy.get('input[placeholder*="Genre"]').should('exist');
    cy.get('input[placeholder*="Année"]').should('exist');
    cy.get('input[placeholder*="joueurs"]').should('exist');
  });

  it('affiche les jeux ou un état vide', () => {
    cy.get('.game-grid, .empty-state').should('exist');
  });

  it('affiche les contrôles de tri', () => {
    cy.get('select').should('exist');
  });

  it('permet de rechercher par titre', () => {
    cy.get('input[placeholder*="Titre"]').type('Catan');
    cy.get('button').contains('Rechercher').click();
    cy.get('.game-grid, .empty-state').should('exist');
  });

  it('navigue vers la fiche détaillée en cliquant sur "Voir la fiche"', () => {
    cy.get('.game-card').first().within(() => {
      cy.get('a').contains('Voir la fiche').click();
    });
    cy.url().should('match', /\/games\/\d+/);
  });
});
