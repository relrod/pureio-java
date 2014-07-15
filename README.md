Experiments with purely functional IO in Java 8.

This is standalone and doesn't depend on FJ right now, though it probably
should.

This is experimental, don't use it.

That being said:
  License: **BSD-2**.

```java
public class PureIOTest {
    private static PureIO<Unit> program =
      TerminalLib.putStrLn("What is your name?")
      .flatMap(x -> TerminalLib.readLine())
      .flatMap(x -> TerminalLib.putStrLn("Hi there, " + x + "! How are you?"))
      .flatMap(x -> TerminalLib.readLine())
      .flatMap(x -> TerminalLib.putStrLn("I am also " + x + "!"))
      .flatMap(x -> TerminalLib.exit(0));

  public static void main(String[] args) {
      UnsafePerformIO.unsafePerformIO(program);
  }
}
```

See more examples in `pureio-examples/`. You might even be able to run them by
doing `sbt pureio-examples/run`, if luck is going your way.

You need sbt 0.13+ and Java 8.
