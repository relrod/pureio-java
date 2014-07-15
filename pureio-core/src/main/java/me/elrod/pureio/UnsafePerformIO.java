package me.elrod.pureio;

import java.io.*;

public class UnsafePerformIO {
  final static BufferedReader in =
    new BufferedReader(new InputStreamReader(System.in));

  public static <A> A unsafePerformIO(PureIO<A> t) {
    return t.fold(
      a -> a,
      a -> a.fold(
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
