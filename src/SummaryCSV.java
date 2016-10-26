import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;

/**
 * Created by Mikołaj Błaż on 27.10.16.
 */

public class SummaryCSV {
    private static FileSystem fs;

    public static void main(String[] args) throws IOException {
        Path inputDir = new Path(args[0]);

        Configuration conf = new Configuration();
        fs = FileSystem.get(conf);

        FileStatus[] inputFiles = fs.listStatus(inputDir);
        Path inputPath;

        for (FileStatus f : inputFiles) {
            inputPath = f.getPath();
            System.out.println("\n\n\n########### " + inputPath.getName() + " ###########\n");
            printShingles(inputPath, false);
            printShingles(inputPath, true);
        }
    }

    public static void printShingles(Path filepath, boolean onlyASCII) throws IOException {
        if (onlyASCII)
            System.out.println("\n\nASCII only:");
        System.out.println("Shingles\tno tokens\t2b tokens\t3b tokens \t4b tokens");
        for (int sl = 2; sl < 11; sl++) {
            System.out.print(Integer.toString(sl) + "\t\t");
            for (int tl : new int[] {0, 2, 3, 4}) {
                int count = new ShinglesCountCSV(sl, tl, onlyASCII).countShingles(filepath, fs);
                System.out.printf("%-16d", count);
            }
            System.out.println();
        }
    }
}