package me.elrod.pureio;

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
    public final <B> B foldRight(final Function<A, Function<B, B>> fn, final B b) {
        if (this.isEmpty())
            return b;
        else
            return fn.apply(this.head()).apply(this.tail().foldRight(fn, b));
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
     * Monadic unit
     */
    public static <A> LinkedList<A> unit(A a) {
        return new LinkedList.Cons<A>(a, new LinkedList.Nil<A>());
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
            throw new RuntimeException("TODO");
            //return new Cons<>(fn.apply(head), tail.map(fn));
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
