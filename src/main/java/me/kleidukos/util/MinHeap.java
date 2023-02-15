package me.kleidukos.util;

import me.kleidukos.object.Node;

import java.util.Stack;

public class MinHeap {

    private final Stack<Node> nodes = new Stack<>();
    private Node root;

    public void push(Node node){
        nodes.push(node);
        var childId = size() - 1;

        while (true){
            var parentId = (childId - 1) / 2;

            if(parentId < 0) parentId = 0;

            if (nodes.get(parentId).right(nodes.get(childId))) return;

            swap(parentId, childId);

            childId = parentId;

            if(childId <= 0) return;
        }
    }

    public Node pop() {
        var node = nodes.get(0);
        var last = nodes.pop();

        if(size() == 0) return node;

        nodes.set(0, last);

        var parentId = 0;
        var childId = 1;

        while (childId < size()){
            if(childId + 1 < size() && nodes.get(childId + 1).left(nodes.get(childId))) childId += 1;

            if(nodes.get(parentId).right(nodes.get(childId))) return node;

            swap(parentId, childId);

            parentId = childId;

            childId = 2 * childId + 1;
        }

        return node;
    }

    public void swap(int i, int x){
        var tmp = nodes.get(i);

        nodes.set(i, nodes.get(x));
        nodes.set(x, tmp);
    }

    public int size(){
        return nodes.size();
    }

    public Node getRoot(){
        if(root == null)
            root = pop();

        return root;
    }
}
