import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by mikib on 23.10.16.
 */
public class LSH {
    private Map<String, Set<Integer>> matrix;
    private int[][] signs;

    private final int SIGN_LEN = 100;

    LSH(Map<String, Set<Integer>> mat, int documentsCount) {
        this.matrix = mat;
        this.signs = new int[documentsCount][SIGN_LEN];
    }


    public void generateSignatures() {

    }


    private int hash(int value, int[] params) {
        return hash(value, params[0], params[1]);
    }

    private int hash(int value, int p, int q) {
        for (int i = 0; i < p; i++) {
            value = (value * p) % q;
        }
        return value;
    }

    private int[] chooseHashParameters() {
        return new int[] {1, 2};
    }
}
