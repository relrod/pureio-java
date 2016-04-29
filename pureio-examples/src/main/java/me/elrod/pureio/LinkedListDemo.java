import me.elrod.pureio.*;

import java.util.function.Function;

/**
 * If this demo works correctly, it will print the following:
 *
 * test
 * array
 * hello
 * world
 * HELLO
 * WORLD
 * test
 * array
 * append1
 * append2
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

        String[] foo = {"test", "array"};
        LinkedList<String> fooLL = LinkedList.fromArray(foo);
        fooLL .ρ (demofn);

        String[] bar = {"append1", "append2"};
        LinkedList<String> barLL = LinkedList.fromArray(bar);

        fooLL .ρ (demofn);
        fooLL .ρ (x -> x.toUpperCase()) .ρ (demofn);

        LinkedList.append(fooLL, barLL) .ρ (demofn);
    }
}
