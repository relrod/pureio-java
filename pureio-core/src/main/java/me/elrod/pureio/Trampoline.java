package me.elrod.pureio;

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
    public abstract <R> R cata(
        final Function<A, R> pure,
        final Function<Identity<Trampoline<A>>, R> k);

    public abstract Either<Identity<Trampoline<A>>, A> resume();

    private static final class Suspend<A> extends Trampoline<A> {
        private final Identity<Trampoline<A>> suspension;
        private Suspend(final Identity<Trampoline<A>> s) {
            this.suspension = s;
        }
        public <R> R cata(
            final Function<A, R> pure,
            final Function<Identity<Trampoline<A>>, R> k) {
            return k.apply(this.suspension);
        }
        public Either<Identity<Trampoline<A>>, A> resume() {
            return Either.left(this.suspension);
        }
    }

    private static final class Pure<A> extends Trampoline<A> {
        private final A value;
        private Pure(final A x) {
            this.value = x;
        }
        public <R> R cata(
            final Function<A, R> pure,
            final Function<Identity<Trampoline<A>>, R> k) {
            return pure.apply(this.value);
        }
        public Either<Identity<Trampoline<A>>, A> resume() {
            return Either.right(this.value);
        }
    }

    public static <A> Trampoline<A> pure(final A x) {
        return new Pure<A>(x);
    }

    public static <A> Trampoline<A> suspend(final Identity<Trampoline<A>> x) {
        return new Suspend<A>(x);
    }

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
