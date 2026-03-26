package ATLAS;

import java.util.*;

public class NodeQueue {
    public NodeQueue(String input) {

        printQueue(ProcessString(input));

    }

    private Queue<String> ProcessString(String s) {
        Queue<String> q = new LinkedList<>();
        q.addAll(Arrays.asList(s.split(" ")));

        return q;
    }

    // Temp function to print Queue. Redo this by popping items from queue and store them in array
    public void printQueue(Queue<String> q) {
        int depth = 0;
        for (String x: q) {
            if (x.toCharArray()[0] == '(') {
                depth++;
                System.out.println(" ");
                for (int i=0; i<depth ; i++) {
                    System.out.print("  ");
                }
            }
            System.out.print(x + " ");
        }
    }
}
