import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private final int twoMask = 0x0000ffff;
    private final int threeMask = 0x00ffffff;

    private Set<String> shingles = new HashSet<>();
    private Set<Integer> tokens = new HashSet<>();


    public ShinglesCount(int shingleLength, int tokenLength) {
        this(shingleLength, tokenLength, false);
    }

    public ShinglesCount(int shingleLength, int tokenLength, boolean onlyASCII) {
        this.shingleLength = shingleLength;
        this.tokenLength = tokenLength;
        this.onlyASCII = onlyASCII;
    }

    public Set<Integer> getTokens() {
        return tokens;
    }

    public Set<String> getShingles() {
        return shingles;
    }

    public int countShingles(Path filepath, FileSystem fs) throws IOException {
        BufferedReader br;
        String line;
        FSDataInputStream input = null;

        Path inputFile = filepath;

        try {
            input = fs.open(inputFile);
            br = new BufferedReader(new InputStreamReader(input));

            while ((line = br.readLine()) != null) {
                processLine(line);
            }

        } finally {
            IOUtils.closeStream(input);
        }

        /* Print results. */
        return getTotal();
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

    public int getTotal() {
        if (tokenLength > 0)
            return tokens.size();
        else
            return shingles.size();
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
        final int lineLength = line.length();
        for (int i = 0; i < lineLength - shingleLength + 1; i++) {
            addShingle(line.substring(i, i + shingleLength));
        }
        if (lineLength > shingleLength - 1) {
            lastLineEnd = line.substring(lineLength - shingleLength + 1);
        } else {
            lastLineEnd = line;
        }
    }

    private void addShingle(String sh) {
        switch (tokenLength) {
            case 0: shingles.add(sh); break;
            case 2: tokens.add(sh.hashCode() & twoMask); break;
            case 3: tokens.add(sh.hashCode() & threeMask); break;
            case 4: tokens.add(sh.hashCode()); break;
        }
    }

    /** Print all shingles */
    public void outputShingles() {
        if (tokenLength == 0) {
            System.out.println("Shingles:");
            for (String sh : shingles) {
                System.out.println(sh);
            }
        } else {
            System.out.println("Tokens:");
            for (int sh : tokens) {
                System.out.println(Integer.toString(sh));
            }
        }
    }
}