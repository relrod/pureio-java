package me.elrod.pureio;

import java.util.function.Function;

/** This provides a nicer public API over the rest of the code. */
public class TerminalLib {
    public static PureConsoleIO<Unit> putStrLn(String s) {
        return new TerminalOperation.PutStrLn<Unit>(s, Unit.VALUE).liftF();
    }

    public static PureConsoleIOT<Unit> putStrLnT(String s) {
        return new TerminalOperation.PutStrLn<Unit>(s, Unit.VALUE).liftT();
    }

    public static PureConsoleIO<String> readLine() {
        return new TerminalOperation.ReadLine<String>(Function.identity()).liftF();
    }

    public static PureConsoleIOT<String> readLineT() {
        return new TerminalOperation.ReadLine<String>(Function.identity()).liftT();
    }

    public static PureConsoleIO<Unit> exit(Integer exitCode) {
        return new TerminalOperation.Exit<Unit>(exitCode, Unit.VALUE).liftF();
    }

    public static PureConsoleIOT<Unit> exitT(Integer exitCode) {
        return new TerminalOperation.Exit<Unit>(exitCode, Unit.VALUE).liftT();
    }
}
