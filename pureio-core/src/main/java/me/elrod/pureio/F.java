package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Various utility functions for working with functions.
 */
public class F {
    private F() {}

    /**
     * The constant function.
     */
    public static <A, B> Function<A, B> c(B x) {
        return a -> x;
    }
}
