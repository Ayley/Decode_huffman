package me.kleidukos.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Struct {

    public static long[] unpack(String format, byte[] values) throws IOException {

        int size = calcSize(format);

        if (size != values.length)
            throw new RuntimeException("Format length and values not equal");

        long[] result = new long[format.length()];

        var bufferReader = new ByteArrayInputStream(values);

        for (int i = 0; i < format.length(); i++) {
            var buffer = new byte[calcSize(String.valueOf(format.charAt(i)))];

            var read = bufferReader.read(buffer);
            result[i] = unpackSingleData(format.charAt(i), buffer);
        }

        return result;
    }

    private static void reverseArray(byte[] array) {
        if (array != null) {
            int i = 0;

            for (int j = array.length - 1; j > i; ++i) {
                byte tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                --j;
            }

        }
    }

    private static long unpackSingleData(char format, byte[] values) {
        reverseArray(values);

        return switch (format) {
            case 'I' -> ByteBuffer.wrap(values).getInt();
            case 'c', 'x' -> ByteBuffer.wrap(values).get();
            default -> throw new IllegalStateException("Unexpected value: " + format);
        };
    }

    public static int calcSize(String format) {
        int counter = 0;
        for (var c : format.toCharArray()) {
            counter += switch (c) {
                case 'I' -> 4;
                case 'c', 'x' -> 1;
                default -> throw new IllegalStateException("Unexpected value: " + c);
            };
        }
        return counter;
    }
}
