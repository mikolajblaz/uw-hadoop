import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;

/**
 * Created by Mikołaj Błaż on 17.10.16.
 */

public class Controller {
    private static FileSystem fs;
    private static Path inputFile;

    public static void main(String[] args) throws IOException {
        inputFile = new Path("/user/mikib/input/hel.txt");
        if (args.length == 1) {
            inputFile = new Path(args[0]);
        }

        Configuration conf = new Configuration();
        fs = FileSystem.get(conf);

        for (int sl = 2; sl < 11; sl++) {
            for (int tl : new int[] {0, 2, 3, 4}) {
                process_file(sl, tl, false);
            }
        }

        System.out.println("ASCII only:");
        for (int sl = 2; sl < 11; sl++) {
            for (int tl : new int[] {0, 2, 3, 4}) {
                process_file(sl, tl, false);
            }
        }
    }

    private static void process_file(int shingleLength, int tokenLength, boolean onlyASCII) throws IOException {
        BufferedReader br;
        String line;
        FSDataInputStream input = null;

        ShinglesCount sc = new ShinglesCount(shingleLength, tokenLength, onlyASCII);

        try {
            input = fs.open(inputFile);
            br = new BufferedReader(new InputStreamReader(input));

            while ((line = br.readLine()) != null) {
                sc.processLine(line);
            }

        } finally {
            IOUtils.closeStream(input);
        }

        /* Print results. */
        sc.printStatistics();

    }
}
