import java.util.ArrayList;
import java.util.Arrays;

public class algo {

    // Constante
    private static int niveauMax;
    public static int joueurXO;
    private static ArrayList<position> listPlateau = new ArrayList<position>();

    public static position trouverCoups(int[][] grid, int XO, int coupAdvX, int coupAdvY) {
        joueurXO = XO;
        init(grid);
        position coup = meilleurCoup(grid, coupAdvX, coupAdvY);
        System.out.println("Meilleur Position trouver: " + (coup.getX()) + "|" + (coup.getY()) + " Score: "
                + coup.getScore());
        return coup;
    }

    public static int[][] construirePetitPlateau(int[][] board) {

        int adv = 4;
        if (joueurXO == 4) {
            adv = 2;
        }

        int[][] petit = new int[3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                petit[x][y] = 0;
                int eval = evaluationStatiquePlateau(board, ouJouerPetitTab(x), ouJouerPetitTab(y));
                if (eval == 100) {
                    petit[x][y] = joueurXO;
                } else if (eval == -100) {
                    petit[x][y] = adv;
                }
            }
        }
        return petit;
    }

    public static int ouJouerPetitTab(int i) {

        if (i == 0)
            return 1;
        else if (i == 1)
            return 4;
        else if (i == 2)
            return 7;
        return 0;
    }

    private static void init(int[][] grid) {
        listPlateau.add(new position(1, 1));
        listPlateau.add(new position(4, 1));
        listPlateau.add(new position(7, 1));
        listPlateau.add(new position(1, 4));
        listPlateau.add(new position(4, 4));
        listPlateau.add(new position(7, 4));
        listPlateau.add(new position(1, 7));
        listPlateau.add(new position(4, 7));
        listPlateau.add(new position(7, 7));

        int count = countCase0(grid);

        if (count > 70) {
            niveauMax = 5;
        } else if (count > 60) {
            niveauMax = 6;
        } else if (count > 50) {
            niveauMax = 7;
        } else if (count > 40) {
            niveauMax = 8;
        } else if (count > 30) {
            niveauMax = 9;
        } else if (count > 20) {
            niveauMax = 10;
        } else if (count > 10) {
            niveauMax = 11;
        } else if (count > 0) {
            niveauMax = 12;
        }
    }

    public static position meilleurCoup(int[][] grid, int coupAdvX, int coupAdvY) {
        ArrayList<position> coups = genereMouvement(grid, coupAdvX, coupAdvY);
        System.out.println("mouv: " + coups.size());
        // position possible
        for (position pos : coups) {
            System.out.println("Coups: " + pos.getX() + "|" + pos.getY());
            // Nouveau grid
            grid[pos.getX()][pos.getY()] = joueurXO;
            // minMax
            double score = minMax(grid, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, pos.getX(),
                    pos.getY(), niveauMax);
            grid[pos.getX()][pos.getY()] = 0;
            pos.setScore(score);
        }

        return ChoisirMeilleurCoup(grid, coups);
    }

    public static position ChoisirMeilleurCoup(int[][] grid, ArrayList<position> coups) {
        Double bestScore = Double.NEGATIVE_INFINITY;
        position bestPosition = null;
        for (position pos : coups) {

            if (pos.getScore() > bestScore) {
                bestScore = pos.getScore();
                bestPosition = pos;
            }
        }
        return bestPosition;
    }

    public static void printGrid(int[][] grid) {
        System.out.println(Arrays.deepToString(grid).replace("], ", "]\n").replace("[[", "[").replace("]]", "]"));
        System.out.println("");
    }

    public static double minMax(int[][] grid, double alpha, double beta, boolean max, int dernierCoupX,
            int dernierCoupY, int niveau) {

        if (evaluationStatiqueGrid(grid) != 2) {
            return evaluationStatiqueGrid(grid) + niveau;
        }

        if (niveau <= 0) {
            return evaluationStatiqueGridCoups(grid, dernierCoupX, dernierCoupY);

        }

        if (max) { // Si le noeud est un max
            double maxVal = Double.NEGATIVE_INFINITY;
            ArrayList<position> mouvements = genereMouvement(grid, dernierCoupX, dernierCoupY);

            for (position pos : mouvements) {
                // Nouveau grid
                grid[pos.getX()][pos.getY()] = joueurXO;
                Double result = minMax(grid, alpha, beta, false, pos.getX(), pos.getY(), niveau - 1);
                grid[pos.getX()][pos.getY()] = 0;

                maxVal = Double.max(maxVal, result);
                alpha = Double.max(alpha, result);

                if (beta <= alpha) { // Coupure alpha
                    break;
                }
            }

            return new Double(maxVal);
        } else {
            Double minVal = Double.POSITIVE_INFINITY;
            ArrayList<position> mouvements = genereMouvement(grid, dernierCoupX, dernierCoupY);
            for (position pos : mouvements) {
                // Nouveau grid
                if (joueurXO == 2) {
                    grid[pos.getX()][pos.getY()] = 4;
                } else {
                    grid[pos.getX()][pos.getY()] = 2;
                }
                Double result = minMax(grid, alpha, beta, true, pos.getX(), pos.getY(), niveau - 1);
                grid[pos.getX()][pos.getY()] = 0;
                minVal = Double.min(minVal, result);
                beta = Double.min(beta, result);

                if (beta <= alpha) { // Coupure alpha
                    break;
                }
            }

            return new Double(minVal);
        }
    }

    // 9 directions
    // haut-gauche, millieu-gauche, bas-gauche, bas, haut, haut-droit,
    // millieu-droit,
    // bas-droit, millieu
    static int[] x = { -1, -1, -1, 0, 0, 1, 1, 1, 0 };
    static int[] y = { -1, 0, 1, -1, 1, -1, 0, 1, 0 };

    private static ArrayList<position> genereMouvement(int[][] grid, int dernierCoupX, int dernierCoupY) {

        ArrayList<position> mouvements = new ArrayList<position>();

        int xPlateau = ouJouerPlateau(dernierCoupX);
        int yPlateau = ouJouerPlateau(dernierCoupY);

        if (evaluationStatiquePlateau(grid, xPlateau, yPlateau) == 2) {
            for (int direction = 0; direction < 9; direction++) {
                int posX = xPlateau + x[direction];
                int posY = yPlateau + y[direction];
                if (grid[posX][posY] == 0) {
                    mouvements.add(new position(posX, posY));
                }
            }
        } else {
            position plateau = choisirMeilleurPlateau(grid);

            for (int direction = 0; direction < 9; direction++) {
                int posX = plateau.getX() + x[direction];
                int posY = plateau.getY() + y[direction];
                if (grid[posX][posY] == 0) {
                    mouvements.add(new position(posX, posY));
                }
            }
        }

        return mouvements;

    }

    public static int evaluationStatiquePlateau(int[][] grid, int centreX, int centreY) {

        // horizontal
        int xH = centreX - 1;
        int yH = centreY - 1;
        for (int y = 0; y < 3; y++) {
            int score = 1;

            for (int x = 0; x < 3; x++) {
                score = score * grid[xH + x][yH + y];
            }
            if (score == 8) {
                if (joueurXO == 2) {
                    return 100;
                } else {
                    return -100;
                }
            }
            if (score == 64) {
                if (joueurXO == 4) {
                    return 100;
                } else {
                    return -100;
                }
            }
        }

        // vertical
        int xV = centreX - 1;
        int yV = centreY - 1;
        for (int x = 0; x < 3; x++) {
            int score = 1;
            for (int y = 0; y < 3; y++) {
                score = score * grid[xV + x][yV + y];
            }

            if (score == 8) {
                if (joueurXO == 2) {
                    return 100;
                } else {
                    return -100;
                }
            }
            if (score == 64) {
                if (joueurXO == 4) {
                    return 100;
                } else {
                    return -100;
                }
            }
        }

        // diagonal droite
        int xDd = centreX + 1;
        int yDd = centreY - 1;
        int scoreD = 1;
        for (int i = 0; i < 3; i++) {
            scoreD = scoreD * grid[xDd - i][yDd + i];
        }
        if (scoreD == 8) {
            if (joueurXO == 2) {
                return 100;
            } else {
                return -100;
            }
        }
        if (scoreD == 64) {
            if (joueurXO == 4) {
                return 100;
            } else {
                return -100;
            }
        }

        // diagonale gauche
        int xDg = centreX - 1;
        int yDg = centreY - 1;
        int scoreG = 1;
        for (int i = 0; i < 3; i++) {
            scoreG = scoreG * grid[xDg + i][yDg + i];
        }
        if (scoreG == 8) {
            if (joueurXO == 2) {
                return 100;
            } else {
                return -100;
            }
        }
        if (scoreG == 64) {
            if (joueurXO == 4) {
                return 100;
            } else {
                return -100;
            }
        }
        if (matchNul(grid, centreX, centreY)) {
            return 0;
        }

        return 2;
    }

    public static int evaluationStatiqueCoups(int[][] grid, int centreX, int centreY) {

        int pointAdv = 0;
        int pointJoueur = 0;

        int adversaire = 4;
        if (joueurXO == 4) {
            adversaire = 2;
        }

        // horizontal
        int xH = centreX - 1;
        int yH = centreY - 1;
        for (int y = 0; y < 3; y++) {
            ArrayList<Integer> combinaison = new ArrayList<Integer>();
            for (int x = 0; x < 3; x++) {
                combinaison.add(grid[xH + x][yH + y]);
            }

            if (!combinaison.contains(adversaire)) {
                for (int i : combinaison) {
                    if (i == joueurXO)
                        pointJoueur++;
                }
            } else if (!combinaison.contains(joueurXO)) {
                for (int i : combinaison) {
                    if (i == adversaire)
                        pointAdv++;
                }
            }

        }

        // vertical
        int xV = centreX - 1;
        int yV = centreY - 1;
        for (int x = 0; x < 3; x++) {
            ArrayList<Integer> combinaison = new ArrayList<Integer>();
            for (int y = 0; y < 3; y++) {
                combinaison.add(grid[xV + x][yV + y]);
            }
            if (!combinaison.contains(adversaire)) {
                for (int i : combinaison) {
                    if (i == joueurXO)
                        pointJoueur++;
                }
            } else if (!combinaison.contains(joueurXO)) {
                for (int i : combinaison) {
                    if (i == adversaire)
                        pointAdv++;
                }
            }
        }

        // diagonal droite
        int xDd = centreX + 1;
        int yDd = centreY - 1;
        ArrayList<Integer> combinaisonDd = new ArrayList<Integer>();
        for (int i = 0; i < 3; i++) {
            combinaisonDd.add(grid[xDd - i][yDd + i]);
        }
        if (!combinaisonDd.contains(adversaire)) {
            for (int i : combinaisonDd) {
                if (i == joueurXO)
                    pointJoueur++;
            }
        } else if (!combinaisonDd.contains(joueurXO)) {
            for (int i : combinaisonDd) {
                if (i == adversaire)
                    pointAdv++;
            }
        }

        // diagonale gauche
        int xDg = centreX - 1;
        int yDg = centreY - 1;
        ArrayList<Integer> combinaisonDg = new ArrayList<Integer>();
        for (int i = 0; i < 3; i++) {
            combinaisonDg.add(grid[xDg + i][yDg + i]);
        }
        if (!combinaisonDg.contains(adversaire)) {
            for (int i : combinaisonDg) {
                if (i == joueurXO)
                    pointJoueur++;
            }
        } else if (!combinaisonDg.contains(joueurXO)) {
            for (int i : combinaisonDg) {
                if (i == adversaire)
                    pointAdv++;
            }
        }

        return pointJoueur - pointAdv;
    }

    public static int ouJouerPlateau(int i) {

        if (i == 0 || i == 3 || i == 6)
            return 1;
        else if (i == 1 || i == 4 || i == 7)
            return 4;
        else if (i == 2 || i == 5 || i == 8)
            return 7;
        return 0;
    }

    public static double evaluationStatiqueGrid(int[][] grid) {
        int[][] petitTab = construirePetitPlateau(grid);
        return evaluationStatiquePlateau(petitTab, 1, 1);

    }

    public static double evaluationStatiqueGridCoups(int[][] grid, int coupX, int coupY) {
        Double score = 0.0;
        for (position plateau : listPlateau) {
            score = score + evaluationStatiqueCoups(grid, plateau.getX(), plateau.getY());
        }
        return score;

    }

    public static boolean advPeutMarquerEnUnCoup(int[][] grid, int coupX, int coupY) {

        int tabX = ouJouerPlateau(coupX);
        int tabY = ouJouerPlateau(coupY);

        if (evaluationStatiquePlateau(grid, tabX, tabY) == 2) {

            for (int direction = 0; direction < 9; direction++) {
                int posX = tabX + x[direction];
                int posY = tabY + y[direction];

                if (grid[posX][posY] == 0) {
                    if (joueurXO == 2) {
                        grid[posX][posY] = 4;
                    } else {
                        grid[posX][posY] = 2;
                    }

                    if (evaluationStatiquePlateau(grid, tabX, tabY) == -100) {
                        grid[posX][posY] = 0;
                        return true;
                    }

                    grid[posX][posY] = 0;
                }

            }

        }

        return false;

    }

    public static boolean onPeutMarquerEnUnCoup(int[][] grid, int coupX, int coupY) {

        int tabX = ouJouerPlateau(coupX);
        int tabY = ouJouerPlateau(coupY);

        if (evaluationStatiquePlateau(grid, tabX, tabY) == 2) {

            ArrayList<position> mouvements = genereMouvement(grid, tabX, tabY);

            for (position posJouable : mouvements) {
                grid[posJouable.getX()][posJouable.getY()] = joueurXO;

                if (evaluationStatiquePlateau(grid, tabX, tabX) == 100) {
                    grid[posJouable.getX()][posJouable.getY()] = 0;
                    return true;
                }
                grid[posJouable.getX()][posJouable.getY()] = 0;

            }
        }

        return false;

    }

    private static boolean matchNul(int[][] grid, int centreX, int centreY) {

        int count = 0;
        for (int direction = 0; direction < 9; direction++) {
            int posX = centreX + x[direction];
            int posY = centreY + y[direction];
            if (grid[posX][posY] == 0) {
                count++;
            }
        }

        if (count == 0) {
            return true;
        }
        return false;

    }

    private static int countCase0(int[][] grid) {

        int nbr = 0;
        for (int posX = 0; posX < 9; posX++) {
            for (int posY = 0; posY < 9; posY++) {
                if (grid[posX][posY] == 0)
                    nbr++;
            }
        }
        return nbr;

    }

    private static position choisirMeilleurPlateau(int[][] board) {

        ArrayList<position> list = new ArrayList<position>();
        for (position temp : listPlateau) {
            if(evaluationStatiquePlateau(board, temp.getX(), temp.getY()) == 2)
                list.add(new position(temp.getX(), temp.getY()));
        }

        int[][] petitTab = construirePetitPlateau(board);

        ArrayList<position> coups = genereMouvementPlateau(petitTab, 1, 1);
        // position possible
        for (position pos : coups) {
            // Nouveau grid
            petitTab[pos.getX()][pos.getY()] = joueurXO;
            // minMax
            double score = minMaxPlateau(petitTab, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true, 1, 1,
                    8);
            petitTab[pos.getX()][pos.getY()] = 0;
            pos.setScore(score);
        }

        for (position pos : coups) {
            for (position plateau : list) {
                if (ouJouerPetitTab(plateau.getX()) == pos.getX() && ouJouerPetitTab(plateau.getY()) == pos.getY()) {
                    plateau.setScore(plateau.getScore() + pos.getScore());
                }
            }
        }

        position bestPosition = null;
        double bestScore = Double.POSITIVE_INFINITY;
        for (position plateau : list) {
            if (plateau.getScore() < bestScore) {
                bestPosition = plateau;
            }
        }
        if(bestPosition == null){
        double bestScore1 = Double.NEGATIVE_INFINITY;
        for (position plateau : list) {
            if (plateau.getScore() > bestScore1) {
                bestPosition = plateau;
            }
        }
        }
        
        return bestPosition;
    }

    public static double minMaxPlateau(int[][] grid, double alpha, double beta, boolean max, int centreX, int centreY,
            int niveau) {

        if (evaluationStatiquePlateau(grid, centreX, centreY) != 2) {
            double s = evaluationStatiquePlateau(grid, centreX, centreY);

            if (s < 0) {
                return new Double(s) - niveau;
            }
            return new Double(s) + niveau;

        }
        if (niveau <= 0) {
            return evaluationStatiqueCoups(grid, centreX, centreY);
        }

        if (max) { // Si le noeud est un max
            double maxVal = Double.NEGATIVE_INFINITY;
            ArrayList<position> mouvements = genereMouvementPlateau(grid, centreX, centreY);

            for (position pos : mouvements) {
                // Nouveau grid
                grid[pos.getX()][pos.getY()] = joueurXO;
                Double result = minMaxPlateau(grid, alpha, beta, false, centreX, centreY, niveau - 1);
                grid[pos.getX()][pos.getY()] = 0;

                maxVal = Double.max(maxVal, result);
                alpha = Double.max(alpha, result);

                if (beta <= alpha) { // Coupure alpha
                    break;
                }
            }

            return new Double(maxVal);
        } else {
            Double minVal = Double.POSITIVE_INFINITY;
            ArrayList<position> mouvements = genereMouvementPlateau(grid, centreX, centreY);
            for (position pos : mouvements) {
                // Nouveau grid
                if (joueurXO == 2) {
                    grid[pos.getX()][pos.getY()] = 4;
                } else {
                    grid[pos.getX()][pos.getY()] = 2;
                }
                Double result = minMaxPlateau(grid, alpha, beta, true, centreX, centreY, niveau - 1);
                grid[pos.getX()][pos.getY()] = 0;
                minVal = Double.min(minVal, result);
                beta = Double.min(beta, result);

                if (beta <= alpha) { // Coupure alpha
                    break;
                }
            }

            return new Double(minVal);
        }
    }

    private static ArrayList<position> genereMouvementPlateau(int[][] grid, int centreX, int centreY) {

        ArrayList<position> mouvements = new ArrayList<position>();

        for (int direction = 0; direction < 9; direction++) {
            int posX = centreX + x[direction];
            int posY = centreY + y[direction];

            if (grid[posX][posY] == 0) {
                mouvements.add(new position(posX, posY));
            }

        }
        return mouvements;

    }
}
