import me.elrod.pureio.*;

import java.util.function.Function;

public class HelloReadFiles {
    private static PureFileIO<LinkedList<String>> comp =
        FileLib.readLines("/etc/passwd");

  public static void main(String[] args) {
      Function<String, String> printIt = x -> { System.out.println(x); return x; };
      LinkedList<String> lines = UnsafePerformIO.unsafePerformFileIO(comp);
      lines.map(printIt);
  }
}
