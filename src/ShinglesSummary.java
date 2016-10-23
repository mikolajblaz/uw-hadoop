import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;

/**
 * Created by Mikołaj Błaż on 17.10.16.
 */

public class ShinglesSummary {
    private static FileSystem fs;
    private static Path inputFile;

    public static void main(String[] args) throws IOException {
        inputFile = new Path("/input/book1.txt");
        if (args.length == 1) {
            inputFile = new Path(args[0]);
        }

        Configuration conf = new Configuration();
        fs = FileSystem.get(conf);

        int count;

        System.out.println("Shingles\tno tokens\t2b tokens\t3b tokens \t4b tokens");
        for (int sl = 2; sl < 11; sl++) {
            System.out.print(Integer.toString(sl) + "\t\t");
            for (int tl : new int[] {0, 2, 3, 4}) {
                count = process_file(sl, tl, false);
                System.out.printf("%-16d", count);
            }
            System.out.println();
        }

        System.out.println("\n\n############################\nASCII only:");
        System.out.println("Shingles\tno tokens\t2b tokens\t3b tokens \t4b tokens");
        for (int sl = 2; sl < 11; sl++) {
            System.out.print(Integer.toString(sl) + "\t\t");
            for (int tl : new int[] {0, 2, 3, 4}) {
                count = process_file(sl, tl, true);
                System.out.printf("%-16d", count);
            }
            System.out.println();
        }
    }

    private static int process_file(int shingleLength, int tokenLength, boolean onlyASCII) throws IOException {
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
        return sc.getTotal();
    }
}
