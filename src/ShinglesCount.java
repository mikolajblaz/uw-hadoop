import java.util.HashSet;
import java.util.Set;

/**
 * Created by mikib on 19.10.16.
 */

public class ShinglesCount {
    private String lastLineEnd = null;

    private final int shingleLength;
    private final int tokenLength;
    private boolean onlyASCII = true;

    private Set<String> shingles = new HashSet<>();
    private Set<Integer> tokens = new HashSet<>();

    public ShinglesCount(int shingleLength, int tokenLength) {
        this.shingleLength = shingleLength;
        this.tokenLength = tokenLength;
    }

    public ShinglesCount(int shingleLength, int tokenLength, boolean onlyASCII) {
        this.shingleLength = shingleLength;
        this.tokenLength = tokenLength;
        this.onlyASCII = onlyASCII;
    }

    public void processLine(String line) {
        line = line.toLowerCase();
        if (onlyASCII) {
            line = line.replaceAll("[^\\x00-\\x7F]", "");
        }
        if (lastLineEnd == null) {
            extractShingles(line);
        } else {
            extractShingles(lastLineEnd + line);
        }
    }

    /** After file reading is done, this method should be called. */
    public void finishReading() {
        if (tokenLength > 0)
            convertToTokens();
    }

    public int getTotal() {
        if (tokenLength > 0)
            return tokens.size();
        else
            return shingles.size();
    }

    /** Convert shingles to tokens. */
    private void convertToTokens() {
        final int twoMask = 0x0000ffff;
        final int threeMask = 0x00ffffff;

        int hash;
        for (String sh : shingles) {
            hash = sh.hashCode();
            if (tokenLength == 2)
                hash &= twoMask;
            else if (tokenLength == 3)
                hash &= threeMask;
            tokens.add(hash);
        }
    }


    /** Print statistics. */
    private void printStatistics() {
        System.out.print("Total count:" + Integer.toString(shingles.size()));
        System.out.println("; shingles length: " + Integer.toString(shingleLength));
    }

    /** Print statistics. */
    private void printTokenStatistics() {
        System.out.print("Total count:" + Integer.toString(tokens.size()));
        System.out.print("; shingles length: " + Integer.toString(shingleLength));
        System.out.println(", token length: " + Integer.toString(tokenLength));
    }

    /** Extract shingles and save unused characters from line end. */
    private void extractShingles(String line) {
        extractShingles(line, shingleLength);
    }

    /** Extract shingles and save unused characters from line end. */
    private void extractShingles(String line, int shingleLength) {
        final int lineLength = line.length();
        for (int i = 0; i < lineLength - shingleLength + 1; i++) {
            shingles.add(line.substring(i, i + shingleLength));
        }
        if (lineLength > shingleLength - 1) {
            lastLineEnd = line.substring(lineLength - shingleLength + 1);
        } else {
            lastLineEnd = line;
        }
    }

    /** Print all shingles */
    private void outputShingles() {
        System.out.println("Shingles:");
        for (String sh : shingles) {
            System.out.println(sh);
        }
    }
}