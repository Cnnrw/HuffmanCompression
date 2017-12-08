/**
 * Encodes standard text files that use ASCII or binary character codes. 
 * MakeCode prompts the user for a file to examine and computes the frequency
 * of each character in the file.
 *
 * @author  Connor Wilding
 * @versin  0.1
 * @since   2017-12-05 19:18
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MakeCode {

    public static void main(String[] args) throws IOException {
        HuffmanTree tree = new HuffmanTree(getCharCode());
    }

    /**
     * getCharCode returns an int[] containing the character count
     * of the text file to be encoded. The int[] will contain exactly
     * 256 values. By calling getFileName and passing the file name 
     * to getCharCount which reads the file and returns a 
     * Map<Character, Integer>, we can then iterate over the map and 
     * build our int array. The index of a value in the charCode array 
     * represents a character's ASCII value, while the actual value 
     * itself is the number of times that character appears in the 
     * text file.
     *
     * @param   String          Name of file to process.
     * @return  int[]           Array containing information about the 
     *                          text file.
     * @throws  IOException
     */
    public static int[] getCharCode(String fileName) throws IOException {
        int[] charCode = new int[256]; 

        Map<Character, Integer> data = getCharCount(fileName);
        data.forEach((charInt, count) -> charCode[(int) charInt] = count);
        return charCode;
    }

    public static int[] getCharCode() throws IOException {
        return getCharCode(getFileName());
    }

    /**
     * getCharCount reads the entire file into a Stream<String> consisting
     * of each line. It then uses flatMap to turn that into a Stream<Character>
     * by "flattening" a Stream<Stream<Character>> which we get by creating
     * an IntStream of [0, line,length()] and calling line.charAt for each
     * element of the InputStream. The char is then autoboxed to a character.
     * It then uses the filter method of Stream to strip out things that 
     * aren't letters. Finally it counts the occurence of each letter in the
     * file and returns a mutable collection.
     *
     * @param   String                      The file name to inspect.
     * @return  Map<Character, Integer>     A map containing character and 
     *                                      occurence count.
     * @throws  IOException
     */
    public static Map<Character, Integer> getCharCount(String fileName) 
                                                    throws IOException {
        return Files.lines(Paths.get(fileName))
            .flatMap(line -> 
                    IntStream.range(0, line.length()).mapToObj(line::charAt))
            // .filter(Character::isLetter)
            // .map(Character::toLowerCase)
            .collect(Collectors.toMap(s -> s, s -> 1, Integer::sum));
    }

    /**
     * getFileName creates a Scanner and prompts the user to enter the
     * name of a file to be processed.
     *
     * @return  String      Name of file to be processed
     */
    private static String getFileName() { 
        try (Scanner scan = new Scanner(System.in)) {
            System.out.print("Enter file name: ");
            String fileName = "src/" + scan.nextLine() + ".text";
            return fileName;
        }
    }
}

