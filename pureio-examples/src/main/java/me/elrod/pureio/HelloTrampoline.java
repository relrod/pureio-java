import me.elrod.pureio.*;

public class HelloTrampoline {
    private static PureConsoleIOT<Unit> program =
        TerminalLib.putStrLnT("What is your name?")
        .flatMap(x -> TerminalLib.readLineT()
                 .flatMap(name -> TerminalLib.putStrLnT("Hi there, " + name + "! How are you?")
                          .flatMap(u1 -> TerminalLib.readLineT()
                                   .flatMap(resp -> TerminalLib.putStrLnT("I am also " + resp + "!")
                                            .flatMap(u2 -> TerminalLib.exitT(0))))));

    public static void main(String[] args) {
        program.run(x -> UnsafePerformIO.unsafePerformIOT(x));
    }
}
