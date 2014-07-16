package me.elrod.pureio;

import java.util.function.Function;

/**
 * The identity monad and comonad, used primarily for delaying evaluation.
 */
public abstract class Identity<T> {
    public abstract T run(); // Comonad : Identity<T> -> T

    public <U> Identity<U> map(Function<T, U> f) {
        return new Identity<U>() {
            public U run() {
                return f.apply(Identity.this.run());
            }
        };
    }

    public <U> Identity<U> flatMap(Function<T, Identity<U>> f) {
        return f.apply(run());
    }
}
