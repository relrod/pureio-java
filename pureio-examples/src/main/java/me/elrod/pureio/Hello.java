import me.elrod.pureio.*;

public class Hello {
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
