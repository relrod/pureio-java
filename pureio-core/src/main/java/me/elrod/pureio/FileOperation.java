package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A sum type for file-related operations one might perform.
 */
 public abstract class FileOperation<A> {
    private FileOperation() {
    }

    public abstract <B> B cata(
        BiFunction<String, Function<LinkedList<String>, A>, B> readLines);

    /**
     * An action which might (in a typical environment) append a string to a
     * file.
     */
    /*final static class AppendString<A> extends FileOperation<A> {
        private A a;
        private String filename;
        private String text;

        public AppendString(
            String filename,
            String text,
            A a) {
            this.filename = filename;
            this.text = text;
            this.a = a;
        }

        public abstract <B> B cata(
            Function<String, Function<String, Function<A, B>>> appendString) {
            return appendString.apply(this.filename).apply(this.text).apply(this.a);
        }
    }
    */

    /**
     * An action which might (in a typical environment) read lines from a file
     * and return them as a {@link LinkedList}.
     */
    final static class ReadLines<A> extends FileOperation<A> {
        private String filename;
        private Function<LinkedList<String>, A> f;

        public ReadLines(String filename, Function<LinkedList<String>, A> f) {
            this.filename = filename;
            this.f = f;
        }

        public <B> B cata(
            BiFunction<String, Function<LinkedList<String>, A>, B> readLines) {
            return readLines.apply(this.filename, this.f);
        }
    }

    // Functor
    public <B> FileOperation<B> map(Function<A, B> f) {
        return cata(
            (filename, s) -> new ReadLines<B>(filename, x -> f.apply(s.apply(x)))
        );
    }

    public PureFileIO<A> liftF() {
      return PureFileIO.free(map(x -> PureFileIO.pure(x)));
    }

    /*    public PureIOT<A> liftT() {
      return PureIOT.suspend(map(x -> PureIOT.pure(x)));
    }
    */
}
