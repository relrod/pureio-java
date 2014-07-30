import me.elrod.pureio.*;

public class ForeverIO {
    private static PureIOT<Unit> program =
        PureIOT.forever(
            TerminalLib.putStrLnT("If trampolining works, this will print forever!"));

  public static void main(String[] args) {
      program.run(x -> UnsafePerformIO.unsafePerformIOT(x));
  }
}
