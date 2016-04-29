import me.elrod.pureio.*;

import java.util.function.Function;

/**
 * If this demo works correctly, it will print the following:
 *
 * test
 * array
 * hello
 * world
 */
public class LinkedListFlatMapDemo {
    public static void main(String[] args) {
        Function<String, String> demofn = x -> { System.out.println(x); return x; };
        LinkedList<LinkedList<String>> ll =
            new LinkedList.Cons<LinkedList<String>>(
                new LinkedList.Cons<String>(
                    "test",
                    new LinkedList.Cons<String>(
                        "array",
                        new LinkedList.Nil<String>())),
                new LinkedList.Cons<LinkedList<String>>(
                    new LinkedList.Cons<String>(
                        "hello",
                        new LinkedList.Cons<String>(
                            "world",
                            new LinkedList.Nil<String>())),
                    new LinkedList.Nil<LinkedList<String>>()));

        LinkedList<String> llc = ll .$ (a -> a);
        llc .ρ (demofn);
    }
}
