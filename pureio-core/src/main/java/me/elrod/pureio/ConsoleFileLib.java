package me.elrod.pureio;

import java.util.function.Function;

/**
 * Provide an easier way to construct console or file operations
 */
public class ConsoleFileLib {
    public static ConsoleFileCoproduct<Unit> putStrLn(String s) {
        return new TerminalOperation.PutStrLn<Unit>(s, Unit.VALUE).liftConsoleFileCoproduct();
    }

    public static ConsoleFileCoproduct<String> readLine() {
        return new TerminalOperation.ReadLine<String>(Function.identity()).liftConsoleFileCoproduct();
    }

    public static ConsoleFileCoproduct<Unit> exit(Integer exitCode) {
        return new TerminalOperation.Exit<Unit>(exitCode, Unit.VALUE).liftConsoleFileCoproduct();
    }

    public static ConsoleFileCoproduct<LinkedList<String>> readLines(String filename) {
        return new FileOperation.ReadLines<LinkedList<String>>(filename, Function.identity()).liftConsoleFileCoproduct();
    }

    public static ConsoleFileCoproduct<Unit> appendString(final String filename, final String text) {
        TupleTwo<String, String> metadata = new TupleTwo<String, String>() {
            public String run1() {
                return filename;
            }
            public String run2() {
                return text;
            }
        };
        return new FileOperation.AppendString<Unit>(metadata, Unit.VALUE).liftConsoleFileCoproduct();
    }
}
