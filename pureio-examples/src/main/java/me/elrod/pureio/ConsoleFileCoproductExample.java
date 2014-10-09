import me.elrod.pureio.*;
import java.util.function.Function;

public class ConsoleFileCoproductExample {
    public static void main(String[] args) {
        ConsoleFileFree<LinkedList<String>> first =
            ConsoleFileLib.putStrLn("What is your name?").liftF()
            .flatMap(x -> ConsoleFileLib.readLine().liftF()
                     .flatMap(name -> ConsoleFileLib.putStrLn("Hi there, " + name + "! Give me a file to read:").liftF()
                              .flatMap(u1 -> ConsoleFileLib.readLine().liftF()
                                       .flatMap(fname -> ConsoleFileLib.readLines(fname).liftF()))));

        // This doesn't have to be separate, but why not?
        ConsoleFileFree<LinkedList<Unit>> second =
            first.flatMap(
                x -> ConsoleFileFree.mapM(
                    y -> ConsoleFileLib.putStrLn(y).liftF(),
                    x));

        UnsafePerformIO.unsafePerformConsoleFileIO(second);
    }
}
