package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class PureIOT<A> {
    private PureIOT() {}

    public abstract Either<TerminalOperation<PureIOT<A>>, A> resume();

    public abstract <R> R cata(
        final Function<Normal<A>, R> normal,
        final Function<Codensity<A>, R> codensity);

    public abstract <B> PureIOT<B> flatMap(final Function<A, PureIOT<B>> fn);

    private static abstract class Normal<A> extends PureIOT<A> {
        public abstract <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<PureIOT<A>>, R> suspend);

        public <B> PureIOT<B> flatMap(final Function<A, PureIOT<B>> fn) {
            return codensity(this, fn);
        }
    }

    private static final class Pure<A> extends Normal<A> {
        private final A value;

        private Pure(final A x) {
            this.value = x;
        }

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return normal.apply(this);
        }

        public <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<PureIOT<A>>, R> suspend) {
            return pure.apply(this.value);
        }

        public Either<TerminalOperation<PureIOT<A>>, A> resume() {
            return Either.right(this.value);
        }
    }

    private static final class Suspend<A> extends Normal<A> {
        private final TerminalOperation<PureIOT<A>> suspension;

        private Suspend(final TerminalOperation<PureIOT<A>> s) {
            this.suspension = s;
        }

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return normal.apply(this);
        }

        public <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<PureIOT<A>>, R> suspend) {
            return suspend.apply(this.suspension);
        }
        public Either<TerminalOperation<PureIOT<A>>, A> resume() {
            return Either.left(this.suspension);
        }
    }

    private static final class Codensity<A> extends PureIOT<A> {
        private final Normal<Object> sub;
        private final Function<Object, PureIOT<A>> k;

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return codensity.apply(this);
        }

        private Codensity(Normal<Object> sub, Function<Object, PureIOT<A>> k) {
            this.sub = sub;
            this.k = k;
        }

        public <B> PureIOT<B> flatMap(final Function<A, PureIOT<B>> fn) {
            return codensity(
                sub,
                o -> k.apply(o).flatMap(fn));
        }

        public Either<TerminalOperation<PureIOT<A>>, A> resume() {
            return cata(
                // Normal
                n -> n.normalCata(
                    // Pure(v)
                    v -> k.apply(v).resume(),

                    // More(kk)
                    kk -> { throw new RuntimeException("TODO!"); }),

                // Codensity
                c -> c.sub.flatMap(o -> c.k.apply(o).flatMap(k)));
        }
    }

    @SuppressWarnings("unchecked")
    protected static <A, B> Codensity<B> codensity(
        final Normal<A> a,
        final Function<A, PureIOT<B>> k) {
        return new Codensity<B>((Normal<Object>) a, (Function<Object, PureIOT<B>>) k);
    }

    public static <A> PureIOT<A> pure(final A x) {
        return new Pure<A>(x);
    }

    public static <A> PureIOT<A> suspend(final TerminalOperation<PureIOT<A>> x) {
        return new Suspend<A>(x);
    }

    // This is taken almost directly from FJ for now.
    // Credit:
    // https://github.com/functionaljava/functionaljava/blob/master/core/src/main/java/fj/control/PureIOT.java
    public A run() {
        PureIOT<A> current = this;
        while (true) {
            final Either<TerminalOperation<PureIOT<A>>, A> x = current.resume();
            if (x.isLeft()) {
                Either.LeftP<TerminalOperation<PureIOT<A>>, A> y = x.projectLeft();
                // TODO: Broken - do we need to use Identity again somehow?
                current = y.unsafeValue();
            } else {
                Either.RightP<TerminalOperation<PureIOT<A>>, A> y = x.projectRight();
                return y.unsafeValue();
            }
        }
    }
}