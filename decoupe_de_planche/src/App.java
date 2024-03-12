import java.lang.reflect.Array;
import java.time.Clock;
import java.util.Random;

public class App {
    private static int[] _memo;
    public static int decouperMemo(int[] prix, int longueur) {
        if (prix.length - 1 == longueur) {
            // Initialisation
            _memo = new int[longueur + 1];
            for (int i = 0; i < _memo.length; i++) {
                _memo[i] = 0;
            }
        }
        if (longueur == 0) {
            return 0;
        }
        int revenuMax = Integer.MIN_VALUE;
        if (_memo[longueur] != 0) {
            return _memo[longueur];
        }
        else {
            for (int i = 1; i <= longueur; i++) {
                revenuMax = Math.max(revenuMax, prix[i] + decouperMemo(prix, longueur - i));
            }
        }
        if (_memo[longueur] == 0) {
            _memo[longueur] = revenuMax;
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
        prix[0] = 0;    // Prix de  0 = 0
        // Générateur de nombre aléatoire
        Random rd = new Random(seed);
        for (int i = 1; i < prix.length; i++) {
            int inf = prix[i-1] == 0? prix[i-1] + 1: prix[i-1], sup = inf + 10;
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
            s += i + "m"+ ": " + prix[i] + "Ar" + "\n";
        }
        System.out.println(s);
    }
    public static void main(String[] args) {
        int longueur = 25;
        int[] prix = genererPrix(longueur); // Générer le prix en auto
        afficherPrix(prix);
        double diff1 = 0, diff2 = 0;
        // Exécution sans memoisation
        long startTime = System.nanoTime();
        System.out.print("Valeur maximale: " + decouper(prix, longueur));
        long endTime = System.nanoTime();
        diff1 = ((double)(endTime - startTime)) / 1_000_000;
        System.out.println(" (Execution sans memo en " + diff1 + "ms)");
        // Exécution avec memoisation
        startTime = System.nanoTime();
        System.out.print("Valeur maximale: " + decouperMemo(prix, longueur));
        endTime = System.nanoTime();
        diff2 = ((double)(endTime - startTime)) / 1_000_000;
        System.out.println(" (Execution avec memo en " + diff2 + "ms)");
        // Comparaison
        System.out.println("Differences de temps d'exécution: " + (diff1 - diff2) + "ms");
    }
}
