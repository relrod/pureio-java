package me.elrod.pureio;

import java.io.*;
import java.util.function.Function;

public abstract class PureIO<A> {
  private PureIO() {
  }

  // Functor
  public <B> PureIO<B> map(Function<A, B> f) {
    return fold(
      a -> PureIO.pure(f.apply(a)),
      a -> PureIO.free(a.map(k -> k.map(f))));
  }

  // Free monad
  public <B> PureIO<B> flatMap(Function<A, PureIO<B>> f) {
    return fold(f, a -> PureIO.free(a.map(k -> k.flatMap(f))));
  }

  public abstract <B> B fold(
    Function<A, B> pure,
    Function<TerminalOperation<PureIO<A>>, B> free);

  final static class Pure<A> extends PureIO<A> {
    private A a;

    public Pure(A a) {
      this.a = a;
    }

    public <B> B fold(
      Function<A, B> pure,
      Function<TerminalOperation<PureIO<A>>, B> free) {
        return pure.apply(a);
    }
  }


  public static <A> PureIO<A> pure(A a) {
    return new Pure<A>(a);
  }

  final static class Free<A> extends PureIO<A> {
    private TerminalOperation<PureIO<A>> a;

    public Free(TerminalOperation<PureIO<A>> a) {
      this.a = a;
    }

    public <B> B fold(
      Function<A, B> pure,
      Function<TerminalOperation<PureIO<A>>, B> free) {
        return free.apply(a);
    }
  }

  public static <A> PureIO<A> free(TerminalOperation<PureIO<A>> a) {
    return new Free<A>(a);
  }

  public static <A,B> PureIO<B> forever(PureIO<A> x) {
      return x.flatMap(unused -> forever(x));
  }
}
