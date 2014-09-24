package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Here we demonstrate how you might use the concept of a coproduct to
 * combine grammars of various types of I/O.
 *
 * In this particular case, we build up a coproduct over {@link PureConsoleIO}
 * and {@link PureFileIO}.
 *
 * Using coproducts causes the trampolining technique to no longer work (and
 * it would be nice to find a solution to this in the future), so we don't make
 * use of {@link PureConsoleIOT} here.
 *
 * This structure makes use of {@link Either} and is very similar to
 * <code>Either<PureConsoleIO<A>, PureFileIO<A>></code> except that we write some
 * useful instances over it. That is,
 * <code>A =&gt; F[A] \/ G[A]</code> is a functor, given functors F and G.
 * We can then construct a free monad over this to combine both grammars.
 *
 * In reality, coproducts, of course, generalize to any two functors (or
 * foldables or traversables, or comonads). But in Java, we are only able to
 * write one very specific instance of this construct.
 */
public class ConsoleFileCoproduct<A> {
    private Either<PureConsoleIO<A>, PureFileIO<A>> x;

    public ConsoleFileCoproduct(Either<PureConsoleIO<A>, PureFileIO<A>> either) {
        this.x = either;
    }

    /**
     * Lift a PureConsoleIO.
     *
     * <br>
     * <code>
     * left :: f a -> Coproduct f g a    <br>
     * left = Coproduct . Left
     * </code>
     */
    public static <A> ConsoleFileCoproduct<A> left(PureConsoleIO<A> p) {
        return new ConsoleFileCoproduct<A>(Either.left(p));
    }

    /**
     * Lift a PureFileIO.
     *
     * <br>
     * <code>
     * right :: g a -> Coproduct f g a    <br>
     * right = Coproduct . Right
     * </code>
     */
    public static <A> ConsoleFileCoproduct<A> right(PureFileIO<A> f) {
        return new ConsoleFileCoproduct<A>(Either.right(f));
    }

    /**
     * Catamorphism over the coproduct.
     *
     * <br>
     * <code>
     * coproduct :: (f a -> b) -> (g a -> b) -> Coproduct f g a -> b    <br>
     * coproduct f g = either f g . getCoproduct
     * </code>
     */
    public <X> X cata(Function<PureConsoleIO<A>, X> p, Function<PureFileIO<A>, X> f) {
        return this.x.cata(p, f);
    }

    /**
     * Functor map over the coproduct.
     *
     * <br>
     * <code>
     * instance (Functor f, Functor g) => Functor (Coproduct f g) where
     * fmap f = Coproduct . coproduct (Left . fmap f) (Right . fmap f)
     * </code>
     **/
    public <B> ConsoleFileCoproduct<B> map(Function<A, B> f) {
        return new ConsoleFileCoproduct<B>(this.cata(x -> Either.left(x.map(f)),
                                                     x -> Either.right(x.map(f))));
    }
}
