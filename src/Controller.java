import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mikołaj Błaż on 17.10.16.
 */

public class Controller {
    public static void main(String[] args) throws IOException {
        Path inputFile = new Path("/user/mikib/input/hel.txt");
        if (args.length == 1) {
            inputFile = new Path(args[0]);
        }

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        BufferedReader br;
        String line;
        FSDataInputStream input = null;

//        ShinglesCount shinglesCount = new ShinglesCount();
//        ShinglesCount shinglesCount_nonascii = new ShinglesCount(false);

        List<ShinglesCount> scs = new LinkedList<>();
        List<ShinglesCount> scs_nonascii = new LinkedList<>();
        for (int shLen = 2; shLen < 11; shLen++) {
            scs.add(new ShinglesCount(shLen, 0, true));
            scs_nonascii.add(new ShinglesCount(shLen, 0, false));
        }
        for (int tokenLen = 2; tokenLen < 5; tokenLen++) {
            scs.add(new ShinglesCount(10, tokenLen, true));
            scs_nonascii.add(new ShinglesCount(10, tokenLen, false));
        }

        try {
            input = fs.open(inputFile);
            br = new BufferedReader(new InputStreamReader(input));

            while ((line = br.readLine()) != null) {
                for (ShinglesCount sc : scs)
                    sc.processLine(line);
                for (ShinglesCount sc : scs_nonascii)
                    sc.processLine(line);
            }

        } finally {
            IOUtils.closeStream(input);
        }


        /* Print results. */
        for (ShinglesCount sc : scs)
            sc.printStatistics();

        System.out.println("\nNon ASCII characters filtered:");
        for (ShinglesCount sc : scs_nonascii)
            sc.printStatistics();

    }
}
