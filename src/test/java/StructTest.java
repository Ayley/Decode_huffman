import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;

public class StructTest {

    private Struct struct;
    private Decoder decoder;

    private String hex = "81 00 00 00 00 00 00 00 0B 00 00 00 06 00 00 00 2D 00 00 00 09 00 00 00 30 00 00 00 03 00 00 00 31 00 00 00 03 00 00 00 32 00 00 00 02 00 00 00 33 00 00 00 02 00 00 00 34 00 00 00 06 00 00 00 35 00 00 00 03 00 00 00 37 00 00 00 04 00 00 00 38 00 00 00 01 00 00 00 39 00 00 00 02 00 00 00 7C 00 00 00 85 00 00 00 11 00 00 00 29 00 00 00 D3 0C 78 90 FB 1D 0E 6E 4B 4C 35 DF 17 75 BD AA 90";

    @Before
    public void before(){
        var bytes = HexFormat.of().parseHex(hex.replace(" ", ""));

        decoder = new Decoder(bytes);
    }

    @Test
    public void testFreqs() throws Exception {
        for (var s: decoder.getFrequencies()) {
            System.out.println(s.character() + " : " + s.frequency());
        }
    }

    @Test
    public void testTree() throws Exception {
        System.out.println(decoder.unpackFile());
    }
}
