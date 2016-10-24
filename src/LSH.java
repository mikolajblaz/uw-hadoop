import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by mikib on 23.10.16.
 */
public class LSH {
    private final int SIGN_LEN = 100;

    private Map<String, Set<Integer>> matrix;
    private int[][] signs;

    LSH(Map<String, Set<Integer>> mat, int documentsCount) {
        this.matrix = mat;
        this.signs = new int[documentsCount][SIGN_LEN];
    }


    public int[][] generateSignatures() {
        final int[][] hashParams = generateHashParams();
        int[] hashValues = new int[SIGN_LEN];
        int rowId = 0;

        for (Set<Integer> rowOnes : matrix.values()) {
            for (int i = 0; i < SIGN_LEN; i++) {
                hashValues[i] = hash(rowId, hashParams[i]);
            }
            for (int col : rowOnes) {
                for (int i = 0; i < SIGN_LEN; i++) {
                    if (hashValues[i] < signs[col][i])
                        signs[col][i] = hashValues[i];
                }
            }
            rowId++;
        }
        return signs;
    }

    private int[][] generateHashParams() {
        int[][] params = new int[SIGN_LEN][2];
        Random r = new Random();
        int p, q;
        for (int i = 0; i < SIGN_LEN; i++) {
            // choose odd integers
            p = r.nextInt();
            q = r.nextInt();
            while (p % 2 == 0 || p > 50000000) p = r.nextInt();
            while (q % 2 == 0 || q < 1000000) q = r.nextInt();
            params[i][0] = p;
            params[i][1] = q;
        }
        return params;
    }

    private int hash(int value, int[] params) {
        return hash(value, params[0], params[1]);
    }

    private int hash(int value, int p, int q) {
        // TODO: quick power
        for (int i = 0; i < p; i++) {
            value = (value * p) % q;
        }
        return value;
    }
}
