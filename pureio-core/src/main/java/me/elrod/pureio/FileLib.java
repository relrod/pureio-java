package me.elrod.pureio;

import java.util.function.Function;

/** This provides a nicer public API over the rest of the file IO code. */
public class FileLib {
    public static PureFileIO<LinkedList<String>> readLines(String filename) {
        return new FileOperation.ReadLines<LinkedList<String>>(filename, Function.identity()).liftF();
    }

    public static PureFileIO<Unit> appendString(final String filename, final String text) {
        TupleTwo<String, String> metadata = new TupleTwo<String, String>() {
            public String run1() {
                return filename;
            }
            public String run2() {
                return text;
            }
        };
        return new FileOperation.AppendString<Unit>(metadata, Unit.VALUE).liftF();
    }
}
