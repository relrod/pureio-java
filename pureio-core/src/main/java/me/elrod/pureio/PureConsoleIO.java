package me.elrod.pureio;

import java.util.function.Function;

/**
 * A simple, untrampolined free IO monad:
 * <code>Free</code> {@link TerminalOperation}.
 */
public abstract class PureConsoleIO<A> {
    private PureConsoleIO() {
    }

    // Functor
    public <B> PureConsoleIO<B> map(Function<A, B> f) {
        return cata(
            a -> PureConsoleIO.pure(f.apply(a)),
            a -> PureConsoleIO.free(a.map(k -> k.map(f))));
    }

    // Free monad
    public <B> PureConsoleIO<B> flatMap(Function<A, PureConsoleIO<B>> f) {
        return cata(f, a -> PureConsoleIO.free(a.map(k -> k.flatMap(f))));
    }

    public <B> PureConsoleIO<B> $(Function<A, PureConsoleIO<B>> f) {
        return flatMap(f);
    }

    public abstract <B> B cata(
        Function<A, B> pure,
        Function<TerminalOperation<PureConsoleIO<A>>, B> free);

    //----------------------------------------------------------------------

    final static class Pure<A> extends PureConsoleIO<A> {
        private A a;

        public Pure(A a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<TerminalOperation<PureConsoleIO<A>>, B> free) {
            return pure.apply(a);
        }
    }


    public static <A> PureConsoleIO<A> pure(A a) {
        return new Pure<A>(a);
    }

    //----------------------------------------------------------------------

    final static class Free<A> extends PureConsoleIO<A> {
        private TerminalOperation<PureConsoleIO<A>> a;

        public Free(TerminalOperation<PureConsoleIO<A>> a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<TerminalOperation<PureConsoleIO<A>>, B> free) {
            return free.apply(a);
        }
    }

    public static <A> PureConsoleIO<A> free(TerminalOperation<PureConsoleIO<A>> a) {
        return new Free<A>(a);
    }

    public static <A,B> PureConsoleIO<B> forever(PureConsoleIO<A> x) {
        return x.flatMap(unused -> forever(x));
    }
}
