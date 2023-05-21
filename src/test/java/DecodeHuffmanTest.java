import org.junit.jupiter.api.Test;

import java.util.HexFormat;

public class DecodeHuffmanTest {

    private final String hex = "81 00 00 00 00 00 00 00 0B 00 00 00 06 00 00 00 2D 00 00 00 09 00 00 00 30 00 00 00 03 00 00 00 31 00 00 00 03 00 00 00 32 00 00 00 02 00 00 00 33 00 00 00 02 00 00 00 34 00 00 00 06 00 00 00 35 00 00 00 03 00 00 00 37 00 00 00 04 00 00 00 38 00 00 00 01 00 00 00 39 00 00 00 02 00 00 00 7C 00 00 00 85 00 00 00 11 00 00 00 29 00 00 00 D3 0C 78 90 FB 1D 0E 6E 4B 4C 35 DF 17 75 BD AA 90";

    @Test
    public void kleidukosDecode() throws Exception {
        var bytes = HexFormat.of().parseHex(hex.replace(" ", ""));

        System.out.println(me.kleidukos.HuffmanDecoder.decode(bytes));
    }

    @Test
    public void lukeDecode(){
        var bytes = HexFormat.of().parseHex(hex.replace(" ", ""));

        System.out.println(HuffmanDecoder.decode(bytes));
    }

}
