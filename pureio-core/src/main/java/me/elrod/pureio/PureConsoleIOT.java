package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A trampolining free IO monad over {@link TerminalOperation}.
 *
 * {@link PureConsoleIOT} takes the same approach to trampolining as
 * {@link Trampoline} except it is specialized to {@link TerminalOperation}
 * because Java doesn't give us the ability to abstract over type constructors.
 *
 * So, much of this is like a huge copypaste mix of {@link PureConsoleIO} and
 * {@link Trampoline} into one mess of a structure.
 *
 * The end result, however, is a trampolining free IO monad.
 *
 * Where {@link Trampoline} is <code>Codensity (Free Identity)</code>, and
 * {@link PureConsoleIO} is <code>Free TerminalOperation</code>, {@link PureConsoleIOT}
 * is <code>Codensity (Free TerminalOperation)</code>.
 *
 * We include a {@link run} method which can be passed an interpreter. The
 * interpreter should use {@link TerminalOperation#cata}.
 */
public abstract class PureConsoleIOT<A> {
    private PureConsoleIOT() {}

    public abstract Either<TerminalOperation<PureConsoleIOT<A>>, A> resume();

    public abstract <R> R cata(
        final Function<Normal<A>, R> normal,
        final Function<Codensity<A>, R> codensity);

    /**
     * Monadic bind. Internally, this is reified to the type level, in order for
     * the trampolining technique to work.
     */
    public abstract <B> PureConsoleIOT<B> flatMap(final Function<A, PureConsoleIOT<B>> fn);

    /**
     * Functor map. This is implemented in terms of {@link flatMap}.
     */
    public <B> PureConsoleIOT<B> map(final Function<A, B> fn) {
        return this.flatMap(x -> pure(fn.apply(x)));
    }

    /**
     * Applicative pattern - function application.
     */
    public <B> PureConsoleIOT<B> applyVia(final PureConsoleIOT<Function<A, B>> fn) {
        return fn.flatMap(y -> flatMap(z -> pure(y.apply(z))));
    }

    private static abstract class Normal<A> extends PureConsoleIOT<A> {
        public abstract <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<PureConsoleIOT<A>>, R> suspend);

        public <B> PureConsoleIOT<B> flatMap(final Function<A, PureConsoleIOT<B>> fn) {
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
            final Function<TerminalOperation<PureConsoleIOT<A>>, R> suspend) {
            return pure.apply(this.value);
        }

        public Either<TerminalOperation<PureConsoleIOT<A>>, A> resume() {
            return Either.right(this.value);
        }
    }

    private static final class Suspend<A> extends Normal<A> {
        private final TerminalOperation<PureConsoleIOT<A>> suspension;

        private Suspend(final TerminalOperation<PureConsoleIOT<A>> s) {
            this.suspension = s;
        }

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return normal.apply(this);
        }

        public <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<PureConsoleIOT<A>>, R> suspend) {
            return suspend.apply(this.suspension);
        }
        public Either<TerminalOperation<PureConsoleIOT<A>>, A> resume() {
            return Either.left(this.suspension);
        }
    }

    private static final class Codensity<A> extends PureConsoleIOT<A> {
        private final Normal<Object> sub;
        private final Function<Object, PureConsoleIOT<A>> k;

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return codensity.apply(this);
        }

        private Codensity(Normal<Object> sub, Function<Object, PureConsoleIOT<A>> k) {
            this.sub = sub;
            this.k = k;
        }

        public <B> PureConsoleIOT<B> flatMap(final Function<A, PureConsoleIOT<B>> fn) {
            return codensity(
                sub,
                o -> k.apply(o).flatMap(fn));
        }

        public Either<TerminalOperation<PureConsoleIOT<A>>, A> resume() {
            return sub.cata(
                // Normal
                n -> n.normalCata(
                    // Pure(v)
                    v -> k.apply(v).resume(),

                    // More(kk)
                    // Due to a regression from java 1.8.0_11 to 1.8.0_20 (and 1.8.0_40), these types
                    // are *required* to be written out fully.
                    new Function<TerminalOperation<PureConsoleIOT<Object>>, Either<TerminalOperation<PureConsoleIOT<A>>, A>>() {
                        public Either<TerminalOperation<PureConsoleIOT<A>>, A> apply(TerminalOperation<PureConsoleIOT<Object>> kk) {
                            return Either.left(kk.map(x -> x.flatMap(k)));
                        }
                    }),

                // Codensity
                c -> c.sub.flatMap(o -> c.k.apply(o).flatMap(k)).resume());
        }
    }

    @SuppressWarnings("unchecked")
    protected static <A, B> Codensity<B> codensity(
        final Normal<A> a,
        final Function<A, PureConsoleIOT<B>> k) {
        return new Codensity<B>((Normal<Object>) a, (Function<Object, PureConsoleIOT<B>>) k);
    }

    protected static <A> PureConsoleIOT<A> pure(final A x) {
        return new Pure<A>(x);
    }

    protected static <A> PureConsoleIOT<A> suspend(final TerminalOperation<PureConsoleIOT<A>> x) {
        return new Suspend<A>(x);
    }

    /**
     * Monadic forever. Does a computation over and over again, indefinitely.
     */
    public static <A, B> PureConsoleIOT<B> forever(final PureConsoleIOT<A> x) {
        return x.flatMap(y -> forever(x));
    }

    /**
     * Run this monad with the passed interpreter.
     *
     * The structure of this method is taken from FunctionalJava.
     * https://github.com/functionaljava/functionaljava/blob/master/core/src/main/java/fj/control/Trampoline.java
     */
    public A run(Function<TerminalOperation<PureConsoleIOT<A>>, PureConsoleIOT<A>> interpreter) {
        PureConsoleIOT<A> current = this;
        while (true) {
            final Either<TerminalOperation<PureConsoleIOT<A>>, A> x = current.resume();
            if (x.isLeft())
                current = interpreter.apply(x.projectLeft().unsafeValue());
            else
                return x.projectRight().unsafeValue();
        }
    }
}
