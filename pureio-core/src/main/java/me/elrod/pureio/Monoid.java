package me.elrod.pureio;

import java.util.function.BiFunction;
import me.elrod.pureio.LinkedList;
import me.elrod.pureio.Semigroup;

/**
 * A monoid is a semigroup with an identity element, satisfying the
 * following laws in addition to the semigroup laws:
 *
 * ∀x. append(unit, x) == x
 * ∀x. append(x, unit) == x
 */
public class Monoid<A> {
    private final BiFunction<A, A, A> append;
    private final A unit;

    public Monoid(final BiFunction<A, A, A> append, final A unit) {
        this.append = append;
        this.unit = unit;
    }

    /**
     * This works around a silly Java type inference bug.
     */
    public static <A> Monoid<A> monoid(
        BiFunction<A, A, A> append,
        final A unit) {
        return new Monoid<A>(append, unit);
    }

    /**
     * Projection to a semigroup.
     */
    Semigroup<A> toSemigroup() {
        return new Semigroup<A>(this.append);
    }

    /**
     * Monoid instance for LinkedList.
     */
    public static <A> Monoid<LinkedList<A>> linkedListMonoid() {
        return monoid((x, y) -> LinkedList.append(x, y), new LinkedList.Nil<A>());
    }

    /**
     * Monoid instance for Maybe.
     */
    public static <A> Monoid<Maybe<A>> maybeMonoid() {
        return monoid(
            (x, y) -> x.cata(F.c(x), y),
            new Maybe.None<A>());
    }
}
