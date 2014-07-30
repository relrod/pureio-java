package me.elrod.pureio;

import java.util.function.Function;

/** This provides a nicer public API over the rest of the code. */
public class TerminalLib {
    public static PureIO<Unit> putStrLn(String s) {
        return new TerminalOperation.PutStrLn<Unit>(s, Unit.VALUE).liftF();
    }

    public static PureIOT<Unit> putStrLnT(String s) {
        return new TerminalOperation.PutStrLn<Unit>(s, Unit.VALUE).liftT();
    }

    public static PureIO<String> readLine() {
        return new TerminalOperation.ReadLine<String>(Function.identity()).liftF();
    }

    public static PureIOT<String> readLineT() {
        return new TerminalOperation.ReadLine<String>(Function.identity()).liftT();
    }

    public static PureIO<Unit> exit(Integer exitCode) {
        return new TerminalOperation.Exit<Unit>(exitCode, Unit.VALUE).liftF();
    }

    public static PureIOT<Unit> exitT(Integer exitCode) {
        return new TerminalOperation.Exit<Unit>(exitCode, Unit.VALUE).liftT();
    }
}
