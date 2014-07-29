package me.elrod.pureio;

import java.util.function.Function;

/**
 * Disjunction sum type.
 *
 * This is very simplified and provides only basic operations.
 *
 * It does have projections which can be used to get values out. We need this
 * functionality for {@link Trampoline}.
 *
 * The projections do not currently implement monadic operations, but probably
 * should.
 *
 * Also, this is verbose and annoying right now.
 */
public abstract class Either<A, B> {
    private Either() {}
    public abstract <X> X cata(Function<A, X> left, Function<B, X> right);
    public abstract boolean isLeft();
    public abstract boolean isRight();

    // Left
    private static final class Left<A, B> extends Either<A, B> {
        private final A x;
        public Left(A x) {
            this.x = x;
        }
        public A value() {
            return this.x;
        }
        public boolean isLeft() { return true; }
        public boolean isRight() { return false; }
        public <X> X cata(Function<A, X> left, Function<B, X> right) {
            return left.apply(x);
        }
    }

    // Right
    private static final class Right<A, B> extends Either<A, B> {
        private B x;
        public Right(B x) {
            this.x = x;
        }
        public B value() {
            return this.x;
        }
        public boolean isLeft() { return false; }
        public boolean isRight() { return true; }
        public <X> X cata(Function<A, X> left, Function<B, X> right) {
            return right.apply(x);
        }
    }

    // Interface
    public static <A, B> Either<A, B> left(A x) { return new Left<A, B>(x); }
    public static <A, B> Either<A, B> right(B x) { return new Right<A, B>(x); }

    // Projections
    public LeftP<A, B> projectLeft() {
        return new LeftP<A, B>(this);
    }

    public RightP<A, B> projectRight() {
        return new RightP<A, B>(this);
    }

    /**
     * Left projection of {@link Either}.
     */
    protected static final class LeftP<A, B> {
        private final Either<A, B> original;
        private LeftP(final Either<A, B> original) {
            this.original = original;
        }

        /**
         * Get the value out of the projection.
         *
         * Unsafe (acts as comonadic <code>extract</code>), throws a
         * {@link RuntimeException} if called on a <code>Right</code>.
         */
        public A unsafeValue() {
            if (original.isLeft())
                return ((Left<A, B>) original).x;
            else
                throw new RuntimeException("Left projection on Right Either");
        }
    }

    /**
     * Right projection of {@link Either}.
     */
    protected static final class RightP<A, B> {
        private final Either<A, B> original;
        private RightP(final Either<A, B> original) {
            this.original = original;
        }

        /**
         * Get the value out of the projection.
         *
         * Unsafe (acts as comonadic <code>extract</code>), throws a
         * {@link RuntimeException} if called on a <code>Left</code>.
         */
        public B unsafeValue() {
            if (original.isRight())
                return ((Right<A, B>) original).x;
            else
                throw new RuntimeException("Right projection on Left Either");
        }
    }
}
