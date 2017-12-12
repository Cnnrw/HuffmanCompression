import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Binary tree following the Huffman algorithm.
 *
 * @author      Connor Wilding
 * @version     0.1
 * @since       2017-12-06 15:25
 */
public class HuffmanTree2 {
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
    public HuffmanTree2(int[] count) throws IOException {
        Queue<HuffmanNode> leaves = new PriorityQueue<>();

        // Iterate over array, adding HuffmanNodes to leaves
        // if their value in count != 0
        for (int i = 0; i < count.length; i++) {
            if (count[i] != 0) {
                leaves.add(new HuffmanNode(i, count[i]));
            }
        }

        // Add special value at the end to signify the
        // end of the file.    charCode: 256   freq: 1 
        leaves.add(new HuffmanNode(256, 1));
        root = constructTree(leaves);
        root.printTree();
    }

    /**
     * Constructs a HuffmanTree from the given input stream. Assumes 
     * that the standard bit representation has been used for the tree.
     * 
     * @param   BitInputStream  input stream.
     */
    public HuffmanTree2(BitInputStream input) throws IOException {
        root = reconstructTree(input);
        root.printTree();
    }

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
     * Reconstructs the tree given the preorder map. Reads a bit from
     * the input stream. If the bit returned is 0, construct a branch node 
     * and recursively build their paths. When the bit returned is 1 create 
     * a leaf node containing the character code and exit the stack.
     *
     * @param   BitInputStream  stream containing a huffman tree map.
     * @return  HuffmanNode     Node being constructed
     */
    private HuffmanNode reconstructTree(BitInputStream input) {
        int bit = input.readBit();

        if (bit == 1) {
            // We've hit a leaf, return a childless node
            return new HuffmanNode(read9(input), 0);
        } else {
            // We've hit a branch, recursively build
            // left and right path. Stack exits when
            // a leaf node is returned.
            HuffmanNode left = reconstructTree(input);
            HuffmanNode right = reconstructTree(input);
            return new HuffmanNode(0,0, left, right);
        }
    }

    /**
     * Writes the current huffmanTree to the provided output stream.
     *  
     * @param   BitOutputStream     stream to write the huffman code to.
     * @throws  IOException
     */
    public void writeHeader(BitOutputStream output) throws IOException {
        writeHeader(output, root);
    }

    /**
     * Writes to the bit stream a representation of the tree that can
     * be used to reconstruct it later. Uses preorder traversal. Nodes 
     * are assigned a 0 if they have children (branch nodes) or 1 if
     * they represent a character (leaf nodes).
     *
     * @param   BitOutputStream     output file being written.
     * @param   HuffmanNode         current node being examined
     */
    private void writeHeader(BitOutputStream output, HuffmanNode root) {
        if (!root.isLeaf()) {
            output.writeBit(0);
            writeHeader(output, root.left);
            writeHeader(output, root.right);
        } else {
            output.writeBit(1);
            write9(output, root.charCode);
        }
    }

    /**
     * Writes a 9-bit representation of n to the given output stream.
     *
     * @param   BitOutputStream  Stream to write the 9-bit integer to
     * @param               int  number to be encoded
     */
    private void write9(BitOutputStream output, int n) {
        for (int i = 0; i < 9; i++) {
            output.writeBit(n % 2);
            n /= 2;
        }
    }

    /**
     * Reads 9 bits to reconstruct the original integer from the 
     * input stream.
     *
     * @param   BitInputStream  input stream being read from
     */
    private int read9(BitInputStream input) {
        int multiplier = 1;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += multiplier * input.readBit();
            multiplier *= 2;
        }
        return sum;
    }

    /**
     * Decodes a compressed text file by cascading through the 
     * reconstructed tree and writing the character found in each
     * leaf to a file.
     *
     * @param   BitInputStream      Reads in the compressed file bit
     *                              by bit.
     * @param   PrintStream         Writes the uncompressed text to 
     *                              a file.
     * @param   int                 End of File character code.
     */
    public void decode(BitInputStream input, PrintStream output, int eof) {
        HuffmanNode current = root;
        int path = input.readBit();
        while (true) {
            if (path == 1) {
                current = current.right;
                path = input.readBit();
            } else if (path == 0) {
                current = current.left;
                path = input.readBit();
            } 

            if (current.isLeaf()) {
                if (current.charCode == 256) {
                    break;
                }
                output.write(current.charCode);
                current = root;
                continue;
            } 
        }
    }

    /**
     * Assigns codes for each character in the tree. The ASCII
     * character code will represent the index of the element in
     * the array containing the characters path code in the tree.
     *
     * @param   String[]    array of size 257 representing character
     *                      codes.
     */
    public void assign(String[] codes) {
        assign(root, "", codes);
    }

    /**
     * Creates the Integer that represents a characters
     * position in the tree.
     *
     * @param   HuffmanNode node whose poition is being mapped.
     * @param   String      String that will be concatinated with a 0 if the 
     *                      call goes left in the tree and 1 if the call goes
     *                      to the right.
     */
    private void assign(HuffmanNode node, String s, String[] codes) {
        if (!node.isLeaf()) {
            assign(node.left, s + '0', codes);
            assign(node.right, s + '1', codes);
        } else {
            codes[node.charCode] = s;
        }
    }

    /**
     * Node object containing the characer code, frequency,
     * and any possible branches.
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        int charCode;
        int freq;
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
                out.write(charCode);
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
