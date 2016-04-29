import me.elrod.pureio.*;

public class HelloTrampoline {
    private static PureConsoleIOT<Unit> program =
        TerminalLib.putStrLnT("What is your name?")
        .$ (x -> TerminalLib.readLineT()
        .$ (name -> TerminalLib.putStrLnT("Hi there, " + name + "! How are you?")
        .$ (u1 -> TerminalLib.readLineT()
        .$ (resp -> TerminalLib.putStrLnT("I am also " + resp + "!")
        .$ (u2 -> TerminalLib.exitT(0))))));

    public static void main(String[] args) {
        program.run(x -> UnsafePerformIO.unsafePerformIOT(x));
    }
}
