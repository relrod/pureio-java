import me.elrod.pureio.*;
import java.util.function.Function;

public class ConsoleFileCoproductExample {
    public static void main(String[] args) {
        ConsoleFileFree<LinkedList<Unit>> program =
            ConsoleFileLib.putStrLn("What is your name?").liftF()
            .$ (x     -> ConsoleFileLib.readLine().liftF()
            .$ (name  -> ConsoleFileLib.putStrLn("Hi there, " + name + "! Give me a file to read:").liftF()
            .$ (u1    -> ConsoleFileLib.readLine().liftF()
            .$ (fname -> ConsoleFileLib.readLines(fname).liftF()))))
            .$ (lines -> ConsoleFileFree.mapM(y -> ConsoleFileLib.putStrLn(y).liftF(), lines));

        UnsafePerformIO.unsafePerformConsoleFileIO(program);
    }
}
