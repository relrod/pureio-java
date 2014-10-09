package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <code>Free (F =&gt;</code> {@link ConsoleFileCoproduct}<code>)</code>.
 */
public abstract class ConsoleFileFree<A> {
    private ConsoleFileFree() {
    }

    // Functor
    public <B> ConsoleFileFree<B> map(Function<A, B> f) {
        return cata(
            a -> ConsoleFileFree.pure(f.apply(a)),
            a -> ConsoleFileFree.free(a.map(k -> k.map(f))));
    }

    // Free monad
    public <B> ConsoleFileFree<B> flatMap(Function<A, ConsoleFileFree<B>> f) {
        return cata(
            f,
            a -> ConsoleFileFree.free(a.map(k -> k.flatMap(f))));
    }

    public abstract <B> B cata(
        Function<A, B> pure,
        Function<ConsoleFileCoproduct<ConsoleFileFree<A>>, B> free);

    //----------------------------------------------------------------------

    final static class Pure<A> extends ConsoleFileFree<A> {
        private A a;

        public Pure(A a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<ConsoleFileCoproduct<ConsoleFileFree<A>>, B> free) {
            return pure.apply(a);
        }
    }


    public static <A> ConsoleFileFree<A> pure(A a) {
        return new Pure<A>(a);
    }

    //----------------------------------------------------------------------

    final static class Free<A> extends ConsoleFileFree<A> {
        private ConsoleFileCoproduct<ConsoleFileFree<A>> a;

        public Free(ConsoleFileCoproduct<ConsoleFileFree<A>> a) {
            this.a = a;
        }

        public <B> B cata(
            Function<A, B> pure,
            Function<ConsoleFileCoproduct<ConsoleFileFree<A>>, B> free) {
            return free.apply(a);
        }
    }

    public static <A> ConsoleFileFree<A> free(ConsoleFileCoproduct<ConsoleFileFree<A>> a) {
        return new Free<A>(a);
    }

    /**
     * Evaluate each action in the sequence from left to right and collect the
     * results.
     *
     * <br>
     * <code>
     * sequence :: Monad m =&gt; [m a] -&gt; m [a]
     * sequence ms = foldr k (return []) ms
     *      where
     *        k m m' = do { x &lt;- m; xs &lt;- m'; return (x:xs) }
     * </code>
     */
    public static <A> ConsoleFileFree<LinkedList<A>> sequence(LinkedList<ConsoleFileFree<A>> ll) {
        return ll.foldRight(
            // k :: Monad m => m a -> m [a] -> m [a]
            (a,b) -> a.flatMap(aPrime -> b.flatMap(bPrime -> ConsoleFileFree.pure(bPrime.cons(aPrime)))),
            ConsoleFileFree.pure(new LinkedList.Nil<A>()));
    }

    /**
     * Same as <code>sequence ∘ map</code>.
     *
     * <br>
     * <code>
     * mapM :: Monad m =&gt; (a -&gt; m b) -&gt; [a] -&gt; m [b]
     * mapM f as = sequence (map f as)
     * </code>
     */
    public static <A, B> ConsoleFileFree<LinkedList<B>> mapM(Function<A, ConsoleFileFree<B>> f, LinkedList<A> ll) {
        return sequence(ll.map(f));
    }
}
