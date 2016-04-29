import me.elrod.pureio.*;

public class ForeverIO {
    private static PureConsoleIO<Unit> program =
        PureConsoleIO.forever(
            TerminalLib.putStrLn("This will print for a bit then bomb out."));

  public static void main(String[] args) {
      UnsafePerformIO.unsafePerformIO(program);
  }
}
