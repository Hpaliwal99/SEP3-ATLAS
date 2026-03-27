package ATLAS;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        String input = "(work in scientist (some lab (that (conduct experiment))))";
        Parse parse1 = new Parse();
        Node root = parse1.parse(input);

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
    }
}