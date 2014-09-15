package me.elrod.pureio;

import java.io.*;
import java.util.function.Function;

/**
 * A simple, untrampolined free IO monad for file operations:
 * <code>Free</code> {@link FileOperation}.
 */
public abstract class PureFileIO<A> {
    private PureFileIO() {
    }

    // Functor
    public <B> PureFileIO<B> map(Function<A, B> f) {
        return cata(
            a -> PureFileIO.pure(f.apply(a)),
            a -> PureFileIO.free(a.map(k -> k.map(f))));
    }

    // Free monad
    public <B> PureFileIO<B> flatMap(Function<A, PureFileIO<B>> f) {
        return cata(f, a -> PureFileIO.free(a.map(k -> k.flatMap(f))));
    }

    public abstract <B> B cata(
        Function<A, B> pure,
        Function<FileOperation<PureFileIO<A>>, B> free);

    //----------------------------------------------------------------------

    final static class Pure<A> extends PureFileIO<A> {
        private A a;

        public Pure(A a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<FileOperation<PureFileIO<A>>, B> free) {
            return pure.apply(a);
        }
    }


    public static <A> PureFileIO<A> pure(A a) {
        return new Pure<A>(a);
    }

    //----------------------------------------------------------------------

    final static class Free<A> extends PureFileIO<A> {
        private FileOperation<PureFileIO<A>> a;

        public Free(FileOperation<PureFileIO<A>> a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<FileOperation<PureFileIO<A>>, B> free) {
            return free.apply(a);
        }
    }

    public static <A> PureFileIO<A> free(FileOperation<PureFileIO<A>> a) {
        return new Free<A>(a);
    }

    public static <A,B> PureFileIO<B> forever(PureFileIO<A> x) {
        return x.flatMap(unused -> forever(x));
    }
}
