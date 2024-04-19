import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Connect4 {
     private int bot = Node.RED;     // 1 red, -1 yellow
     private Node state;


     Connect4(int bot) {
          this.bot = bot;
          this.state = new Node(Node.RED);
     }

     static int minimax(Node n, int depth) {
          if (n.isTerminal() || depth == 0)
               return n.eval();
          if (n.isMax()) {
               int val = Integer.MIN_VALUE;
               for (Node childNode: n.getSucc()) {
                    val = Math.max(val, minimax(childNode, depth - 1));
               }  
               return val;
          }
          else {
               int val = Integer.MAX_VALUE;
               for (Node childNode: n.getSucc()) {
                    val = Math.min(val, minimax(childNode, depth - 1));
               }
               return val;
          }
     }
     int minimax(Node n, int depth, int alpha, int beta) {
          if (n.isTerminal() || depth == 0)
               return n.eval(bot);
          if (n.isMax()) {
               int val = Integer.MIN_VALUE;
               for (Node childNode: n.getSucc()) {
                    val = Math.max(val, minimax(childNode, depth - 1, alpha, beta));
                    alpha = Math.max(alpha, val);
                    if (beta <= alpha) {
                         return val;    // Coupe alpha
                    }
               }
               return val;
          }
          else {
               int val = Integer.MAX_VALUE;
               for (Node childNode: n.getSucc()) {
                    val = Math.min(val, minimax(childNode, depth - 1, alpha, beta));
                    beta = Math.min(beta, val);
                    if (beta <= alpha) {
                         return val;    // Coupe beta
                    }
               }
               return val;
          }
     }
     private boolean botTurn() {
          return state.getTurn() == bot;
     }

     void play() {
          Scanner scanner = new Scanner(System.in);
          while (!state.isTerminal()) {     
               if (!botTurn()) {
                    System.out.print("Write the column number: ");
                    state.playAt(scanner.nextInt());
               }
               else {
                    Node nextMove = null;
                    int max = Integer.MIN_VALUE;
                    ArrayList<Node> successors = state.getSucc();
                    Collections.shuffle(successors);   // Shuffle the values in the list so the bot reacts more randomly
                    for (Node succ: successors) {
                         int val = minimax(succ, 5);
                         if (val > max) {
                              nextMove = succ;
                              max = val;
                         }
                    }
                    state = nextMove;
               }
               System.out.println(state);
               System.out.println("-----------------------");
          }
     }
}
