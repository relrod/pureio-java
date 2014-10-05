package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Here we demonstrate how you might use the concept of a coproduct to
 * combine grammars of various types of I/O.
 *
 * In this particular case, we build up a coproduct over {@link TerminalOperation}
 * and {@link FileOperation}.
 *
 * Using coproducts causes the trampolining technique to no longer work (and
 * it would be nice to find a solution to this in the future), so we don't make
 * use of {@link TerminalOperationT} here.
 *
 * This structure makes use of {@link Either} and is very similar to
 * <code>Either&lt;TerminalOperation<A>, FileOperation<A>&gt;</code> except that we
 * write some useful instances over it. That is,
 * <code>A =&gt; F[A] \/ G[A]</code> is a functor, given functors F and G.
 * We can then construct a free monad over this to combine both grammars.
 *
 * In reality, coproducts, of course, generalize to any two functors (or
 * foldables or traversables, or comonads). But in Java, we are only able to
 * write one very specific instance of this construct.
 */
public class ConsoleFileCoproduct<A> {
    private Either<TerminalOperation<A>, FileOperation<A>> x;

    public ConsoleFileCoproduct(Either<TerminalOperation<A>, FileOperation<A>> either) {
        this.x = either;
    }

    /**
     * Lift a TerminalOperation.
     *
     * <br>
     * <code>
     * left :: f a -&gt; Coproduct f g a    <br>
     * left = Coproduct . Left
     * </code>
     */
    public static <A> ConsoleFileCoproduct<A> left(TerminalOperation<A> p) {
        return new ConsoleFileCoproduct<A>(Either.left(p));
    }

    /**
     * Lift a FileOperation.
     *
     * <br>
     * <code>
     * right :: g a -&gt; Coproduct f g a    <br>
     * right = Coproduct . Right
     * </code>
     */
    public static <A> ConsoleFileCoproduct<A> right(FileOperation<A> f) {
        return new ConsoleFileCoproduct<A>(Either.right(f));
    }

    /**
     * Catamorphism over the coproduct.
     *
     * <br>
     * <code>
     * coproduct :: (f a -&gt; b) -&gt; (g a -&gt; b) -&gt;
     * Coproduct f g a -&gt; b
     * <br>
     * coproduct f g = either f g . getCoproduct
     * </code>
     */
    public <X> X cata(Function<TerminalOperation<A>, X> p, Function<FileOperation<A>, X> f) {
        return this.x.cata(p, f);
    }

    /**
     * Functor map over the coproduct.
     *
     * <br>
     * <code>
     * instance (Functor f, Functor g) =&gt; Functor (Coproduct f g) where
     * fmap f = Coproduct . coproduct (Left . fmap f) (Right . fmap f)
     * </code>
     **/
    public <B> ConsoleFileCoproduct<B> map(Function<A, B> f) {
        return new ConsoleFileCoproduct<B>(
            this.cata(
                new Function<TerminalOperation<A>, Either<TerminalOperation<B>, FileOperation<B>>>() {
                    public Either<TerminalOperation<B>, FileOperation<B>> apply(TerminalOperation<A> a) {
                        return Either.left(a.map(f));
                    }
                },
                new Function<FileOperation<A>, Either<TerminalOperation<B>, FileOperation<B>>>() {
                    public Either<TerminalOperation<B>, FileOperation<B>> apply(FileOperation<A> a) {
                        return Either.right(a.map(f));
                    }
                }));
    }
}
