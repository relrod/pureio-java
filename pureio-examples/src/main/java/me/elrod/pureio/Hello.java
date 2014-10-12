import me.elrod.pureio.*;

public class Hello {
    private static PureConsoleIO<Unit> program =
        TerminalLib.putStrLn("What is your name?")
        .$ (x -> TerminalLib.readLine()
        .$ (name -> TerminalLib.putStrLn("Hi there, " + name + "! How are you?")
        .$ (u1 -> TerminalLib.readLine()
        .$ (resp -> TerminalLib.putStrLn("I am also " + resp + "!")
        .$ (u2 -> TerminalLib.exit(0))))));

    public static void main(String[] args) {
        UnsafePerformIO.unsafePerformIO(program);
    }
}
