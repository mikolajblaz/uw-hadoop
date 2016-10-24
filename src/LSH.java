import javafx.util.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by mikib on 23.10.16.
 */
public class LSH {
    private final int bands = 5;
    private final int rows = 20;
    private final int SIGN_LEN = bands * rows;
    private final int bucketsCnt = 1000000;
    private final int documentsCount;

    private FileSystem fs;
    private Configuration conf;
    private Path[] files;
    private Path outputDir;

    /* Succesive steps: */
    private Map<String, Set<Integer>> matrix = new HashMap<>();
    private int[][] signs;
    private Map<Integer, ArrayList<Integer>> buckets = new HashMap<>();
    private List<Pair<Integer, Integer>> candidatePairs = new LinkedList<>();

    LSH(Path[] files, FileSystem fs, Configuration conf, Path outputDir) {
        this.files = files;
        this.fs = fs;
        this.conf = conf;
        this.outputDir = outputDir;
        this.documentsCount = files.length;
        this.signs = new int[documentsCount][SIGN_LEN];
    }

    public List<Pair<Integer, Integer>> doLSH(boolean reset) throws IOException {
        System.out.println("Init");
        createShinglesMatrix();
        System.out.println("Shingles done");
        saveMatrix();
        System.out.println("Saving done");
        generateSignatures();
        System.out.println("Signatures done");
        if (reset)
            matrix = null;  // free memory
        saveSignatures();
        System.out.println("Saving done");
        hashToBuckets();
        System.out.println("Buckets done");
        if (reset) {
            signs = null;  // free memory
            buckets = null;
        }
        System.out.println("LSH done");
        return candidatePairs;
    }

    /* Step 1 */
    private void createShinglesMatrix() throws IOException {
        ShinglesCount sc;
        Set<String> shingles;

        /* m<s, d> - shingle 's' is in documents 'd' */
        Set<Integer> shSet;

        for (int i = 0; i < documentsCount; i++) {
            sc = new ShinglesCount(4, 0);
            sc.countShingles(files[i], fs);
            shingles = sc.getShingles();
            for (String sh : shingles) {
                if (!matrix.containsKey(sh))
                    matrix.put(sh, new HashSet<>());
                shSet = matrix.get(sh);
                shSet.add(i);   // shingle 'sh' is in document 'i'
            }
        }
    }

    /*Step 2 */
    private int[][] generateSignatures() {
        final int[][] hashParams = generateHashParams();
        int[] hashValues = new int[SIGN_LEN];
        int rowId = 0;

        for (Set<Integer> rowOnes : matrix.values()) {
            for (int i = 0; i < SIGN_LEN; i++) {
                hashValues[i] = hashInt(rowId, hashParams[i]);
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

    /* Step 3 */
    private void hashToBuckets() {
        int bucket;
        List<Integer> list;
        for (int b = 0; b < bands; b++) {
            for (int doc = 0; doc < documentsCount; doc++) {
                bucket = hashBand(signs[doc], b * rows);
                if (!buckets.containsKey(bucket))
                    buckets.put(bucket, new ArrayList<>(1));
                list = buckets.get(bucket);
                list.add(doc);
            }
            clearBuckets();
        }
    }

    private void clearBuckets() {
        for (ArrayList<Integer> b : buckets.values()) {
            if (b.size() > 1) {
                final int bSize = b.size();
                // add pairs
                for (int i = 0; i < bSize - 1; i++) {
                    for (int j = i + 1; j < bSize; j++) {
                        candidatePairs.add(new Pair<>(b.get(i), b.get(j)));
                    }
                }
            }
        }
        buckets.clear();
    }

    private void saveMatrix() throws IOException {
        String text = "Hello hello";
        InputStream in = new BufferedInputStream(new ByteArrayInputStream(text.getBytes()));
        Path outputPath = new Path(outputDir, "matrix");
        FSDataOutputStream out = fs.create(outputPath);
        IOUtils.copyBytes(in, out, conf);
    }

    private void saveSignatures() {

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

    private int hashInt(int value, int[] params) {
        return hashInt(value, params[0], params[1]);
    }

    private int hashInt(int value, int p, int q) {
        // TODO: quick power
        for (int i = 0; i < p; i++) {
            value = (value * p) % q;
        }
        return value;
    }

    /* Similair to hashCode String implementation. */
    private int hashBand(int[] band, int startIndex) {
        int val = 0;
        for (int i = startIndex; i < startIndex + rows; i++) {
            val = (31 * val) + band[i];
        }
        return val % bucketsCnt;
    }
}
