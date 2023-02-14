import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static Struct struct;

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(new URI("https://eu-trade.naeu.playblackdesert.com/Trademarket/GetWorldMarketHotList")).POST(HttpRequest.BodyPublishers.ofString("{\"keyType\": 1, \"mainCategory\": 1, \"subCategory\": 1}"))
                .build();

        var result = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        System.out.printf(unpack(result.body()));
    }

    public static String unpack(byte[] bytes) throws Exception {
        struct = new Struct();
        return unpackFile(new ByteArrayInputStream(bytes));
    }

    private static String unpackFile(ByteArrayInputStream is) throws Exception {
        var freqs = getFreqs(is);

        System.out.println(freqs.entrySet());

        var heap = makeTree(freqs);

        var bytes = read(is, "III");

        var buffer = new byte[(int)bytes[0]];

        var packed = is.read(buffer);

        return decode(heap, buffer, (int) bytes[0]);
    }

    private static String decode(Heap heap, byte[] packed, long aByte) {
        var bits = new byte[packed.length];
        int i = 0;
        for (byte b : packed) {
            bits[i] = b;
            i++;
        }

        var pos = 0;

        String result = "";

        var tree = heap.pop();
        System.out.println(tree.getC());

        while (pos < bits.length) {
            var node = tree;
            while (true) {
                if (pos >= packed.length) break;

                var bit = bits[pos];

                if (bit >= 1) {
                    node = node.getRight();
                } else {
                    node = node.getLeft();
                }

                pos++;

                if (node == null) {
                    break;
                }

                if (node.getLeft() == null && node.getRight() == null) break;
            }
            result += node.getC();
        }

        return result;
    }

    private static Heap makeTree(Map<String, Long> freqs) {
        var heap = new Heap();

        for (var e : freqs.entrySet()) {
            heap.push(new Node(e.getKey(), e.getValue(), null, null));
        }

        while (heap.size() > 1) {
            var x = heap.pop();
            var y = heap.pop();

            var node = new Node(x.getC() + y.getC(), x.getFreq() + y.getFreq(), x, y);
            heap.push(node);
        }

        return heap;
    }

    private static Map<String, Long> getFreqs(ByteArrayInputStream is) throws Exception {
        long charsCount = read(is, "III")[0];

        Map<String, Long> map = new HashMap<>();

        for (int i = 0; i < charsCount; i++) {
            var count = read(is, "I")[0];
            var c = (char) read(is, "cxxx")[0];
            map.put(String.valueOf(c), count);
        }

        return map;
    }

    private static long[] read(ByteArrayInputStream is, String fmt) throws Exception {
        var i = struct.calcSize(fmt);

        byte[] buffer = new byte[i];

        var b = is.read(buffer);

        System.out.println(b);
        System.out.println(buffer.length);

        return struct.unpack(fmt, buffer);
    }
}