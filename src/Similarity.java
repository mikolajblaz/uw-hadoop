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
        Path outputDir = new Path(args[1]);

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        FileStatus[] inputFiles = fs.listStatus(inputDir);
        Path[] files = new Path[inputFiles.length];
        String[] filenames = new String[inputFiles.length];

        System.out.println("Files read:");
        for (int i = 0; i < inputFiles.length; i++) {
            files[i] = inputFiles[i].getPath();
            filenames[i] = files[i].getName();
            System.out.println(filenames[i]);
        }

        System.out.println("\n\n############## Shingles version ###############");
        printCandidates(new LSH(outputDir, files, fs, conf, false), filenames); // shingles
        System.out.println("\n\n############### Tokens version ###############");
        printCandidates(new LSH(outputDir, files, fs, conf, true), filenames);  // tokens
    }

    public static void printCandidates(LSH lsh, String[] filenames) throws IOException {

        Set<Pair> pairs = lsh.doLSH(true);

        System.out.println("Candidate pairs:");
        for (Pair p : pairs) {
            System.out.println("(" + filenames[p.a] + ", " + filenames[p.b] + ")");
        }
        System.out.println("\nTotal pairs found: " + Integer.toString(pairs.size()));
    }




}
