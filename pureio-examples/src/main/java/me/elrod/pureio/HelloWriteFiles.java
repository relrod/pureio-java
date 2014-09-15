import me.elrod.pureio.*;

import java.util.function.Function;
import java.io.*;

public class HelloWriteFiles {
    public static void main(String[] args) throws IOException {
        // We should be clever and implement this in PureFileIO!
        // For now, go away and don't look at this please. :-(
        File temp = File.createTempFile("temp", ".txt");

        PureFileIO<Unit> comp =
            FileLib.appendString(temp.getPath(), "Hello from pureio-java!");

        Unit res = UnsafePerformIO.unsafePerformFileIO(comp);
        System.out.println("Done. Go look at: " + temp.getPath());
    }
}
