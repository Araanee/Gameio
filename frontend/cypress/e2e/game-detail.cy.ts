describe('Fiche détaillée d\'un jeu', () => {
  beforeEach(() => {
    cy.visit('/');
    cy.get('.game-card').first().within(() => {
      cy.get('a').contains('Voir la fiche').click();
    });
  });

  it('affiche le titre du jeu', () => {
    cy.get('h1').should('exist');
  });

  it('affiche la section notation', () => {
    cy.get('.rating-section, .ratings-section, .note-section, form').should('exist');
  });

  it('l\'URL contient l\'id du jeu', () => {
    cy.url().should('match', /\/games\/\d+/);
  });
});
