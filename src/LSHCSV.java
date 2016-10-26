import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mikib on 27.10.16.
 */
public class LSHCSV extends LSH {
    LSHCSV(Path outputDir, Path[] files, FileSystem fs, Configuration conf, boolean useTokens) {
        super(outputDir, files, fs, conf, useTokens);
    }

    /* Step 1 */
    @Override
    protected void createShinglesMatrix() throws IOException {
        ShinglesCountCSV sc = new ShinglesCountCSV(4, 0, false);
        sc.countShingles(files[0], fs);
        matrix = sc.getCSVShingles();

        this.documentsCount = sc.getLinesCount();
        this.signs = new int[documentsCount][SIGN_LEN];
    }

    /* Step 1 - token version */
    @Override
    protected void createTokensMatrix() throws IOException {
        ShinglesCountCSV sc = new ShinglesCountCSV(10, 4, false);
        sc.countShingles(files[0], fs);
        matrixTokens = sc.getCSVTokens();

        this.documentsCount = sc.getLinesCount();
        this.signs = new int[documentsCount][SIGN_LEN];
    }
}
