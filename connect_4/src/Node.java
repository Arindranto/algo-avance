import java.util.ArrayList;
import java.util.List;

public class Node {
     public static final int RED = 1;
     public static final int YELLOW = -1;
     public static final int VOID = 0;
     private static final long TURN_MASK = 0x4_00000_00000L;
     private static final long FULL = 0x3_FFFFF_FFFFFL;
     private static final short BOARD_ROW = 6;
     private static final short BOARD_COLUMN = 7;

     // Bitboard
     private long red = 0x0_00000_00000L; // 1 bit tour + 42 emplacements pour les rouges => 
     private long yellow = 0x0_00000_00000L; // 1 bit tour + 42 emplacements pour les jaunes =>

     public Node(int piece) {
          this.setTurn(piece);
     }

     public Node(Node n) {
          this.red = n.red;
          this.yellow = n.yellow;
     }

     public static Node parse(String moveSequence, int piece) {
          Node n = new Node(piece);
          char[] colArray = moveSequence.toUpperCase().toCharArray();
          for (int i = 0; !n.isFull() && i < colArray.length; i++) {
               Character c = colArray[i];
               try {
                    int col = Integer.parseInt(c.toString());
                    n.playAt(col);
               }
               catch (Exception e) {
                    System.out.println(e.getMessage());
               }
               catch (Error e) {
                    System.out.println(e.getMessage());
               }
          }
          return n;
     }

     public boolean isFull() {
          return ((red | yellow) ^ TURN_MASK) == FULL;
     }

     private static short getBoardSize() {
          return BOARD_ROW * BOARD_COLUMN;
     }

     public static boolean isRed(int piece) {
          return piece == RED;
     }

     public static boolean isYellow(int piece) {
          return piece == YELLOW;
     }

     private static boolean notRedNorYellow(int piece) {
          return !isRed(piece) && !isYellow(piece);
     }

     private static long getMask(int index) {
          return 1L << index;
     }
     private Integer getTopRow(int col) {
          Integer index = null;
          int row = 0;
          while (row < BOARD_ROW && !notRedNorYellow(at(row, col))) {
               row++;
          }
          if (row < BOARD_ROW)
               index = row;
          return index;
     }

     private Integer getTopIndex(int col) {
          return getIndex(getTopRow(col), col);
     }

     private boolean columnIsFull(int col) {
          return getTopIndex(col) == null;
     }

     private void putPiece(int piece, int index) {
          long mask = getMask(index);
          if (!notRedNorYellow(at(index))) {
               throw new Error("There's already a piece at index" + index);
          }
          if (isRed((piece))) {
               red |= mask;
          }
          else if (isYellow(piece)) {
               yellow |= mask;
          }
     }

     private Integer getIndex(Integer row, Integer col) {
          if (row == null || col == null || row < 0 || col < 0 || row >= BOARD_ROW || col >= BOARD_COLUMN) return null;
          return row * BOARD_COLUMN + col;
     }

     private Node playAt(int piece, int index) {
          // 1 <= index <= BOARD_COLUMN
          if (index == 0 || index > BOARD_COLUMN) {
               throw new Error("Column out of range");
          }
          index--;
          Integer i = getTopIndex(index);
          if (i != null) {
               putPiece(piece, i);
               return this;
          }
          else {
               throw new Error("The column is full");
          }
     }

     public Node playAt(int col) {
          playAt(getTurn(), col);
          invertTurn();
          return this;
     }

     public int at(Integer index) {
          if (index == null || index < 0 || index >= getBoardSize())
               return VOID;
          long mask = getMask(index);
          if ((red & mask) != 0) {
               return RED;
          }
          else if ((yellow & mask) != 0) {
               return YELLOW;
          }
          return VOID;
     }

     public int at(int row, int col) {
          return at(getIndex(row, col));
     }
     private void invertTurn() {
          red ^= TURN_MASK;
          yellow ^= TURN_MASK;
     }
     public int getTurn() {
          if ((red & TURN_MASK) != 0) {
               return RED;
          }
          else if ((yellow & TURN_MASK) != 0) {
               return YELLOW;
          }
          throw new Error("No turn for the given node");
     }

     public void setTurn(int piece) {
          switch (piece) {
               case RED:
                    red |= TURN_MASK;
                    yellow |= 0;
                    break;
               case YELLOW:
                    yellow |= TURN_MASK;
                    red |= 0;
                    break;
               default:
                    throw new Error("Unknown piece type");
          }
     }

     public void setTurn() {
          setTurn(RED);
     }

     /*-------------------------------------- */
     ArrayList<Node> getSucc() {
          ArrayList<Node> n = new ArrayList<Node>();
          for (int i = 0; i < BOARD_COLUMN; i++) {
               if (!columnIsFull(i)) {
                    n.add(createChild(i));
               }
          }
          return n;
     }

     public Node createChild(int col) {
          Node n = new Node(this); // Copy
          return n.playAt(col + 1);     // col + 1 car le joueur place sur les colonne de 1 à 7
     }

     int eval(int us) {
          int val = 0;
          // Par colonne
          for (int j = 0; j < BOARD_COLUMN; j++) {
               val = 0;
               for (int i = 0; i < BOARD_ROW; i++) {
                    int next = at(i, j);
                    if (next == 0) {
                         break;
                    }
                    if (val * next <= 0) {
                         val = 0;
                    }
                    val += next;
                    if (Math.abs(val) == 4) {
                         System.out.println("Colonne ");
                         return val * 100 * us;
                    }
               }
          }
 
          // Par ligne
          for (int i = 0; i < BOARD_ROW; i++) {
               val = 0;
               for (int j = 0; j < BOARD_COLUMN; j++) {
                    int next = at(i, j);
                    if (val * next <= 0) {
                         val = 0;
                    }
                    val += next;
                    if (Math.abs(val) == 4) {
                         System.out.println("Ligne");
                         return val * 100 * us;
                    }
               }
          }
          return 0;
     }
     
     int eval() {
          return eval(getTurn());
     }
     
     boolean isTerminal() {
          return eval(Node.RED) != 0 || isFull();
     }
     boolean isMax() {
          return getTurn() == RED;
     }

     private static char toChar(int piece) {
          switch (piece) {
               case RED:
                    return 'O';
               case YELLOW:
                    return 'X';
               default:
                    return ' ';
          }
     }

     @Override
     public String toString() {
          StringBuilder sb = new StringBuilder();
          StringBuilder sb2 = new StringBuilder();
          for (int i = 0; i < BOARD_ROW; i++) {
               sb2.delete(0, sb2.length());
               sb2.append("\n|");
               for (int j = 0; j < BOARD_COLUMN; j++) {
                    sb2.append(toChar(at(i, j)));
                    // sb2.append(i + " " + j + " " + (i * BOARD_COLUMN + j));
                    sb2.append('|');
               }
               sb.insert(0, sb2.toString());
          }
          return sb.toString().trim();
     }
}
