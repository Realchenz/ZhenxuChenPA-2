import jdk.jfr.DataAmount;
import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Your code goes in this file
 * fill in the empty methods to allow for the required
 * operations. You can add any fields or methods you want
 * to help in your implementations.
 */
@Data
public class AVLPlayerNode{
    private Player data;
    private double value;
    private AVLPlayerNode parent;
    private AVLPlayerNode leftChild;
    private AVLPlayerNode rightChild;

    private int rightWeight;
    private int balanceFactor;
    
    public AVLPlayerNode(Player data,double value){
        this.data = data;
        this.value = value;
    }

    public AVLPlayerNode insert(Player newGuy, double value) {
        if (value < this.value) {
            if (leftChild == null) {
                leftChild = new AVLPlayerNode(newGuy, value);
                leftChild.parent = this;
            } else {
                leftChild = leftChild.insert(newGuy, value);
            }
        } else {
            if (rightChild == null) {
                rightChild = new AVLPlayerNode(newGuy, value);
                rightChild.parent = this;
            } else {
                rightChild = rightChild.insert(newGuy, value);
            }
        }

        updateBalanceFactor();
        return balance();
    }

    public AVLPlayerNode delete(double value) {
        if (value < this.value) {
            if (leftChild != null) {
                leftChild = leftChild.delete(value);
            }
        } else if (value > this.value) {
            if (rightChild != null) {
                rightChild = rightChild.delete(value);
            }
        } else {
            // Node to delete found
            if (leftChild == null && rightChild == null) {
                return null; // No child case
            } else if (leftChild == null) {
                return rightChild; // Only right child case
            } else if (rightChild == null) {
                return leftChild; // Only left child case
            } else {
                // Node has both left and right children
                AVLPlayerNode successor = findMin(rightChild);
                this.data = successor.data;
                this.value = successor.value;
                rightChild = rightChild.delete(successor.value);
            }
        }

        updateBalanceFactor();
        return balance();
    }

    private AVLPlayerNode findMin(AVLPlayerNode node) {
        while (node.leftChild != null) {
            node = node.leftChild;
        }
        return node;
    }

    private AVLPlayerNode balance() {
        if (balanceFactor == 2) {
            if (rightChild.balanceFactor < 0) {
                rightChild.rotateRight();
            }
            return rotateLeft();
        } else if (balanceFactor == -2) {
            if (leftChild.balanceFactor > 0) {
                leftChild.rotateLeft();
            }
            return rotateRight();
        }
        return this;
    }

    private AVLPlayerNode rotateLeft() {
        AVLPlayerNode newRoot = rightChild;
        rightChild = newRoot.leftChild;
        if (newRoot.leftChild != null) {
            newRoot.leftChild.parent = this;
        }
        newRoot.leftChild = this;
        newRoot.parent = parent;
        parent = newRoot;
        updateBalanceFactor();
        newRoot.updateBalanceFactor();
        return newRoot;
    }

    private AVLPlayerNode rotateRight() {
        AVLPlayerNode newRoot = leftChild;
        leftChild = newRoot.rightChild;
        if (newRoot.rightChild != null) {
            newRoot.rightChild.parent = this;
        }
        newRoot.rightChild = this;
        newRoot.parent = parent;
        parent = newRoot;
        updateBalanceFactor();
        newRoot.updateBalanceFactor();
        return newRoot;
    }

    private void updateBalanceFactor() {
        int leftHeight = (leftChild == null) ? -1 : leftChild.getHeight();
        int rightHeight = (rightChild == null) ? -1 : rightChild.getHeight();
        balanceFactor = rightHeight - leftHeight;
    }

    private int getHeight() {
        int leftHeight = (leftChild == null) ? -1 : leftChild.getHeight();
        int rightHeight = (rightChild == null) ? -1 : rightChild.getHeight();
        return 1 + Math.max(leftHeight, rightHeight);
    }

    //this should return the Player object stored in the node with this.value == value
    public Player getPlayer(double value){
        if (this.value == value) {
            return this.data;
        } else if (value < this.value && leftChild != null) {
            return leftChild.getPlayer(value);
        } else if (value > this.value && rightChild != null) {
            return rightChild.getPlayer(value);
        } else {
            return null;
        }
    }
    
    //this should return the rank of the node with this.value == value
    public int getRank(double value){
        List<AVLPlayerNode> nodes = inOrderTraversal();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getValue() == value) {
                return nodes.size() - i;
            }
        }
        return -1;
    }

    private List<AVLPlayerNode> inOrderTraversal() {
        List<AVLPlayerNode> nodes = new ArrayList<>();
        if (leftChild != null) {
            nodes.addAll(leftChild.inOrderTraversal());
        }
        nodes.add(this);
        if (rightChild != null) {
            nodes.addAll(rightChild.inOrderTraversal());
        }
        return nodes;
    }


    //this should return the tree of names with parentheses separating subtrees
    //eg "((bob)alice(bill))"
    public String treeString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        if (leftChild != null) {
            sb.append(leftChild.treeString());
        }

        sb.append(data.getName());

        if (rightChild != null) {
            sb.append(rightChild.treeString());
        }

        sb.append(")");
        return sb.toString();
    }

    //this should return a formatted scoreboard in descending order of value
    //see example printout in the pdf for the command L
    public String scoreboard(){
        StringBuilder sb = new StringBuilder();

        PriorityQueue<AVLPlayerNode> maxHeap = new PriorityQueue<>(Comparator.comparingDouble(AVLPlayerNode::getValue).reversed());

        traverseInOrder(this, maxHeap);

        while (!maxHeap.isEmpty()) {
            AVLPlayerNode node = maxHeap.poll();
            Player player = node.getData();
            sb.append(String.format("%-20s %2d %5.2f%n", player.getName(), player.getID(), node.getValue()));
        }

        return sb.toString();
    }

    private void traverseInOrder(AVLPlayerNode node, PriorityQueue<AVLPlayerNode> maxHeap) {
        if (node == null) {
            return;
        }

        traverseInOrder(node.getRightChild(), maxHeap);
        maxHeap.offer(node);
        traverseInOrder(node.getLeftChild(), maxHeap);
    }
	
}
    
	
