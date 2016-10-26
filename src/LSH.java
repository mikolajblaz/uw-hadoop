import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
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
    private Set<Pair> candidatePairs = new HashSet<>();

    LSH(Path[] files, FileSystem fs, Configuration conf, Path outputDir) {
        this.files = files;
        this.fs = fs;
        this.conf = conf;
        this.outputDir = outputDir;
        this.documentsCount = files.length;
        this.signs = new int[documentsCount][SIGN_LEN];
    }

    public Set<Pair> doLSH(boolean reset) throws IOException {
        System.out.println("Init");
        createShinglesMatrix();
        System.out.println("Shingles done");
        saveMatrix();
        System.out.println("Matrix saving done");
        generateSignatures();
        System.out.println("Signatures done");
        if (reset)
            matrix = null;  // free memory
        saveSignatures();
        System.out.println("Signatures saving done");
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
                    matrix.put(sh, new HashSet<Integer>());
                shSet = matrix.get(sh);
                shSet.add(i);   // shingle 'sh' is in document 'i'
            }
        }
    }

    /*Step 2 */
    private int[][] generateSignatures() {
        initializeSigns();
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
                    buckets.put(bucket, new ArrayList<Integer>(1));
                list = buckets.get(bucket);
                list.add(doc);
            }
            clearBuckets();
        }
    }

    private void clearBuckets() {
        int n, m;
        for (ArrayList<Integer> b : buckets.values()) {
            if (b.size() > 1) {
                final int bSize = b.size();
                // add pairs
                for (int i = 0; i < bSize - 1; i++) {
                    for (int j = i + 1; j < bSize; j++) {
                        n = b.get(i);
                        m = b.get(j);
                        // add sorted
                        if (n < m)
                            candidatePairs.add(new Pair(n, m));
                        else
                            candidatePairs.add(new Pair(m, n));
                    }
                }
            }
        }
        buckets.clear();
    }

    private void saveMatrix() throws IOException {
        Path outputPath = new Path(outputDir, "matrix");
        FSDataOutputStream out = fs.create(outputPath);
        BufferedOutputStream output = new BufferedOutputStream(out);

        StringBuilder str;
        Set<Integer> set;

        for (Map.Entry<String, Set<Integer>> pair : matrix.entrySet()) {
            set = pair.getValue();
            str = new StringBuilder(pair.getKey() + '\t');
            for (int i = 0; i < documentsCount; i++) {
                str.append(set.contains(i) ? '1' : '.');
            }
            str.append('\n');
            output.write(str.toString().getBytes());
            output.flush();
        }

        output.close();
    }

    private void saveSignatures() throws IOException {
        Path outputPath = new Path(outputDir, "signatures");
        FSDataOutputStream out = fs.create(outputPath);
        BufferedOutputStream output = new BufferedOutputStream(out);

        StringBuilder str;

        for (int s = 0; s < SIGN_LEN; s++) {
            str = new StringBuilder("");
            for (int doc = 0; doc < documentsCount; doc++) {
                str.append(String.format("%-12d", signs[doc][s]));
            }
            str.append('\n');
            output.write(str.toString().getBytes());
            output.flush();
        }

        output.close();
    }

    private void initializeSigns() {
        for (int i = 0; i < documentsCount; i++) {
            for (int j = 0; j < SIGN_LEN; j++) {
                signs[i][j] = Integer.MAX_VALUE;
            }
        }
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
        return hash((hash(value) + p) * q);
    }

    private int hash(int x) {
        x = ((x >> 16) ^ x) * 0x45d9f3b;
        x = ((x >> 16) ^ x) * 0x45d9f3b;
        x = (x >> 16) ^ x;
        return x;
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
