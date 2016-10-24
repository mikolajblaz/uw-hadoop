import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mikołaj Błaż on 17.10.16.
 */

public class Minhashing {
    private static FileSystem fs;

    public static void main(String[] args) throws IOException {
        String[] files = args;

        Configuration conf = new Configuration();
        fs = FileSystem.get(conf);

        Map<String, Set<Integer>> mat = createShinglesMatrix(files);
        // matrix 'mat' is ready now
        LSH lsh = new LSH(mat, files.length);
        int[][] signs = lsh.generateSignatures();
        // signs is a matrix [docsCount][100] now

    }

    private static Map<String, Set<Integer> > createShinglesMatrix(String[] files) throws IOException {
        ShinglesCount sc;
        Set<String> shingles;

        /* m<s, d> - shingle 's' is in documents 'd' */
        Map<String, Set<Integer>> mat = new HashMap<>();
        Set<Integer> shSet;

        int documentsCount = files.length;
        for (int i = 0; i < documentsCount; i++) {
            sc = new ShinglesCount(4, 0);
            sc.countShingles(files[i], fs);
            shingles = sc.getShingles();
            for (String sh : shingles) {
                shSet = mat.getOrDefault(sh, new HashSet<>());
                shSet.add(i);   // shingle 'sh' is in document 'i'
            }
        }
        return mat;
    }
}
