package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class TrampolinedPureIO<A> {
    private TrampolinedPureIO() {}

    /**
     * Perform one step of the computation and return the next step.
     */
    public abstract Either<TerminalOperation<TrampolinedPureIO<A>>, A> resume();

    public abstract <R> R cata(
        final Function<Normal<A>, R> normal,
        final Function<Codensity<A>, R> codensity);

    public abstract <B> TrampolinedPureIO<B> flatMap(final Function<A, TrampolinedPureIO<B>> fn);

    private static abstract class Normal<A> extends TrampolinedPureIO<A> {
        public abstract <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<TrampolinedPureIO<A>>, R> suspend);

        public <B> TrampolinedPureIO<B> flatMap(final Function<A, TrampolinedPureIO<B>> fn) {
            return codensity(this, fn);
        }
    }

    private static final class Suspend<A> extends Normal<A> {
        private final TerminalOperation<TrampolinedPureIO<A>> suspension;

        private Suspend(final TerminalOperation<TrampolinedPureIO<A>> s) {
            this.suspension = s;
        }

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return normal.apply(this);
        }

        public <R> R normalCata(
            final Function<A, R> pure,
            final Function<TerminalOperation<TrampolinedPureIO<A>>, R> suspend) {
            return suspend.apply(this.suspension);
        }

        public Either<TerminalOperation<TrampolinedPureIO<A>>, A> resume() {
            return Either.left(this.suspension);
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
            final Function<TerminalOperation<TrampolinedPureIO<A>>, R> suspend) {
            return pure.apply(this.value);
        }

        public Either<TerminalOperation<TrampolinedPureIO<A>>, A> resume() {
            return Either.right(this.value);
        }
    }

    /**
     * Unfortunately, limits of Java's type system force us to use Object
     * extensively here. However, this is private and it will be correct by
     * construction before it is ever used.
     */
    private static final class Codensity<A> extends TrampolinedPureIO<A> {
        private final Normal<Object> sub;
        private final Function<Object, TrampolinedPureIO<A>> k;

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return codensity.apply(this);
        }

        private Codensity(Normal<Object> sub, Function<Object, TrampolinedPureIO<A>> k) {
            this.sub = sub;
            this.k = k;
        }

        public <B> TrampolinedPureIO<B> flatMap(final Function<A, TrampolinedPureIO<B>> fn) {
            return codensity(
                sub,
                o -> suspend(new TerminalOperation<TrampolinedPureIO<B>>() {
                        public TrampolinedPureIO<B> run() {
                            return k.apply(o).flatMap(fn);
                        }
                    }));
        }

        public Either<TerminalOperation<TrampolinedPureIO<A>>, A> resume() {
            return Either.left(
                sub.resume().cata(
                    // Left
                    p -> p.map(
                        ot -> ot.cata(
                            o -> o.normalCata(
                                obj -> k.apply(obj),
                                t -> t.run().flatMap(k)),
                            c -> codensity(
                                c.sub,
                                o -> c.k.apply(o).flatMap(k)))),
                    // Right
                    o -> new TerminalOperation<TrampolinedPureIO<A>>() {
                        public TrampolinedPureIO<A> run() {
                            return k.apply(o);
                        }
                    }));
        }
    }

    @SuppressWarnings("unchecked")
    protected static <A, B> Codensity<B> codensity(
        final Normal<A> a,
        final Function<A, TrampolinedPureIO<B>> k) {
        return new Codensity<B>((Normal<Object>) a, (Function<Object, TrampolinedPureIO<B>>) k);
    }

    public static <A> TrampolinedPureIO<A> pure(final A x) {
        return new Pure<A>(x);
    }

    public static <A> TrampolinedPureIO<A> suspend(final TerminalOperation<TrampolinedPureIO<A>> x) {
        return new Suspend<A>(x);
    }

    // This is taken almost directly from FJ for now.
    // Credit:
    // https://github.com/functionaljava/functionaljava/blob/master/core/src/main/java/fj/control/Trampolined.java
    public A run() {
        TrampolinedPureIO<A> current = this;
        while (true) {
            final Either<TerminalOperation<TrampolinedPureIO<A>>, A> x = current.resume();
            if (x.isLeft()) {
                Either.LeftP<TerminalOperation<TrampolinedPureIO<A>>, A> y = x.projectLeft();
                current = y.unsafeValue().run();
            } else {
                Either.RightP<TerminalOperation<TrampolinedPureIO<A>>, A> y = x.projectRight();
                return y.unsafeValue();
            }
        }
    }
}
