import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Decoder {

    private Struct struct;
    private Heap heap;
    private ByteArrayInputStream byteArray;

    public Decoder(){
        struct = new Struct();
        heap = new Heap();
    }

    public Decoder(byte[] input){
        struct = new Struct();
        heap = new Heap();
        byteArray = new ByteArrayInputStream(input);
    }


    public String unpack(byte[] input) throws Exception {
        byteArray = new ByteArrayInputStream(input);

        return unpackFile();
    }

    public String unpackFile() throws Exception {
        //Create frequency list
        var frequencies = getFrequencies();

        //Create node tree
        var tree = createTree(frequencies);

        //Packets
        long packedBits, packedBytes = read("III")[2];

        packedBits = packedBytes;

        var buffer = new byte[(int)packedBytes];

        var packed = byteArray.read(buffer);

        return decode(tree, frequencies, buffer, packedBits);
    }

    private String decode(Node tree, List<Frequency> frequencies, byte[] buffer, long packedBits) {
        var bytes = Arrays.copyOfRange(buffer, 0,(int)packedBits);
        String result = "";
        int pos = 0;

        while (pos < bytes.length){

            var node = tree;
            while (true){
                if(pos >= bytes.length) throw new RuntimeException("Invalid data: pos");

                var bit = bytes[pos] != 0;

                if(bit)
                    node = node.getRight();
                else
                    node = node.getLeft();

                pos++;

                if(node == null)
                    throw new RuntimeException("Invalid data: node");

                if(node.getLeft() == null || node.getRight() == null)
                    break;
            }
            result += node.getC();
        }

        return result;
    }

    public List<Frequency> getFrequencies() throws Exception {
        var charCount = read("III")[2];

        var result = new ArrayList<Frequency>();

        for(int i = 0; i < charCount; i++){
            var count = read("I")[0];
            var character = read("cxxx")[0];

            result.add(new Frequency((char) character, count));
        }

        return result;
    }

    public Node createTree(List<Frequency> frequencies){

        for (var e : frequencies) {
            heap.push(new Node(String.valueOf(e.character()), e.frequency(), null, null));
        }

        while (heap.size() > 1) {
            var x = heap.pop();
            var y = heap.pop();

            var node = new Node(x.getC() + y.getC(), x.getFreq() + y.getFreq(), x, y);

            heap.push(node);
        }

        return heap.getRoot();
    }

    private long[] read(String fmt) throws Exception {
        var count = Struct.calcSize(fmt);

        var buffer = new byte[count];

        var readBytes = byteArray.read(buffer);

        return struct.unpack(fmt, buffer);
    }
}
