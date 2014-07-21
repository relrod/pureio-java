package me.elrod.pureio;

import java.util.function.Function;

/**
 * The identity monad and comonad, used primarily for delaying evaluation.
 */
public abstract class Identity<T> {
    /**
     * Comonadic extraction. A.k.a "Get the value out."
     */
    public abstract T run();

    /**
     * Functor map.
     */
    public <U> Identity<U> map(Function<T, U> f) {
        return new Identity<U>() {
            public U run() {
                return f.apply(Identity.this.run());
            }
        };
    }

    /**
     * Applicative map.
     */
    public <U> Identity<U> applyVia(Identity<Function<T, U>> f) {
        return new Identity<U>() {
            public U run() {
                return f.run().apply(Identity.this.run());
            }
        };
    }

    /**
     * Monadic bind.
     */
    public <U> Identity<U> flatMap(Function<T, Identity<U>> f) {
        return f.apply(run());
    }
}
