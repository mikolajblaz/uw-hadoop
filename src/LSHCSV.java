import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Created by mikib on 27.10.16.
 */
public class LSHCSV extends LSH {
    LSHCSV(Path outputDir, Path[] files, FileSystem fs, Configuration conf, boolean useTokens) {
        super(outputDir, files, fs, conf, useTokens);
    }
}
