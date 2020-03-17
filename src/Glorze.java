import java.util.ArrayList;
import java.util.LinkedList;

public class Glorze {

    public static void main(String[] args) {
        System.out.println("glorze.com");
        LinkedList<Integer> linkedList = new LinkedList<Integer>();
        linkedList.add(1);
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(1);
        if(arrayList.equals(linkedList)) {
            System.out.println("equals is true");
        } else {
            System.out.println("equals is false");
        }
    }

}
