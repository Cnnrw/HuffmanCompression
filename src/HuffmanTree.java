import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

/**
 * Binary tree following the Huffman algorithm.
 *
 * @author      Connor Wilding
 * @version     0.1
 * @since       2017-12-06 15:25
 */
public class HuffmanTree {
    private Queue<HuffmanNode> leaves;
    private String[] huffmanPath;
    private HuffmanNode root;

    /**
     * Constructor for the HuffmanTree class. Takes an array of 
     * integers that represents the characters and their frequencies
     * of a given file to compress. The index of a non-null element
     * represents the character code and the element itself is the 
     * frequency of that character in the file.
     * 
     * @param   int[]   Array of character frequencies.
     * @throws  IOException
     */
    public HuffmanTree(int[] count) throws IOException {
        leaves = new PriorityQueue<>();

        // Iterate over array, adding HuffmanNodes to leaves
        // if their value in count != 0
        for (int i = 0; i < count.length; i++) {
            if (count[i] != 0) {
                leaves.add(new HuffmanNode(i, count[i]));
            }
        }

        /* Add special value at the end to signify the
           end of the file.    charCode: 256   freq: 1 */
        leaves.add(new HuffmanNode(256, 1));
        root = constructTree(leaves);

        huffmanPath = new String[257];
        huffmanMap(root, huffmanPath, "");

        // print tree
        root.printTree();
    }

    /**
     * Constructor for the HuffmanTree class. Takes a Scanner that
     * is focused on the code file for a huffmanTree and creates a 
     * tree from the information in the file.
     *
     * @param   Scanner     Scanner pointed at the code file of a huffmanTree
     */
    public HuffmanTree(Scanner scan) {
        String[] charCode = new String[257];    // Create string array
        leaves = new PriorityQueue<>();         // Initialize queue

        /* Read in the code file. The code file has a cadence in the
         * form of:     Character code  
         *              Character tree position
         * The character code will be used as the index for the 
         * tree position in the String array.
         */
        while (scan.hasNextLine()) {
            int n = Integer.parseInt(scan.nextLine());
            charCode[n] = scan.nextLine();
        }
        for (int i = 0; i < charCode.length; i++) {
            System.out.println(charCode[i] + " : " + i);
        }
    }

    // public void decode()

    /**
     * Constructs a huffman tree using the naturally ordered queue of 
     * leaves. constructTree takes the front two leaves of the queue
     * and makes them children of a node whose frequency is equal to
     * the sum of its childrens frequencies. When it is finished it
     * returns the last node in the queue which will be the tree's 
     * root.
     *
     * @param   Queue<HuffmanNode>  Queue containing ordered leaves.
     * @return  HuffmanNode         The root of the newly created tree.
     */
    private HuffmanNode constructTree(Queue<HuffmanNode> leaves) {
        while (leaves.size() > 1) {
            HuffmanNode left = leaves.remove();
            HuffmanNode right = leaves.remove();

            HuffmanNode parent = new HuffmanNode(left.freq + right.freq, 
                                                 left.freq + right.freq, 
                                                 left, 
                                                 right); 
            leaves.offer(parent);
        }
        return leaves.remove();
    }

    /**
     * Writes the current huffmanTree to the provided output stream,
     * in the format: <charCode>
     *                <map position>
     */
    public void write(PrintStream output) throws IOException {
        write(output, root);
    }

    private void write(PrintStream output, HuffmanNode root) {
        if (!root.isLeaf()) {
            write(output, root.left);
            write(output, root.right);
        } else {
            output.println(root.charCode);
            output.println(root.bitPosition);
        }
    }

    /**
     * Creates the Integer that represents a characters
     * position in the tree.
     *
     * @param   HuffmanNode node whose poition is being mapped.
     * @param   String      String that will be concatinated with a 0 if the 
     *                      call goes left in the tree and 1 if the call goes
     *                      to the right.
     * @param   String[]    String array to hold the integers in the position
     *                      of the characters ASCII code.
     */
    private void huffmanMap(HuffmanNode node, String[] huffPath, String s) {
        if (!node.isLeaf()) {
            huffmanMap(node.left, huffPath, s + '0');
            huffmanMap(node.right, huffPath, s + '1');
        } else {
            node.bitPosition = s;
            huffPath[node.charCode] = s;
        }
    }

    /**
     * Node object containing the characer code, frequency,
     * and any possible branches.
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        int charCode;
        int freq;
        String bitPosition;
        HuffmanNode left, right;

        /**
         * Constructs a HuffmanNode and assigns the values
         * for both the character code and frequency said
         * character was found in the file
         * 
         * @param           int     Character ASCII code
         * @param           int     Character frequency
         * @param   HuffmanNode     Left node.
         * @param   HuffmanNode     Right node.
         */
        HuffmanNode(int charCode, int freq, 
                    HuffmanNode left, HuffmanNode right) {
            this.charCode = charCode;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        HuffmanNode(int charCode, int freq) {
            this(charCode, freq, null, null);
        }

        /**
         * isLeaf determines whether or not a node is a leaf. A
         * leaf is defined as not having children.
         *
         * @return  boolean     whether or not the node has children.
         */
        boolean isLeaf() {
            return (this.left == null && this.right == null);
        }
        
        /**
         * Overrides the compareTo method as required by the Comparable
         * interface. Orders Huffman nodes according to their frequency,
         * the lower the frequency the higher in the queue they are 
         * placed.
         */
        @Override
        public int compareTo(HuffmanNode other) {
            if (!(other instanceof HuffmanNode)) {
                throw new IllegalArgumentException();
            }
            if (freq == other.freq) {
                return 0;
            } else if (freq < other.freq) {
                return -1;
            } else {
                return 1;
            }
        }

//----------------------------Tree Printing---------------------------------//

        void printTree() throws IOException {
            BufferedWriter out = new BufferedWriter(
                                new OutputStreamWriter(System.out));
            if (right != null) {
                right.printTree(out, true, "");
            }
            printNodeValue(out);
            if (left != null) {
                left.printTree(out, false, "");
            }
            out.flush();
            out.close();
        }

        void printNodeValue(BufferedWriter out) throws IOException {
            if (charCode == 256) {
                out.write("<End Of File>");
            } else {
                out.write(charCode + " : " + freq);
            }
            out.write('\n');
        }

        void printTree(BufferedWriter out, boolean isRight, 
                               String indent) throws IOException {
            if (right != null) {
                right.printTree(out, true, indent 
                             + (isRight ? "        " : " |      "));
            }

            out.write(indent);

            if (isRight) {
                out.write(" /");
            } else {
                out.write(" \\");
            }

            out.write("----- ");
            printNodeValue(out);

            if (left != null) {
                left.printTree(out, false, indent 
                            + (isRight ? " |      " : "        "));
            }
        }
    }
}
