package me.kleidukos.object;

import me.kleidukos.util.Struct;

public record Frequency(char character, long frequency) {

    @Override
    public String toString() {
        return "Frequency{" +
                "character='" + character + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}
