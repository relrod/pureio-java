import me.elrod.pureio.*;

public class Hello {
    private static PureIO<Unit> program =
        TerminalLib.putStrLn("What is your name?")
        .flatMap(x -> TerminalLib.readLine()
                 .flatMap(name -> TerminalLib.putStrLn("Hi there, " + name + "! How are you?")
                          .flatMap(u1 -> TerminalLib.readLine()
                                   .flatMap(resp -> TerminalLib.putStrLn("I am also " + resp + "!")
                                            .flatMap(u2 -> TerminalLib.exit(0))))));

    public static void main(String[] args) {
        UnsafePerformIO.unsafePerformIO(program);
    }
}
