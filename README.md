# pureio-java

[![Build Status](https://travis-ci.org/CodeBlock/pureio-java.svg?branch=master)](https://travis-ci.org/CodeBlock/pureio-java)

Experiments with purely functional IO in Java 8.

This is standalone and doesn't depend on FJ right now, though it probably
should. As such, it re-implements several classes that are commonplace in FJ.

This is experimental, you probably don't want to use it right now.

That being said:

License: **BSD-2**.

## Trampolined

```java
public class PureIOTest {
    private static PureIOT<Unit> program =
      TerminalLib.putStrLnT("What is your name?")
      .flatMap(x -> TerminalLib.readLineT())
      .flatMap(x -> TerminalLib.putStrLnT("Hi there, " + x + "! How are you?"))
      .flatMap(x -> TerminalLib.readLineT())
      .flatMap(x -> TerminalLib.putStrLnT("I am also " + x + "!"))
      .flatMap(x -> TerminalLib.exitT(0));

  public static void main(String[] args) {
    program.run(x -> UnsafePerformIO.unsafePerformIOT(x));
  }
}
```

## Not trampolined

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

## Running it

You need [sbt](https://raw.githubusercontent.com/paulp/sbt-extras/master/sbt)
0.13+ and Java 8. Just download the script, `chmod +x sbt` and place it
somewhere in your `$PATH`.

Once you have sbt installed, you should be able to do this:

```
PATH=/usr/lib/jvm/java-1.8.0-openjdk.x86_64/bin/:$PATH sbt -java-home /usr/lib/jvm/java-1.8.0-openjdk.x86_64/ pureio-examples/run
```

For convenience, you can add this to your bash_profile for future use:

```
alias sbt8='PATH=/usr/lib/jvm/java-1.8.0-openjdk.x86_64/bin/:$PATH sbt -java-home /usr/lib/jvm/java-1.8.0-openjdk.x86_64/'
```

and then use `sbt8 pureio-examples/run` to run the examples.


## JavaDoc

[View javadoc](https://codeblock.github.io/pureio-java/).
