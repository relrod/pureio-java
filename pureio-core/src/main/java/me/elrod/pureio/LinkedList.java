package me.elrod.pureio;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A simple, purely functional linked-list implementation.
 *
 * We are already re-inventing most of the world, so why not?
 * This doesn't seem to exist in the Java stdlib. If/when we depend on FJ, we
 * should use their List<A>. For now, implement the basics.
 */
public abstract class LinkedList<A> /* extends Iterable<A> */ {
    private LinkedList() {}
    public abstract A head();
    public abstract LinkedList<A> tail();

    public final LinkedList<A> cons(final A a) {
        return new Cons<A>(a, this);
    }

    public final boolean isEmpty() {
        return this instanceof Nil;
    }

    /**
     * Catamorphism.
     *
     * This does **NOT** trampoline and therefore will overflow the stack very
     * quickly.
     */
    public final <B> B foldRight(final BiFunction<A, B, B> fn, final B b) {
        if (this.isEmpty())
            return b;
        else
            return fn.apply(this.head(), this.tail().foldRight(fn, b));
    }

    /**
     * Functor map.
     *
     * This does **NOT** trampoline and therefore will overflow the stack very
     * quickly.
     */
    public abstract <B> LinkedList<B> map(Function<A, B> fn);

    /**
     * (semi)monadic bind.
     *
     * This does **NOT** trampoline and therefore will overflow the stack very
     * quickly.
     */
    public abstract <B> LinkedList<B> flatMap(Function<A, LinkedList<B>> fn);

    /**
     * Pretty "infix-alike" syntax for flatMap.
     */
   public <B> LinkedList<B> $(Function<A, LinkedList<B>> f) {
        return flatMap(f);
    }

    /**
     * Monadic/Applicative point
     */
    public static <A> LinkedList<A> point(A a) {
        return new LinkedList.Cons<A>(a, new LinkedList.Nil<A>());
    }

    private static <A> LinkedList<A> reverseHelper(LinkedList<A> initial, LinkedList<A> as) {
        /**
           reverse l =  rev l []
           where
           rev []     a = a
           rev (x:xs) a = rev xs (x:a)
        */
        if (as.isEmpty())
            return initial;
        else
            return reverseHelper(initial.cons(as.head()), as.tail());
    }

    /**
     * Reverse the list.
     *
     * This does **NOT** trampoline and therefore will overflow the stack very
     * quickly.
     */
    public LinkedList<A> reverse() {
        return reverseHelper(new Nil<A>(), this);
    }

    private static <A> LinkedList<A> fromArrayHelper(LinkedList<A> initial, A[] as) {
        if (as.length > 0)
            return fromArrayHelper(initial.cons(as[0]), Arrays.copyOfRange(as, 1, as.length));
        else
            return initial;
    }

    /**
     * Convert an array of As to a LinkedList of As.
     *
     * This does **NOT** trampoline and therefore will overflow the stack very
     * quickly.
     */
    public static <A> LinkedList<A> fromArray(A[] as) {
        return fromArrayHelper(new Nil<A>(), as).reverse();
    }

    /**
     * Append two lists together.
     *
     * This does **NOT** trampoline and therefore will overflow the stack very
     * quickly.
     */
    public static <A> LinkedList<A> append(LinkedList<A> as, LinkedList<A> bs) {
        if (as instanceof Nil) {
            return bs;
        } else {
            // (++) (x:xs) ys = x : xs ++ ys
            return append(as.tail(), bs).cons(as.head());
        }
    }

    /**
     * Concatenate a list of lists into a list.
     *
     * Calls {@link foldRight} and therefore does not currently trampoline.
     */
    @SuppressWarnings("unchecked")
    public static <A> LinkedList<A> concat(LinkedList<LinkedList<A>> xs) {
        // concat = foldr (++) []
        return xs.foldRight(
            new BiFunction<LinkedList<A>, LinkedList<A>, LinkedList<A>>() {
                public LinkedList<A> apply(LinkedList<A> as, LinkedList<A> bs) {
                    return LinkedList.append(as, bs);
                }
            },
            (LinkedList<A>)new Nil<LinkedList<A>>());
    }

    public final static class Cons<A> extends LinkedList<A> {
        A head;
        LinkedList<A> tail;

        public Cons(A head, LinkedList<A> tail) {
            this.head = head;
            this.tail = tail;
        }

        public A head() {
            return this.head;
        }

        public LinkedList<A> tail() {
            return this.tail;
        }

        public <B> LinkedList<B> map(Function<A, B> fn) {
            return new Cons<>(fn.apply(head), tail.map(fn));
        }

        public <B> LinkedList<B> flatMap(Function<A, LinkedList<B>> fn) {
            return concat(map(fn));
        }
    }

    public final static class Nil<A> extends LinkedList<A> {
        public A head() {
            throw new RuntimeException("head() called on an empty list");
        }

        public LinkedList<A> tail() {
            throw new RuntimeException("tail() called on an empty list");
        }

        public <B> LinkedList<B> map(Function<A, B> fn) {
            return new Nil<B>();
        }

        public <B> LinkedList<B> flatMap(Function<A, LinkedList<B>> fn) {
            return new Nil<B>();
        }
    }
}
