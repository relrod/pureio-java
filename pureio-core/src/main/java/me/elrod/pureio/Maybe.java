package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An optional type.
 */
public abstract class Maybe<T> {
    private Maybe() {}

    /**
     * Catamorphism over Maybe<A>.
     */
    public abstract <U> U cata(
        Function<T, U> some,
        U none);

    // Functor
    public <U> Maybe<U> fmap(final Function<T, U> fn) {
        return this.cata(
            new Function<T, Maybe<U>>() {
                public Maybe<U> apply(T x) {
                    return unit(fn.apply(x));
                }
            },
            new None<U>());
    }

    // Applicative
    public <U> Maybe<U> applyVia(final Maybe<Function<T,U>> ofn) {
        return ofn.cata(
            new Function<Function<T, U>, Maybe<U>>() {
                public Maybe<U> apply(final Function<T, U> fn) {
                    return Maybe.this.fmap(fn);
                }
            },
            new None<U>());
    }

    // Monad
    public static <U> Maybe<U> unit(U x) {
        return new Some<U>(x);
    }

    public <U> Maybe<U> bind(final Function<T, Maybe<U>> fn) {
        return this.cata(
            new Function<T, Maybe<U>>() {
                public Maybe<U> apply(T x) {
                    return fn.apply(x);
                }
            },
            new None<U>());
    }

    public final static class Some<T> extends Maybe<T> {
        private T x;
        public Some(T x) {
            this.x = x;
        }
        public <U> U cata(final Function<T, U> some, final U none) {
            return some.apply(x);
        }
    }

    public final static class None<T> extends Maybe<T> {
        public None() { }
        public <U> U cata(final Function<T, U> some, final U none) {
            return none;
        }
    }
}
