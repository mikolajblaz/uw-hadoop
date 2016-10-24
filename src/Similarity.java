import javafx.util.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;
import java.util.*;

/**
 * Created by Mikołaj Błaż on 17.10.16.
 */

public class Similarity {
    public static void main(String[] args) throws IOException {
        String[] files = args;

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        LSH lsh = new LSH(files, fs);
        List<Pair<Integer, Integer>> pairs = lsh.doLSH(true);

        System.out.println("Candidate pairs:");
        for (Pair<Integer, Integer> p : pairs) {
            System.out.println("(" + files[p.getKey()] + ", " + files[p.getValue()] + ")");
        }
        System.out.println("\nTotal: " + Integer.toString(files.length));
    }






}
