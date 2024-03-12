import java.lang.reflect.Array;
import java.time.Clock;
import java.util.HashMap;
import java.util.Random;

public class App {
    private static int[] _memo; 
    private final static String maxKey = "max";
    private final static String decoupeKey = "decoupe";

    private static void fillZero(int[] tab) {
        for (int i = 0; i < tab.length; i++) {
            tab[i] = 0;
        }
    }
    private static void initMemo(int n) {
        // Initialisation
        _memo = new int[n + 1];
        /* for (int i = 0; i < n; i++) {
            _memo[i] = 0;
        } */
        fillZero(_memo);
    }

    public static Object decouperMemo(int[] prix, int longueur) {
        if (prix.length - 1 == longueur) {
            initMemo(longueur);
        }
        if (longueur == 0) {
            return 0;
        }
        int revenuMax = Integer.MIN_VALUE;
        if (_memo[longueur] != 0) {
            return _memo[longueur];
        } else {
            for (int i = 1; i <= longueur; i++) {
                revenuMax = Math.max(revenuMax, prix[i] + (int)decouperMemo(prix, longueur - i));
            }
            _memo[longueur] = revenuMax;
        }
        // A la fin
        if (_memo[_memo.length - 1] != 0) {
            HashMap hMap = new HashMap<String, Object>();
            int[] listeLongueur = new int[_memo.length];
            fillZero(listeLongueur);
            int max = revenuMax;
            int j = 1;
            for (int i = _memo.length - 2; i >= 0; i--) {
                int x = _memo[i];
                if (max - x == prix[j]) {
                    listeLongueur[j]++;
                    max = x;
                    j = 1;
                }
                else {
                    j++;
                }
            }
            hMap.put(maxKey, revenuMax);
            hMap.put(decoupeKey, listeLongueur);
            return hMap;
        }
        return revenuMax;
    }

    public static int decouper(int[] prix, int longueur) {
        if (longueur == 0) {
            return 0;
        }
        int revenuMax = Integer.MIN_VALUE;
        for (int i = 1; i <= longueur; i++) {
            revenuMax = Math.max(revenuMax, prix[i] + decouper(prix, longueur - i));
        }
        return revenuMax;
    }

    private static int[] genererPrix(int n, long seed) {
        int[] prix = new int[n + 1];
        prix[0] = 0; // Prix de 0 = 0
        // Générateur de nombre aléatoire
        Random rd = new Random(seed);
        for (int i = 1; i < prix.length; i++) {
            int inf = prix[i - 1] == 0 ? prix[i - 1] + 1 : prix[i - 1], sup = inf + 10;
            int nbr = rd.nextInt(inf, sup);
            prix[i] = nbr;
        }
        return prix;
    }

    private static int[] genererPrix(int n) {
        return genererPrix(n, System.currentTimeMillis());
    }

    public static void afficherPrix(int[] prix) {
        String s = "";
        for (int i = 1; i < prix.length; i++) {
            s += i + "m" + ": " + prix[i] + "Ar" + (i % 5 == 0 ? "\n" : " | ");
        }
        System.out.println(s);
    }

    public static void main(String[] args) {
        int longueur = 10;
        int[] prix = genererPrix(longueur); // Générer le prix en auto
        System.out.println();
        afficherPrix(prix);
        double diff1 = 0, diff2 = 0;
        // Exécution sans memoisation
        long startTime = System.nanoTime();
        decouper(prix, longueur);
        // System.out.print("Valeur maximale: " + decouper(prix, longueur));
        long endTime = System.nanoTime();
        diff1 = ((double) (endTime - startTime)) / 1_000_000;
        // Exécution avec memoisation
        startTime = System.nanoTime();
        HashMap hMap = (HashMap)decouperMemo(prix, longueur);   // Avec maxKey et decoupeKey
        endTime = System.nanoTime();
        diff2 = ((double) (endTime - startTime)) / 1_000_000;
        int revenuMax = (int)hMap.get(maxKey);
        int[] decoupe = (int[])hMap.get(decoupeKey);
        // System.out.print("Valeur maximale: " + revenuMax);
        String str = "******* Détails ********\n";
        for (int i = 1; i < decoupe.length; i++) {
            if (decoupe[i] != 0) {
                int montant = decoupe[i] * prix[i];
                str += i + "m x " + decoupe[i] + " (PU: " + prix[i] + "Ar) => " + montant + "Ar" + "\n";
            }
        }
        str += "----------\nTotal: " + longueur + "m => " + revenuMax + "Ar";
        System.out.println(str);
        // Comparaison
        System.out.println("\nExecution sans memo en " + diff1 + "ms");
        System.out.println("Execution avec memo en " + diff2 + "ms");
        System.out.println("Differences de temps d'exécution: " + (diff1 - diff2) + "ms");
    }
}
