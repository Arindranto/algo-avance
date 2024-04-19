public class App {
    public static void main(String[] args) throws Exception {
        Node n = Node.parse("7765655444162717", Node.RED);
        Connect4 c4 = new Connect4(Node.RED);
        /*System.out.println(n);
        System.out.println(c4.minimax(n, 5));*/
        c4.play();
        /* System.out.println(n);
        System.out.println(n.eval() + " par rapport a " + n.getTurn()); */
    }
}
