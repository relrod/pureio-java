import me.elrod.pureio.*;

public class ForeverIO {
    private static PureConsoleIO<Unit> program =
        PureConsoleIO.forever(
            TerminalLib.putStrLn("This will bomb out with SOE."));

  public static void main(String[] args) {
      UnsafePerformIO.unsafePerformIO(program);
  }
}
