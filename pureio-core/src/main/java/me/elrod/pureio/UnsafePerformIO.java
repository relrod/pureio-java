package me.elrod.pureio;

import java.io.*;

/**
 * A compatibility interpreter for our free IO monads to make them do things in
 * a way that a typical Java environment might expect.
 *
 * You could implement your own to do cooler, better, things.
 */
public class UnsafePerformIO {
  final static BufferedReader in =
    new BufferedReader(new InputStreamReader(System.in));

  public static <A> A unsafePerformIO(PureIO<A> t) {
    return t.cata(
      a -> a,
      a -> a.cata(
        (s, tt) -> {
          System.out.println(s);
          return unsafePerformIO(tt);
        },
        f       -> {
          try {
            String s = in.readLine();
            return unsafePerformIO(f.apply(s));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        },
        (ec, tt) -> {
          System.exit(ec);
          return unsafePerformIO(tt);
        }
    ));
  }
}
