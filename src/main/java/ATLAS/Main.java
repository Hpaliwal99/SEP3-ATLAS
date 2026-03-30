package ATLAS;


import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws ParseException {

        String input1 = "(work in *scientist (some lab (that (conduct experiment))))";
        String input2 = "(work in *priest (some church (that (conduct sermon))))";
        Parse parse1 = new Parse();
        Parse parse2 = new Parse();
        Node root = parse1.parse(input1);
        Node root2 = parse2.parse(input2);

        System.out.println();
        System.out.println("Printing Parse:");
        parse1.printIndent(root);
        System.out.println();
        System.out.println("Printing flattened Parse:");
        System.out.println(parse1.toFlat(root));
        System.out.println();
        parse1.printQueues();
        System.out.println();

        parse1.replaceKeywords(root);
        System.out.println(parse1.toFlat(root));
        System.out.println();
        parse1.printIndent(root);

        System.out.println(Utility.compare(parse1, parse2));
        LinkedList<String[]> u = Utility.getKeywordMapping(parse1, parse2);
        System.out.println(Arrays.deepToString(u.toArray())); //make custom toString
        System.out.println(Utility.StringCompare(input1, input2));
        System.out.println(Arrays.deepToString(Utility.getStringKeywordMapping(input1,input2).toArray()));

    }
}