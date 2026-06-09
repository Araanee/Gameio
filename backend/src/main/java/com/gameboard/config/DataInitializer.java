package com.gameboard.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.gameboard.game.BoardGame;
import com.gameboard.game.BoardGameRepository;
import com.gameboard.user.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserService userService;
    private final BoardGameRepository boardGameRepository;

    @Override
    public void run(ApplicationArguments args) {
        userService.initDefaultUsers();
        initDefaultGames();
    }

    private void initDefaultGames() {
        List<BoardGame> games = List.of(
            game("Catan", "Colonisez une île en collectant et échangeant des ressources pour construire routes, villages et villes.", "https://upload.wikimedia.org/wikipedia/en/a/a3/Catan-2015-boxart.jpg", "Stratégie", 3, 4, 1995),
            game("Ticket to Ride", "Reliez des villes à travers l'Europe en posant des wagons sur des routes ferroviaires.", "https://upload.wikimedia.org/wikipedia/en/9/92/Ticket_to_Ride_Board_Game_Box_EN.jpg", "Famille", 2, 5, 2004),
            game("Pandemic", "Coopérez pour stopper quatre maladies qui se répandent sur la planète avant qu'il ne soit trop tard.", "https://upload.wikimedia.org/wikipedia/en/2/2a/PandemicBoard.jpg", "Coopératif", 2, 4, 2008),
            game("Les Aventuriers du Rail", "Parcourez l'Amérique du Nord en construisant des lignes de train pour relier les grandes villes.", "https://upload.wikimedia.org/wikipedia/en/9/92/Ticket_to_Ride_Board_Game_Box_EN.jpg", "Famille", 2, 5, 2004),
            game("7 Wonders", "Développez votre civilisation antique en construisant des merveilles et en gérant votre économie.", "https://upload.wikimedia.org/wikipedia/en/7/7c/7_wonders_board_game_cover.jpg", "Stratégie", 2, 7, 2010),
            game("Agricola", "Gérez une ferme médiévale, nourrissez votre famille et développez vos terres.", "https://upload.wikimedia.org/wikipedia/en/f/f4/Agricola_board_game_box_cover.jpg", "Gestion", 1, 5, 2007),
            game("Dominion", "Construisez votre deck de cartes pour dominer le royaume en achetant actions et trésors.", "https://upload.wikimedia.org/wikipedia/en/7/7f/Dominion_game_cover.jpg", "Cartes", 2, 4, 2008),
            game("Carcassonne", "Posez des tuiles pour construire un paysage médiéval et déployez vos partisans pour marquer des points.", "https://upload.wikimedia.org/wikipedia/en/a/a8/Carcassonne_game.jpg", "Famille", 2, 5, 2000),
            game("Wingspan", "Attirez des oiseaux dans vos réserves naturelles pour créer un moteur de jeu puissant.", "https://upload.wikimedia.org/wikipedia/en/d/db/Wingspan_board_game.jpg", "Stratégie", 1, 5, 2019),
            game("Azul", "Carrelez le mur du palais royal en collectant des tuiles colorées avec élégance.", "https://upload.wikimedia.org/wikipedia/en/9/9e/Azul_board_game_box_cover.jpg", "Abstrait", 2, 4, 2017),
            game("Root", "Jouez des factions asymétriques en guerre pour le contrôle d'une forêt.", "https://upload.wikimedia.org/wikipedia/en/5/51/Root_board_game_cover.jpg", "Stratégie", 2, 4, 2018),
            game("Gloomhaven", "Incarnez des mercenaires dans un donjon coopératif avec une campagne narrative profonde.", "https://upload.wikimedia.org/wikipedia/en/3/3e/Gloomhaven_box_cover.jpg", "Coopératif", 1, 4, 2017),
            game("Terraforming Mars", "Transformez la planète rouge en colonisant ses territoires, gérant eau, température et oxygène.", "https://upload.wikimedia.org/wikipedia/en/f/fc/Terraforming_Mars_board_game_box_cover.jpg", "Stratégie", 1, 5, 2016),
            game("Betrayal at House on the Hill", "Explorez une maison hantée jusqu'à ce qu'un traître se révèle parmi vous.", "https://upload.wikimedia.org/wikipedia/en/0/09/Betrayal_at_house_on_the_hill_cover.jpg", "Horreur", 3, 6, 2004),
            game("Splendor", "Collectez des gemmes pour acheter des cartes et attirer des nobles dans ce jeu de développement.", "https://upload.wikimedia.org/wikipedia/en/2/2e/Splendor_board_game_cover.jpg", "Stratégie", 2, 4, 2014),
            game("Dixit", "Laissez libre cours à votre imagination en décrivant des illustrations poétiques et surréalistes.", "https://imgs.search.brave.com/CiQ4rfMFs6_dXtXeCN8t-gGjhFQbWJmUu1YvdBEOTbU/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzE1Y3BBLXJiR0wu/anBn", "Ambiance", 3, 6, 2008),
            game("Codenames", "Deux équipes s'affrontent pour identifier leurs agents secrets à partir d'indices en un mot.", "https://imgs.search.brave.com/-xhdADHrx6qGhcQULKYhf-qKpMXlKo5i-q2QSXdOT8Q/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/cGljY29sb21vbmRv/dG95cy5jb20vY2Ru/L3Nob3AvcHJvZHVj/dHMvdGd0Z19obzE3/Z2FtZV9jZ2UwMDAz/Nl8xMDAweDEwMDAu/anBnP3Y9MTY2MTg0/NTExOQ", "Ambiance", 2, 8, 2015),
            game("Mysterium", "Un fantôme communique avec des médiums via des visions oniriques pour résoudre un meurtre.", "https://imgs.search.brave.com/jelmja4kRimtp56GfP9o1KnZP5TW0Huc8M2aKF2nhXY/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/bWF6aXBseS5jb20v/Y2RuL3Nob3AvcHJv/ZHVjdHMvTXlzdGVy/aXVtR2FtZS1tYWlu/LmpwZz92PTE3NjI0/NDU1NzUmd2lkdGg9/MTYwMA", "Coopératif", 2, 7, 2015),
            game("7 Wonders Duel", "Affrontez un adversaire unique dans un duel de civilisations antiques.", "https://imgs.search.brave.com/DOWeQSv4DlW1RDN-oui0IoEyWjFHiYAkN2INH8_oRRs/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/bHVkdW0uZnIvNDI4/MDAtbWVkaXVtX2Rl/ZmF1bHQvNy13b25k/ZXJzLWR1ZWwuanBn", "Stratégie", 2, 2, 2015),
            game("Brass Birmingham", "Construisez un réseau industriel dans l'Angleterre du 18ème siècle.", "https://imgs.search.brave.com/571bc5NqenBdFJJxe7t1__yiBZuhm4z4CHrm_zyiDPw/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL21CRUFB/ZVN3cEdsb2lKTWEv/cy1sMjI1LmpwZw", "Stratégie", 2, 4, 2018),
            game("Spirit Island", "Des esprits de la nature s'unissent pour repousser les colonisateurs de leur île sacrée.", "https://imgs.search.brave.com/5l95lwFt4smbunTwxv1c8w4UJ3oFCiA032AvCt0Uk9M/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nLzl5SUFB/ZVN3bTBkcG5vaFgv/cy1sMjI1LmpwZw", "Coopératif", 1, 4, 2017),
            game("Everdell", "Construisez une cité peuplée de créatures des bois dans un cadre féerique.", "https://imgs.search.brave.com/irxqoA6KDlQJqlZ8dcJNPYkfQ-C_0GP821iEPma1MgM/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9mLnRh/YmxldG9waWEuY29t/L2MvMDAxLzY3MC85/enBNalF6NXRkQVRG/QjQzYmRZY3NBLnBu/Zz93aWR0aD0yMzAm/YXV0b19vcHRpbWl6/ZT1sb3cmc2NhbGU9/Ym90aCZtb2RlPWNy/b3A", "Gestion", 1, 4, 2018),
            game("Viticulture", "Gérez un vignoble en Toscane et produisez le meilleur vin de la région.", "https://imgs.search.brave.com/hXR6GaiaGwq5CgU1JkdymJ1wmTSYrv3HaAT_NkPoRnM/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NTFpOGp5LWpXS0wu/anBn", "Gestion", 2, 6, 2013),
            game("Concordia", "Développez un réseau commercial à travers l'Empire romain en envoyant vos colons.", "https://imgs.search.brave.com/ccj4B6jYb-RZ12TZVXfqdNc3XxYqjgBEHS8FKBvhoEY/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/ODE5dWt3UHBFSkwu/anBn", "Stratégie", 2, 5, 2013),
            game("Lost Ruins of Arnak", "Explorez une île mystérieuse, découvrez des artefacts et affrontez des gardiens anciens.", "https://imgs.search.brave.com/O4JXthCAsJoNsIwbIoMMxgYM1ZfQYUnctH64ibL-dOA/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL1Mv/YXBsdXMtbWVkaWEt/bGlicmFyeS1zZXJ2/aWNlLW1lZGlhL2I2/M2ZhYTE0LTNlMDUt/NGE0Mi05NDAwLTZl/MzczODE0MTlkYy5f/X0NSMCwwLDY5Nywx/MDAwX1BUMF9TWDQ4/OF9WMV9fXy5wbmc", "Stratégie", 1, 4, 2020),
            game("Scythe", "Conquérez un monde diesel-punk d'Europe de l'Est en gérant ressources, combat et diplomatie.", "https://imgs.search.brave.com/kFmw0HDgRYLLeojjckUEL5Qq7O6mHoVqulKqR7wGsYU/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/bGUtcGFzc2UtdGVt/cHMuY29tLzIxNDQx/LW1lZGl1bV9kZWZh/dWx0L3NjeXRoZS5q/cGc", "Stratégie", 1, 5, 2016),
            game("King of Tokyo", "Incarnez des monstres géants qui se battent pour le contrôle de Tokyo dans ce jeu de dés festif.", "https://imgs.search.brave.com/DXJVoTuFpQ4924bWzdb5jM8z33_temoxq2SGrQDRsfw/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/ODFaM3BocHRsT0wu/anBn", "Famille", 2, 6, 2011),
            game("Coup", "Bluffez et éliminez vos adversaires dans ce jeu de déduction et de trahison en seulement 15 minutes.", "https://imgs.search.brave.com/4ZZKuwzGr3RX3wWH2f9ZFfNj3cmLvBzki-e0FFrOF2s/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NDE1Tm5lOGxPSEwu/anBn", "Ambiance", 2, 6, 2012),
            game("Sushi Go!", "Collectez les meilleures combinaisons de sushis en passant vos cartes à la ronde.", "https://imgs.search.brave.com/VmDq9fh3xXzGqJqMA7XDCS_bj04_4axRbG4RvPCM7zk/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzF3RVBSWWRwZEwu/anBn", "Famille", 2, 5, 2013),
            game("Patchwork", "Confectionnez le plus beau patchwork en récupérant des pièces de tissu sur un plateau circulaire.", "https://imgs.search.brave.com/lvsJV2q1YIittKGQ4dpxteVdHF1NGa2No_YdCm691dM/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NjE5RG5KZGJDZEwu/anBn", "Abstrait", 2, 2, 2014),
            game("Cascadia", "Construisez un écosystème naturel en plaçant des tuiles paysage et des jetons animaux.", "https://imgs.search.brave.com/vcyDRZesAFly_eKubPDZuaqY6cY9L4mqid460zrvcKM/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pMC53/cC5jb20vd3d3LnRo/ZWJvYXJkZ2FtZWZh/bWlseS5jb20vd3At/Y29udGVudC91cGxv/YWRzLzIwMjQvMDIv/Q2FzY2FkaWFfQWxs/LTUyNXg0ODcuanBn/P3Jlc2l6ZT01MjUs/NDg3", "Famille", 1, 4, 2021),
            game("Arkham Horror", "Affrontez des horreurs lovecraftiennes pour sceller les portails interdimensionnels avant qu'il ne soit trop tard.", "https://imgs.search.brave.com/mLMGXL6nUYlZ7Pls97S6jzY3A_mXe-Ycp7Tcb9yGzoI/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/Z2FtaW5nbGliLmNv/bS9jZG4vc2hvcC9w/cm9kdWN0cy9hcmto/YW0taG9ycm9yLXRo/ZS1jYXJkLWdhbWUt/ZWRnZS1vZi10aGUt/ZWFydGgtaW52ZXN0/aWdhdG9yLWV4cGFu/c2lvbi05ODAxOTEu/anBnP3Y9MTcwMDE5/MjIwOSZ3aWR0aD01/MDA", "Coopératif", 1, 8, 2005),
            game("Dead of Winter", "Survivez à l'hiver zombie en gérant votre colonie, mais méfiez-vous des traîtres parmi vous.", "https://imgs.search.brave.com/JuNTaK_mMmLv2h_Yi-7E30hfmXHbDQx8zG8c8SQCf5E/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL05iOEFB/ZVN3QTFCcFBMNWYv/cy1sNDAwLndlYnA", "Coopératif", 2, 5, 2014),
            game("Sheriff of Nottingham", "Passez en contrebande des marchandises illicites en bluffant le shérif dans ce jeu de négociation.", "https://imgs.search.brave.com/ItIjtygEmyvmKsmIkhlRs2g12w9RywP0C8Hjf6RX0vE/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL3h5QUFB/ZVN3OX54cEcyYVgv/cy1sMjI1LmpwZw", "Ambiance", 3, 5, 2014),
            game("Star Realms", "Construisez votre flotte spatiale et détruisez la base adverse dans ce duel de deck-building.", "https://imgs.search.brave.com/FvlaHG73KbayUQ-rolNei8WDhLb_ffkksTLdDbUCon4/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9sZXNn/ZW50bGVtZW5kdWpl/dS5jb20vMzE2Mi1t/ZWRpdW1fZGVmYXVs/dC9zdGFyLXJlYWxt/cy5qcGc", "Cartes", 2, 2, 2014),
             game("Twilight Imperium", "Dominez la galaxie en gérant diplomatie, commerce et batailles spatiales épiques sur plusieurs heures.", "https://imgs.search.brave.com/wUZMICxANoFOcpQBN3Y3pN5j04inupmWyYy2wFPJ0Wc/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nLzlZWUFB/ZVN3MXFabzF4UTgv/cy1sMjI1LmpwZw", "Stratégie", 3, 6, 2017),
            game("Robinson Crusoe", "Survivez sur une île déserte en construisant un abri, cherchant de la nourriture et affrontant des dangers.", "https://imgs.search.brave.com/_oBJIwZ34Pvi70_ou98KGKTABPt0ni2MZOO2AWjCNlo/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL0FESUFB/T1N3cnNObktQdlQv/cy1sNTAwLmpwZw", "Coopératif", 1, 4, 2012),
            game("Pandemic Legacy Saison 1", "Campagne coopérative où vos décisions modifient le plateau et l'histoire au fil des parties.", "https://imgs.search.brave.com/YDMo4a8sVdgr2kJG9kFyhj2QF5QjPklNMwhHeFFXvak/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NTFDTWJ5QW1nZEwu/anBn", "Coopératif", 2, 4, 2015),
            game("Sagrada", "Créez un vitrail en drafant des dés colorés tout en respectant des contraintes de placement.", "https://imgs.search.brave.com/vw7cVW1zjqGP9AMFhRzwxiiYqcSXmFC4M7avDuq6kuk/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NTFXT1F5VTArZEwu/anBn", "Abstrait", 1, 4, 2017),
            game("Cartographers", "Dessinez des cartes de territoire en remplissant votre feuille selon les formes révélées à chaque tour.", "https://imgs.search.brave.com/Y8HZEPpGMEtPeJy4-3kmkdA6QVIvFtVujZkByZ5dhus/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nLzVNY0FB/ZVN3dEdCcEpsd3ov/cy1sNTAwLmpwZw", "Stratégie", 1, 100, 2019),
            game("Welcome To...", "Construisez le quartier résidentiel américain idéal en cochant des cases sur votre feuille de jeu.", "https://imgs.search.brave.com/LgYpy9cTSGZ2_J9PEAueZ8WBQLtESyaGq3qbgGP-kgo/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/dGhlZ2FtZXN0ZXdh/cmQuY29tL2Nkbi9z/aG9wL3Byb2R1Y3Rz/L3dlbGNvbWUtdG8t/b3V0YnJlYWstdGhl/bWF0aWMtbmVpZ2hi/b3Jvb2QtZXhwYW5z/aW9uLXJldGFpbC1w/cmUtb3JkZXItZWRp/dGlvbi1yZXRhaWwt/Ym9hcmQtZ2FtZS1l/eHBhbnNpb24tZGVl/cC13YXRlci1nYW1l/cy0zODE3MjgyNTIy/MzMyMF8yMDAweC5q/cGc_dj0xNjQzMjg2/MDEw", "Famille", 1, 100, 2018),
            game("Isle of Skye", "Achetez et vendez des tuiles paysage pour bâtir le royaume écossais le plus prospère.", "https://imgs.search.brave.com/vdXH1mAbbkoOl3p6aH_S2vVNC3y-W4JrDmH_I__rkNY/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/amV1eGRlbmltLmJl/L2ltYWdlcy9qZXV4/L0lzbGVPZlNreWVf/bGFyZ2UwMS5qcGc", "Stratégie", 2, 5, 2015),
            game("Tokaido", "Voyagez le long de la route côtière japonaise en collectant souvenirs, rencontres et panoramas.", "https://imgs.search.brave.com/uSSfrXeA9e4MO8NqusEZjXHd4pIQa0DUIp6JNUjF-7I/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tZWRp/YS5wbGF5LWluLmNv/bS9pbWcvcHJvZHVj/dC90b2thaWRvLWR1/by1ib2l0ZS1kZS1q/ZXUuanBn", "Famille", 2, 5, 2012),
            game("Clank!", "Infiltrez le donjon d'un dragon pour voler des artefacts sans faire trop de bruit.", "https://imgs.search.brave.com/dVazvk1DejhT9xwVixFL1zHlZkuTyVrAykn_bzWAClg/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pMC53/cC5jb20vZ3VzYW5k/Y28ubmV0L3dwLWNv/bnRlbnQvdXBsb2Fk/cy8yMDI0LzA4L2Ns/YW5rLXAtaW1hZ2Ut/OTUzNTUtZ3JhbmRl/LmpwZz9yZXNpemU9/OTYwLDEwMDAmc3Ns/PTE", "Aventure", 2, 4, 2016),
            game("Ethnos", "Recrutez des créatures fantastiques et contrôlez des régions dans ce jeu d'area control rapide.", "https://imgs.search.brave.com/pEe_d0CU-xn_i4oBXmv7A9WaGFJ_ed0UXV8P_a9d5TU/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9jZG4u/Y2FyZHNyZWFsbS5j/b20vaW1hZ2VzL2Nh/cnRhcy9lbi9pbi1m/cm9udC9ldGhub3Mt/MS1tZWQucG5nPzY1/Njk_JndpZHRoPTI1/MA", "Stratégie", 2, 6, 2017),
            game("Flamecraft", "Placez des dragons artisans dans les boutiques d'une ville magique pour améliorer leur production.", "https://imgs.search.brave.com/JcFpVEEg26BkIolozFKsxhYJrQYrETne7U_oiOhZ0rY/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pbWFn/ZXMtbmEuc3NsLWlt/YWdlcy1hbWF6b24u/Y29tL2ltYWdlcy9J/LzYxV0NFV1dLVXBM/LmpwZw", "Famille", 1, 5, 2022),
            game("Oceans", "Évoluez des espèces marines en développant des traits uniques dans un écosystème en perpétuel changement.", "https://imgs.search.brave.com/btosqNePd5p7JsSqchuAHHPuGU2zILBcBWdsAM8weuA/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/OTFQSGFTdFZkWkwu/anBn", "Stratégie", 2, 4, 2020),
            game("Sleeping Gods", "Naviguez dans un monde fantastique ouvert à la recherche de totems pour rentrer chez vous.", "https://imgs.search.brave.com/nTbrS1Uu4H8jbc8WPK1iF2CCBovG0uZzBc2P00y1hNg/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzFzMFVaQm5tWUwu/anBn", "Aventure", 1, 4, 2021),
            game("Oath", "Reecrivez l'histoire d'un empire a travers des parties qui s'influencent les unes les autres.", "https://imgs.search.brave.com/suZ1BBoMkyfovdFog3BPyycbuB0JNOB_9Ub53J9i0Wk/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9pLmVi/YXlpbWcuY29tL2lt/YWdlcy9nL0xVWUFB/ZVN3dGlCcHRiRkcv/cy1sMjI1LmpwZw", "Strategie", 1, 6, 2021)
        );

        for (BoardGame g : games) {
            var existing = boardGameRepository.findByTitleContainingIgnoreCase(g.getTitle());
            if (existing.isEmpty()) {
                boardGameRepository.save(g);
            } else {
                existing.stream()
                    .filter(e -> e.getStatus() != BoardGame.Status.APPROVED && "admin".equals(e.getProposedBy()))
                    .forEach(e -> { e.setStatus(BoardGame.Status.APPROVED); boardGameRepository.save(e); });
            }
        }
    }

    private BoardGame game(String title, String description, String imageUrl, String genre, int min, int max, int year) {
        BoardGame g = new BoardGame();
        g.setTitle(title);
        g.setDescription(description);
        g.setImageUrl(imageUrl);
        g.setGenre(genre);
        g.setMinPlayers(min);
        g.setMaxPlayers(max);
        g.setYear(year);
        g.setStatus(BoardGame.Status.APPROVED);
        g.setProposedBy("admin");
        return g;
    }
}
