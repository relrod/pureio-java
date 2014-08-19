package me.elrod.pureio;

import java.util.function.Function;

/** This provides a nicer public API over the rest of the file IO code. */
public class FileLib {
    public static PureFileIO<LinkedList<String>> readLines(String filename) {
        return new FileOperation.ReadLines<LinkedList<String>>(filename, Function.identity()).liftF();
    }
}
