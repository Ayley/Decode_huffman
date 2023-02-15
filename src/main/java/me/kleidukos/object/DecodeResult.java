package me.kleidukos.object;

import java.util.List;

public record DecodeResult(String result, Node node, List<Frequency> frequencies, byte[] input) { }