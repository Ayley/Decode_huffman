import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.thshsh.struct.Struct;

public class HuffmanDecoder {

    public static void main(String[] args) {
        try {
            String test = "81000000000000000B000000060000002D000000090000003000000003000000310000000300000032000000020000003300000002000000340000000600000035000000030000003700000004000000380000000100000039000000020000007C000000850000001100000029000000D30C7890FB1D0E6E4B4C35DF1775BDAA90";
            byte[] hex = Hex.decodeHex((args.length == 1 ? args[0] : test).toCharArray());

            String decoded = decode(hex);
            System.out.println(decoded); // "53801-198-55428-4050|53802-0-17725-70000|"
        } catch (DecoderException e) {
            e.printStackTrace();
        }
    }

    private static Unpacked read(byte[] buffer, String format) {
        Struct<List<Object>> struct = Struct.create("<" + format);
        int size = struct.byteCount();
        byte[] packed = new byte[size];
        System.arraycopy(buffer, 0, packed, 0, size);

        // Unpacking or reading the data doesn't modify the passed buffer, so we
        // do it manually and return the resized buffer as well.
        byte[] resized = new byte[buffer.length - size];
        System.arraycopy(buffer, size, resized, 0, buffer.length - size);

        return new Unpacked(struct.unpack(packed), resized);
    }

    private static Frequencies getFrequencies(byte[] buffer) {
        Unpacked data = read(buffer, "3I");
        long chars = (long) data.get(2);

        Map<String, Long> freqs = new LinkedHashMap<String, Long>();
        for (int i = 0; i < chars; i++) {
            data = read(data.resized, "I");
            long freq = (long) data.get(0);

            data = read(data.resized, "c3b");
            String char_ = new String(new byte[] { (byte) data.get(0) });
            freqs.put(char_, freq);
        }

        return new Frequencies(freqs, data.resized);
    }

    private static Nod makeTree(Frequencies freqs) {
        MinHeap heap = new MinHeap();
        for (Map.Entry<String, Long> entry : freqs.freqs.entrySet()) {
            heap.push(new Nod(entry.getKey(), entry.getValue()));
        }

        while (heap.size() > 1) {
            Nod n1 = heap.pop();
            Nod n2 = heap.pop();
            Nod parent = new Nod(n1.c + n2.c, n1.f + n2.f, n1, n2);

            heap.push(parent);
        }

        return heap.pop();
    }

    public static String decode(byte[] buffer) {
        Frequencies freqs = getFrequencies(buffer);
        Nod tree = makeTree(freqs);
        // System.out.println("Freqs: " + freqs.freqs);
        // System.out.println("Tree: c:" + tree.c + "  f:" + tree.f);

        Unpacked data = read(freqs.resized, "3I");
        Long packedBits = (long) data.get(0);
        // long packedBytes = (long) data.get(1);
        // System.out.println("Packed bits: " + packedBits);
        // System.out.println("Packed bytes: " + packedBytes);

        BitList bits = new BitList(packedBits.intValue(), data.resized);
        String unpacked = "";
        int pos = 0;

        while (pos < bits.length()) {
            Nod node = tree;
            while (true) {
                if (pos >= bits.length()) {
                    throw new RuntimeException("Invalid data: pos (" + pos + ") >= bits.size (" + bits.length() + ")");
                }
                if (bits.get(pos)) {
                    node = node.r;
                } else {
                    node = node.l;
                }

                pos += 1;
                if (node == null) {
                    throw new RuntimeException("Invalid data: node");
                }

                if (node.l == null && node.r == null) {
                    break;
                }
            }
            unpacked += node.c;
        }

        return unpacked;
    }
}

//#region
// Decoding helper classes
class Unpacked {
    private List<Object> result;
    public byte[] resized;

    public Unpacked(List<Object> result, byte[] resized) {
        this.result = result;
        this.resized = resized;
    }

    public Object get(int index) {
        return result.get(index);
    }
}

class Frequencies {
    public Map<String, Long> freqs;
    public byte[] resized;

    public Frequencies(Map<String, Long> freqs, byte[] resized) {
        this.freqs = freqs;
        this.resized = resized;
    }
}

class BitList {
    private int numBits;
    private BitSet bits;

    public BitList(int numBits, byte[] buffer) {
        this.numBits = numBits;
        this.bits = new BitSet(numBits);

        String binaryString = "";
        for (byte b : buffer) {
            binaryString += Integer.toBinaryString(b & 255 | 256).substring(1);
        }

        for (int i = 0; i < numBits && i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '1') {
                this.bits.set(i);
            }
        }
    }

    public boolean get(int index) {
        return this.bits.get(index);
    }

    public void set(int index, boolean value) {
        this.bits.set(index, value);
    }

    public int length() {
        return this.numBits;
    }
}
//#endregion

//#region
// MinHeap implementation from https://github.com/shrddr/huffman_heap
class MinHeap {
    public LinkedList<Nod> heap;

    public MinHeap() {
        heap = new LinkedList<Nod>();
    }

    public int size() {
        return heap.size();
    }

    public void swap (int i, int j) {
        Nod temp = this.heap.get(i);
        this.heap.set(i, this.heap.get(j));
        this.heap.set(j, temp);
    }

    public void push(Nod node) {
        this.heap.add(node);
        int childIndex = this.heap.size() - 1;

        while (true) {
            int parentIndex = (childIndex - 1) / 2;
            if (parentIndex < 0) parentIndex = 0;

            if (this.heap.get(parentIndex).le(this.heap.get(childIndex))) return;

            this.swap(parentIndex, childIndex);
            childIndex = parentIndex;
            if (childIndex <= 0) return;
        }
    }

    public Nod pop() {
        Nod node = this.heap.get(0);
        Nod last = this.heap.removeLast();

        if (this.size() == 0) return node;
        this.heap.set(0, last);

        int parentIndex = 0;
        int childIndex = 1;
        while (childIndex < this.size()) {
            if (childIndex + 1 < this.size() && this.heap.get(childIndex + 1).lt(this.heap.get(childIndex))) {
                childIndex += 1;
            }

            if (this.heap.get(parentIndex).le(this.heap.get(childIndex))) {
                return node;
            }

            this.swap(parentIndex, childIndex);
            parentIndex = childIndex;
            childIndex = 2 * childIndex + 1;
        }

        return node;
    }
}

class Nod {
    String c;
    Long f;
    Nod l;
    Nod r;

    public Nod(String c, Long f, Nod l, Nod r) {
        this.c = c;
        this.f = f;
        this.l = l;
        this.r = r;
    }

    public Nod(String c, Long f) {
        this.c = c;
        this.f = f;
        this.l = null;
        this.r = null;
    }

    public boolean lt(Nod other) {
        return this.f < other.f;
    }

    public boolean le(Nod other) {
        return this.f <= other.f;
    }

    @Override
    public String toString() {
        return "Nod{" +
                "c='" + c + '\'' +
                ", f=" + f +
                ", l=" + l +
                ", r=" + r +
                '}'+
                '\n';
    }
}
//#endregion