package me.elrod.pureio;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Here we demonstrate how you might use the concept of a coproduct to
 * combine grammars of various types of I/O.
 *
 * In this particular case, we build up a coproduct over {@link PureIO}
 * and {@link PureFileIO}.
 *
 * Using coproducts causes the trampolining technique to no longer work (and
 * it would be nice to find a solution to this in the future), so we don't make
 * use of {@link PureIOT} here.
 *
 * This structure makes use of {@link Either} and is very similar to
 * <code>Either<PureIO<A>, PureFileIO<A>></code> except that we write some
 * useful instances over it. That is,
 * <code>A =&gt; F[A] \/ G[A]</code> is a functor, given functors F and G.
 * We can then construct a free monad over this to combine both grammars.
 *
 * In reality, coproducts, of course, generalize to any two functors (or
 * foldables or traversables, or comonads). But in Java, we are only able to
 * write one very specific instance of this construct.
 */
public class PureIOFileIOCoProduct<A> {
    private Either<PureIO<A>, PureFileIO<A>> x;

    public PureIOFileIOCoProduct(Either<PureIO<A>, PureFileIO<A>> either) {
        this.x = either;
    }

    /**
     * Lift a PureIO.
     *
     * <br>
     * <code>
     * left :: f a -> Coproduct f g a    <br>
     * left = Coproduct . Left
     * </code>
     */
    public static <A> PureIOFileIOCoProduct<A> left(PureIO<A> p) {
        return new PureIOFileIOCoProduct<A>(Either.left(p));
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
    public static <A> PureIOFileIOCoProduct<A> right(PureFileIO<A> f) {
        return new PureIOFileIOCoProduct<A>(Either.right(f));
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
    public <X> X cata(Function<PureIO<A>, X> p, Function<PureFileIO<A>, X> f) {
        return this.x.cata(p, f);
    }
}
