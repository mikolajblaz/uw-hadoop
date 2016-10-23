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
    public static void main(String[] args) throws IOException {
        String filepath = "/input/book1.txt";
        if (args.length == 1)
            filepath = args[0];

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        int count;
        ShinglesCount sc;

        System.out.println("Shingles\tno tokens\t2b tokens\t3b tokens \t4b tokens");
        for (int sl = 2; sl < 11; sl++) {
            System.out.print(Integer.toString(sl) + "\t\t");
            for (int tl : new int[] {0, 2, 3, 4}) {
                count = new ShinglesCount(sl, tl, false).countShingles(filepath, fs);
                System.out.printf("%-16d", count);
            }
            System.out.println();
        }

        System.out.println("\n\n############################\nASCII only:");
        System.out.println("Shingles\tno tokens\t2b tokens\t3b tokens \t4b tokens");
        for (int sl = 2; sl < 11; sl++) {
            System.out.print(Integer.toString(sl) + "\t\t");
            for (int tl : new int[] {0, 2, 3, 4}) {
                count = new ShinglesCount(sl, tl, true).countShingles(filepath, fs);
                System.out.printf("%-16d", count);
            }
            System.out.println();
        }
    }
}
