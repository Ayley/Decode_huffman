package me.kleidukos;

import me.kleidukos.object.DecodeResult;
import me.kleidukos.object.Frequency;
import me.kleidukos.object.Node;
import me.kleidukos.util.MinHeap;
import me.kleidukos.util.Struct;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class HuffmanDecoder {

    public static String decode(byte[] input) throws IOException {
        return unpackFile(new ByteArrayInputStream(input));
    }

    public static String decode(ByteArrayInputStream byteArray) throws IOException {
        return unpackFile(byteArray);
    }

    public static DecodeResult decodeResult(byte[] input) throws IOException {
        return unpackFileResult(new ByteArrayInputStream(input));
    }

    public static DecodeResult decodeResult(ByteArrayInputStream byteArray) throws IOException {
        return unpackFileResult(byteArray);
    }

    private static String unpackFile(ByteArrayInputStream byteArray) throws IOException {
        var frequencies = getFrequencies(byteArray);

        var minHeap = createTree(frequencies);

        var packets = read("III", byteArray);

        var buffer = new byte[(int) packets[1]];

        var read = byteArray.read(buffer);

        return decodeHuffman(minHeap, buffer, packets[0]);
    }

    private static DecodeResult unpackFileResult(ByteArrayInputStream byteArray) throws IOException {
        var frequencies = getFrequencies(byteArray);

        var minHeap = createTree(frequencies);

        var packets = read("III", byteArray);

        var buffer = new byte[(int) packets[1]];

        var read = byteArray.read(buffer);

        var result = decodeHuffman(minHeap, buffer, packets[0]);

        return new DecodeResult(result, minHeap.getRoot(), frequencies, byteArray.readAllBytes());
    }

    private static String decodeHuffman(MinHeap minHeap, byte[] buffer, long packet) {
        String result = "";

        var bitSet = createBitSet(buffer);

        int pos = 0;
        while (pos < packet){
            var node = minHeap.getRoot();

            while (true){
                if(pos >= bitSet.size()) throw new RuntimeException("Invalid data: pos");

                if(bitSet.get(pos))
                    node = node.right();
                else
                    node = node.left();

                pos++;

                if(node == null)
                    throw new RuntimeException("Invalid data: node");

                if(node.left() == null && node.right() == null)
                    break;
            }

            result += node.character();
        }

        return result;
    }

    private static BitSet createBitSet(byte[] buffer){
        var bitSet = new BitSet(buffer.length);

        var binaryString = "";

        for(var b : buffer)
            binaryString += Integer.toBinaryString(b & 255 | 256).substring(1);

        for(int i = 0; i < binaryString.length(); i++){
            if(binaryString.charAt(i) == '1')
                bitSet.set(i);
        }

        return bitSet;
    }

    //Create node tree from frequencies
    private static MinHeap createTree(List<Frequency> frequencies) {
        var minHeap = new MinHeap();

        for (var frequency : frequencies) {
            minHeap.push(new Node(String.valueOf(frequency.character()), frequency.frequency(), null, null));
        }

        while (minHeap.size() > 1){
            var a = minHeap.pop();
            var b = minHeap.pop();
            var parent = new Node(a.character() + b.character(), a.frequency() + b.frequency(), a, b);

            minHeap.push(parent);
        }

        return minHeap;
    }

    //Create a frequency list
    private static List<Frequency> getFrequencies(ByteArrayInputStream byteArray) throws IOException {
        var count = read("III", byteArray)[2];

        var result = new ArrayList<Frequency>();

        for (int i = 0; i < count; i++) {
            var frequency = read("I", byteArray)[0];
            var character = read("cxxx", byteArray)[0];

            result.add(new Frequency((char) character, frequency));
        }

        return result;
    }

    private static long[] read(String format, ByteArrayInputStream byteArray) throws IOException {
        var size = Struct.calcSize(format);

        var buffer = new byte[size];

        var read = byteArray.read(buffer);

        return Struct.unpack(format, buffer);
    }

}
