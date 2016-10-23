import java.util.HashSet;
import java.util.Set;

/**
 * Created by mikib on 19.10.16.
 */

public class ShinglesCount {
    private String line;
    private String lastLineEnd = null;

    private int shingleLength = 4;
    private int tokenLength = 0;
    private boolean onlyASCII = true;

    private Set<String> shingles = new HashSet<>();
    private Set<String> Tokens = new HashSet<>();

    public ShinglesCount() {};

    public ShinglesCount(boolean onlyASCII) {
        this.onlyASCII = onlyASCII;
    }

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

    /** Convert shingles to tokens. */
    public void convertToTokens() {

    }


    /** After file reading is done, this method should be called. */
    public void finishReading() {
        if (tokenLength > 0)
            convertToTokens();
        printStatistics();
    }


    /** Print statistics. */
    public void printStatistics() {
        System.out.print("Total count:" + Integer.toString(shingles.size()));
        System.out.print("; shingles length: " + Integer.toString(shingleLength));
        if (tokenLength > 0) {
            System.out.print(", token length: " + Integer.toString(tokenLength));
        }
        System.out.println();
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