import java.io.IOException;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class MakeCodeTest {
    Map<Character, Integer> charCount;
    int[] charCode;

    @Test
    public void testFileProcessing() throws IOException {
        charCount = MakeCode.getCharCount("src/short.text");
        charCode = MakeCode.getCharCode("src/short.text");

        for (int i = 0; i < charCode.length; i++) {
            if (charCode[i] != 0) {
                assertTrue(charCount.containsKey((char) i));
                assertTrue(charCount.containsValue(charCode[i]));
            }
        }
    }

}
