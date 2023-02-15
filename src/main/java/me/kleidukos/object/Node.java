package me.kleidukos.object;

public record Node(String character, long frequency, Node left, Node right) {

    public boolean left(Node other){
        return frequency < other.frequency;
    }

    public boolean right(Node other){
        return frequency <= other.frequency;
    }

    @Override
    public String toString() {
        return "Node{" +
                "character='" + character + '\'' +
                ", frequency=" + frequency +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
