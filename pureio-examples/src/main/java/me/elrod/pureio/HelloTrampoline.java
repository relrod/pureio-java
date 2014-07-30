import me.elrod.pureio.*;

public class HelloTrampoline {
    private static PureIOT<Unit> program =
      TerminalLib.putStrLnT("What is your name?")
      .flatMap(x -> TerminalLib.readLineT())
      .flatMap(x -> TerminalLib.putStrLnT("Hi there, " + x + "! How are you?"))
      .flatMap(x -> TerminalLib.readLineT())
      .flatMap(x -> TerminalLib.putStrLnT("I am also " + x + "!"))
      .flatMap(x -> TerminalLib.exitT(0));

  public static void main(String[] args) {
      program.run();
  }
}
