public class Node {

    private final String c;
    private final long freq;
    private final Node left;
    private final Node right;

    public Node(String c, long freq, Node left, Node right) {
        this.c = c;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public boolean left(Node other){
        return freq < other.freq;
    }

    public String getC() {
        return c;
    }

    public long getFreq() {
        return freq;
    }

    public boolean right(Node other){
        return freq <= other.freq;
    }
}
