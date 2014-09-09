package me.elrod.pureio;

import java.util.function.Function;

/**
 * This is basically Identity except a two-product instead.
 */
public abstract class TupleTwo<A, B> {
    /**
     * Comonadic extraction in the first element.
     */
    public abstract A run1();

    /**
     * Comonadic extraction in the second element.
     */
    public abstract B run2();

    /**
     * Comonadic duplication. This does not force.
     *
     * This is isomorphic to the Functional Java implementation, *NOT* the
     * Control.Comonad implementation in Haskell, in that we work with the
     * tuple in the first argument, not the second.
     */
    public TupleTwo<TupleTwo<A, B>, B> duplicate() {
        return new TupleTwo<TupleTwo<A, B>, B>() {
            public TupleTwo<A, B> run1() {
                return new TupleTwo<A, B>() {
                    public A run1() {
                        return TupleTwo.this.run1();
                    }
                    public B run2() {
                        return TupleTwo.this.run2();
                    }
                };
            }

            public B run2() {
                return TupleTwo.this.run2();
            }
        };
    }

    /**
     * Comonadic extension.
     *
     * This is isomorphic to the Functional Java implementation, *NOT* the
     * Control.Comonad implementation in Haskell, in that we work with the
     * tuple in the first argument, not the second.
     */
    public <C> TupleTwo<C, B> extend(Function<TupleTwo<A, B>, C> f) {
        return this.duplicate().map1(f);
    }

    /**
     * Functor map in first argument.
     */
    public <C> TupleTwo<C, B> map1(Function<A, C> f) {
        return new TupleTwo<C, B>() {
            public C run1() {
                return f.apply(TupleTwo.this.run1());
            }

            public B run2() {
                return TupleTwo.this.run2();
            }
        };
    }

    /**
     * Functor map in second argument.
     */
    public <C> TupleTwo<A, C> map2(Function<B, C> f) {
        return new TupleTwo<A, C>() {
            public A run1() {
                return TupleTwo.this.run1();
            }

            public C run2() {
                return f.apply(TupleTwo.this.run2());
            }
        };
    }

    /**
     * Bifunctor map
     */
    public <C, D> TupleTwo<C, D> bimap(Function<A, C> f1, Function<B, D> f2) {
        return map1(f1).map2(f2);
    }
}
