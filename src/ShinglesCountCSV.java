import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by mikib on 27.10.16.
 */
public class ShinglesCountCSV extends ShinglesCount {

    private Map<String, Set<Integer>> matrix = new HashMap<>();
    private Map<Integer, Set<Integer>> matrixTokens = new HashMap<>();

    private int currDoc = 0;

    public ShinglesCountCSV(int shingleLength, int tokenLength, boolean onlyASCII) {
        super(shingleLength, tokenLength, onlyASCII);
    }


    public Map<String, Set<Integer>> getCSVShingles() {
        return matrix;
    }

    public Map<Integer, Set<Integer>> getCSVTokens() {
        return matrixTokens;
    }

    @Override
    public void processLine(String line) {
        line = line.toLowerCase();
        if (onlyASCII) {
            line = line.replaceAll("[^\\x00-\\x7F]", "");
        }
        extractShingles(line);

        currDoc++;
    }

    @Override
    protected void addShingle(String sh) {
        if (tokenLength == 0) {
            if (!matrix.containsKey(sh))
                matrix.put(sh, new HashSet<Integer>());
            Set<Integer> shSet = matrix.get(sh);
            shSet.add(currDoc);
        } else {
            int token = 0;
            switch (tokenLength) {
                case 2: token = sh.hashCode() & twoMask; break;
                case 3: token = sh.hashCode() & threeMask; break;
                case 4: token = sh.hashCode(); break;
            }
            if (!matrixTokens.containsKey(token))
                matrixTokens.put(token, new HashSet<Integer>());
            Set<Integer> tokSet = matrixTokens.get(token);
            tokSet.add(currDoc);
        }
    }

    @Override
    public int getTotal() {
        if (tokenLength > 0)
            return matrixTokens.size();
        else
            return matrix.size();
    }

    public int getLinesCount() {
        return currDoc;
    }
}
