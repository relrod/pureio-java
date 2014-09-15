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
        BiFunction<String, Function<LinkedList<String>, A>, B> readLines,
        BiFunction<TupleTwo<String, String>, A, B> appendString);

    /**
     * An action which might (in a typical environment) append a string to a
     * file.
     */
    final static class AppendString<A> extends FileOperation<A> {
        // TODO: Does this /really/ need to be TupleTwo? I feel like it doesn't.
        private TupleTwo<String,String> x;
        private A a;

        public AppendString(TupleTwo<String, String> x, A a) {
            this.x = x;
            this.a = a;
        }

        public <B> B cata(
            BiFunction<String, Function<LinkedList<String>, A>, B> readLines,
            BiFunction<TupleTwo<String, String>, A, B> appendString) {
            return appendString.apply(this.x, this.a);
        }
    }

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
            BiFunction<String, Function<LinkedList<String>, A>, B> readLines,
            BiFunction<TupleTwo<String, String>, A, B> appendString) {
            return readLines.apply(this.filename, this.f);
        }
    }

    // Functor
    public <B> FileOperation<B> map(Function<A, B> f) {
        return cata(
            (filename, s) -> new ReadLines<B>(filename, x -> f.apply(s.apply(x))),
            (data, a)     -> new AppendString<B>(data, f.apply(a))
        );
    }

    public PureFileIO<A> liftF() {
      return PureFileIO.free(map(x -> PureFileIO.pure(x)));
    }
}
