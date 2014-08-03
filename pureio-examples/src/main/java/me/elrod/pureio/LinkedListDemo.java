import me.elrod.pureio.*;

import java.util.function.Function;

/**
 * If this demo works correctly, it will print the following:
 *
 * hello
 * world
 * HELLO
 * WORLD
 *
 * It does this by mapping (using mapping to both print as a side effect and to
 * convert the text to uppercase, in separate steps).
 */
public class LinkedListDemo {
    public static void main(String[] args) {
        Function<String, String> demofn = x -> { System.out.println(x); return x; };
        LinkedList<String> ll = new LinkedList.Cons<String>("hello",
                                                            new LinkedList.Cons<String>("world",
                                                                                        new LinkedList.Nil<String>()));
        ll.map(demofn);
        ll.map(x -> x.toUpperCase()).map(demofn);
    }
}
