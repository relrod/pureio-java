package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A trampoline is a sum type with two basic constructors: Pure and
 * Suspend. A Pure is a leaf value produced at the end of a tree of
 * computations; A Suspend holds a computation that can be resumed.
 *
 * We use {@link Either<A, B>} to construct the tree.
 */
public abstract class Trampoline<A> {
    private Trampoline() {}

    /**
     * Perform one step of the computation and return the next step.
     */
    public abstract Either<Identity<Trampoline<A>>, A> resume();

    public abstract <R> R cata(
        final Function<Normal<A>, R> normal,
        final Function<Codensity<A>, R> codensity);

    public abstract <B> Trampoline<B> flatMap(final Function<A, Trampoline<B>> fn);

    private static abstract class Normal<A> extends Trampoline<A> {
        public abstract <R> R normalCata(
            final Function<A, R> pure,
            final Function<Identity<Trampoline<A>>, R> suspend);

        public <B> Trampoline<B> flatMap(final Function<A, Trampoline<B>> fn) {
            return codensity(this, fn);
        }
    }

    private static final class Suspend<A> extends Normal<A> {
        private final Identity<Trampoline<A>> suspension;
        private Suspend(final Identity<Trampoline<A>> s) {
            this.suspension = s;
        }

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return normal.apply(this);
        }

        public <R> R normalCata(
            final Function<A, R> pure,
            final Function<Identity<Trampoline<A>>, R> suspend) {
            return suspend.apply(this.suspension);
        }
        public Either<Identity<Trampoline<A>>, A> resume() {
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
            final Function<Identity<Trampoline<A>>, R> suspend) {
            return pure.apply(this.value);
        }

        public Either<Identity<Trampoline<A>>, A> resume() {
            return Either.right(this.value);
        }
    }

    /**
     * Unfortunately, limits of Java's type system force us to use Object
     * extensively here. However, this is private and it will be correct by
     * construction before it is ever used.
     */
    private static final class Codensity<A> extends Trampoline<A> {
        private final Normal<Object> sub;
        private final Function<Object, Trampoline<A>> k;

        public <R> R cata(
            final Function<Normal<A>, R> normal,
            final Function<Codensity<A>, R> codensity) {
            return codensity.apply(this);
        }

        private Codensity(Normal<Object> sub, Function<Object, Trampoline<A>> k) {
            this.sub = sub;
            this.k = k;
        }

        public <B> Trampoline<B> flatMap(final Function<A, Trampoline<B>> fn) {
            return codensity(
                sub,
                o -> suspend(new Identity<Trampoline<B>>() {
                        public Trampoline<B> run() {
                            return k.apply(o).flatMap(fn);
                        }
                    }));
        }

        public Either<Identity<Trampoline<A>>, A> resume() {
            throw new RuntimeException("TODO!");
        }
    }

    @SuppressWarnings("unchecked")
    protected static <A, B> Codensity<B> codensity(
        final Normal<A> a,
        final Function<A, Trampoline<B>> k) {
        return new Codensity<B>((Normal<Object>) a, (Function<Object, Trampoline<B>>) k);
    }

    /*
    private static final class FlatMap<A, B> extends Trampoline<A> {
        private final Trampoline<A> sub;
        private final Function<A, Trampoline<B>> k;

        private FlatMap(final Trampoline<A> sub, final Function<A, Trampoline<B>> k) {
            this.sub = sub;
            this.k = k;
        }

        public <R> R cata(
            final Function<A, R> pure,
            final Function<Identity<Trampoline<A>>, R> suspend,
            final BiFunction<Trampoline<A>, Function<A, Trampoline<R>>, R> flatmap) {
            return flatmap.apply(this.sub, this.k);
        }

        public Either<Identity<Trampoline<A>>, A> resume() {
            return null;
            sub.cata(
                // Pure
                v -> k.apply(v).resume(),
                // Suspend
                k2 -> Either.left(new Identity<Trampoline<A>>() {
                        public Trampoline<A> run() {
                            return flatmap(k2.run(), k);
                        }
                    }),
                // FlatMap
                (b, g) -> (new FlatMap<Trampoline<A>, Object>(
                               b,
                               x -> new FlatMap<Trampoline<A>, Object>(g.apply(x), k)
                               )).resume());
        }
    }
    */

    public static <A> Trampoline<A> pure(final A x) {
        return new Pure<A>(x);
    }

    public static <A> Trampoline<A> suspend(final Identity<Trampoline<A>> x) {
        return new Suspend<A>(x);
    }

    /*
    public static <A, B> Trampoline<A> flatmap(final Trampoline<A> sub, final Function<A, Trampoline<B>> k) {
        return new FlatMap<A, B>(sub, k);
    }
    */

    // This is taken almost directly from FJ for now.
    // Credit:
    // https://github.com/functionaljava/functionaljava/blob/master/core/src/main/java/fj/control/Trampoline.java
    public A run() {
        Trampoline<A> current = this;
        while (true) {
            final Either<Identity<Trampoline<A>>, A> x = current.resume();
            // TODO: Can we use Either#cata here?
            if (x.isLeft()) {
                Either.LeftP<Identity<Trampoline<A>>, A> y = x.projectLeft();
                current = y.unsafeValue().run();
            } else {
                // We hit the end of the tree
                Either.RightP<Identity<Trampoline<A>>, A> y = x.projectRight();
                return y.unsafeValue();
            }
        }
    }
}
