package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A trampolining free IO monad over {@link TerminalOperation}.
 *
 * {@link PureIOT} takes the same approach to trampolining as
 * {@link Trampoline} except it is specialized to {@link TerminalOperation}
 * because Java doesn't give us the ability to abstract over type constructors.
 *
 * So, much of this is like a huge copypaste mix of {@link PureIO} and
 * {@link Trampoline} into one mess of a structure.
 *
 * The end result, however, is a trampolining free IO monad.
 *
 * Where {@link Trampoline} is <code>Codensity (Free Identity)</code>, and
 * {@link PureIO} is <code>Free TerminalOperation</code>, {@link PureIOT}
 * is <code>Codensity (Free TerminalOperation)</code>.
 *
 * We include a {@link run} method which can be passed an interpreter. The
 * interpreter should use {@link TerminalOperation#cata}.
 */
public abstract class PureIOT<A> {
    private PureIOT() {}

    public abstract Either<TerminalOperation<PureIOT<A>>, A> resume();

    public abstract <R> R cata(
        final Function<Normal<A>, R> normal,
        final Function<Codensity<A>, R> codensity);

    /**
     * Monadic bind. Internally, this is reified to the type level, in order for
     * the trampolining technique to work.
     */
    public abstract <B> PureIOT<B> flatMap(final Function<A, PureIOT<B>> fn);

    /**
     * Functor map. This is implemented in terms of {@link flatMap}.
     */
    public <B> PureIOT<B> map(final Function<A, B> fn) {
        return this.flatMap(x -> pure(fn.apply(x)));
    }

    /**
     * Applicative pattern - function application.
     */
    public <B> PureIOT<B> applyVia(final PureIOT<Function<A, B>> fn) {
        return fn.flatMap(y -> flatMap(z -> pure(y.apply(z))));
    }

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
            return sub.cata(
                // Normal
                n -> n.normalCata(
                    // Pure(v)
                    v -> k.apply(v).resume(),

                    // More(kk)
                    kk -> Either.left(kk.map(x -> x.flatMap(k)))),

                // Codensity
                c -> c.sub.flatMap(o -> c.k.apply(o).flatMap(k)).resume());
        }
    }

    @SuppressWarnings("unchecked")
    protected static <A, B> Codensity<B> codensity(
        final Normal<A> a,
        final Function<A, PureIOT<B>> k) {
        return new Codensity<B>((Normal<Object>) a, (Function<Object, PureIOT<B>>) k);
    }

    protected static <A> PureIOT<A> pure(final A x) {
        return new Pure<A>(x);
    }

    protected static <A> PureIOT<A> suspend(final TerminalOperation<PureIOT<A>> x) {
        return new Suspend<A>(x);
    }

    /**
     * Monadic forever. Does a computation over and over again, indefinitely.
     */
    public static <A, B> PureIOT<B> forever(final PureIOT<A> x) {
        return x.flatMap(y -> forever(x));
    }

    /**
     * Run this monad with the passed interpreter.
     *
     * The structure of this method is taken from FunctionalJava.
     * https://github.com/functionaljava/functionaljava/blob/master/core/src/main/java/fj/control/Trampoline.java
     */
    public A run(Function<TerminalOperation<PureIOT<A>>, PureIOT<A>> interpreter) {
        PureIOT<A> current = this;
        while (true) {
            final Either<TerminalOperation<PureIOT<A>>, A> x = current.resume();
            if (x.isLeft())
                current = interpreter.apply(x.projectLeft().unsafeValue());
            else
                return x.projectRight().unsafeValue();
        }
    }
}
