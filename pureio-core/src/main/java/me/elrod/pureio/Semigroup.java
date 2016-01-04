package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A semigroup is an associative magma. That is, it is a structure with an
 * associative, binary operator satisfying the following law:
 *
 * ∀x y z. append(x, append(y, z)) == append(append(x, y), z)
 */
public class Semigroup<A> {
    private final BiFunction<A, A, A> append;

    public Semigroup(final BiFunction<A, A, A> append) {
        this.append = append;
    }

    /**
     * This works around a silly Java type inference bug.
     */
    public static <A> Semigroup<A> semigroup(BiFunction<A, A, A> append) {
        return new Semigroup<A>(append);
    }

    /**
     * Semigroup instance for LinkedList.
     */
    public static <A> Semigroup<LinkedList<A>> linkedListSemigroup() {
        return semigroup((x, y) -> LinkedList.append(x, y));
    }

    /**
     * Semigroup instance for Maybe.
     */
    public static <A> Semigroup<Maybe<A>> maybeSemigroup() {
        return semigroup(
            (x, y) -> x.cata(F.c(x), y));
    }
}
