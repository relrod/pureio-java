import me.elrod.pureio.*;

import java.math.BigInteger;

/**
 * A simple example to show that our Trampoline<A> does what it should.
 *
 * The existence of this program is not meant to be a proof, but a
 * demonstration.
 *
 * This could/should be turned into a test.
 */
public class TrampolineWorks {

    /**
     * This is a typical factorial function. It uses BigInteger because we are
     * interested in the stack complexity, not performance.
     *
     * This will overflow the stack if called with a large number.
     * e.g. fac(new BigInteger("10000000"), BigInteger.ONE);
     */
    public static BigInteger fac(BigInteger x, BigInteger acc) {
        if (x.equals(BigInteger.ONE)) {
            return acc;
        } else {
            return fac(x.subtract(BigInteger.ONE), acc.multiply(x));
        }
    }

    /**
     * This is a trampolined version of the above factorial function.
     *
     * It demonstrates that our trampoline does as we expect. While calling it
     * with a huge number takes a considerable amount of time to yield a result,
     * it never overflows the stack.
     */
    public static Trampoline<BigInteger> facT(BigInteger x, BigInteger acc) {
        if (x.equals(BigInteger.ONE)) {
            return Trampoline.pure(acc);
        } else {
            return Trampoline.suspend(new Identity<Trampoline<BigInteger>>() {
                    public Trampoline<BigInteger> run() {
                        return facT(x.subtract(BigInteger.ONE), acc.multiply(x));
                    }
                });
        }
    }

    public static void main(String[] a) {
        System.out.println(facT(new BigInteger("50000"), BigInteger.ONE).run());
    }
}
