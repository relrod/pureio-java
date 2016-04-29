import me.elrod.pureio.*;

public class ForeverIOT {
    private static PureConsoleIOT<Unit> program =
        PureConsoleIOT.forever(
            TerminalLib.putStrLnT("If trampolining works, this will print forever!"));

  public static void main(String[] args) {
      program.run(x -> UnsafePerformIO.unsafePerformIOT(x));
  }
}
