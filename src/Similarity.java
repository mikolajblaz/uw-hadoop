import javafx.util.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.*;

/**
 * Created by Mikołaj Błaż on 17.10.16.
 */

public class Similarity {
    public static void main(String[] args) throws IOException {
        Path inputDir = new Path(args[0]);

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        FileStatus[] inputFiles = fs.listStatus(inputDir);
        String[] files = new String[inputFiles.length];
        String[] filenames = new String[inputFiles.length];
        Path path;
        for (int i = 0; i < inputFiles.length; i++) {
            path = inputFiles[i].getPath();
            files[i] = path.toString();
            filenames[i] = path.getName();
            System.out.println(files[i]);
        }

        LSH lsh = new LSH(files, fs);
        List<Pair<Integer, Integer>> pairs = lsh.doLSH(true);

        System.out.println("Candidate pairs:");
        for (Pair<Integer, Integer> p : pairs) {
            System.out.println("(" + filenames[p.getKey()] + ", " + filenames[p.getValue()] + ")");
        }
        System.out.println("\nTotal: " + Integer.toString(pairs.size()));
    }






}
