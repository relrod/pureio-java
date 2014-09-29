package me.elrod.pureio;

import java.util.function.Function;

/**
 * <code>Free (F =&gt;</code> {@link ConsoleFileCoproduct}<code>)</code>.
 */
public abstract class ConsoleFileFree<A> {
    private ConsoleFileFree() {
    }

    // Functor
    public <B> ConsoleFileFree<B> map(Function<A, B> f) {
        return cata(
            a -> ConsoleFileFree.pure(f.apply(a)),
            a -> ConsoleFileFree.free(a.map(k -> k.map(f))));
    }

    // Free monad
    public <B> ConsoleFileFree<B> flatMap(Function<A, ConsoleFileFree<B>> f) {
        return cata(
            f,
            a -> ConsoleFileFree.free(a.map(k -> k.flatMap(f))));
    }

    public abstract <B> B cata(
        Function<A, B> pure,
        Function<ConsoleFileCoproduct<ConsoleFileFree<A>>, B> free);

    //----------------------------------------------------------------------

    final static class Pure<A> extends ConsoleFileFree<A> {
        private A a;

        public Pure(A a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<ConsoleFileCoproduct<ConsoleFileFree<A>>, B> free) {
            return pure.apply(a);
        }
    }


    public static <A> ConsoleFileFree<A> pure(A a) {
        return new Pure<A>(a);
    }

    //----------------------------------------------------------------------

    final static class Free<A> extends ConsoleFileFree<A> {
        private ConsoleFileCoproduct<ConsoleFileFree<A>> a;

        public Free(ConsoleFileCoproduct<ConsoleFileFree<A>> a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<ConsoleFileCoproduct<ConsoleFileFree<A>>, B> free) {
            return free.apply(a);
        }
    }

    public static <A> ConsoleFileFree<A> free(ConsoleFileCoproduct<ConsoleFileFree<A>> a) {
        return new Free<A>(a);
    }
}
