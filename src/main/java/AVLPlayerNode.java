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

    /**
    * Insert a new node into the tree and return the new root
    * @param newGuy the player to insert
    * @param value the value to insert the player at
    * @return the new root of the tree
    * @runtime O(log n)
     */
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
            rightWeight++;
        }
        reconnectParentToChild();
        updateBalanceFactor();
        return balance();
    }

    /**
     * Delete a node from the tree and return the new root
     * @param value the node with value to delete
     * @return the new root of the tree
     * @runtime O(log n)
     */
    public AVLPlayerNode delete(double value) {
        AVLPlayerNode current = this;
        if (value < this.value) {
            if (leftChild != null) {
                leftChild = leftChild.delete(value);
            }
        } else if (value > this.value) {
            if (rightChild != null) {
                rightChild = rightChild.delete(value);
                rightWeight--;
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
                rightWeight--;
            }
            current.parent = null;
        }
        current.reconnectParentToChild();
        updateBalanceFactor();
        return current.balance();
    }

    /**
     * Find the minimum node in the tree
     * @param node the root of the tree
     * @return the minimum node in the tree
     * @runtime O(log n)
     */
    private AVLPlayerNode findMin(AVLPlayerNode node) {
        while (node.leftChild != null) {
            node = node.leftChild;
        }
        return node;
    }

    /**
     * Balance the tree
     * @return the new root of the tree
     * @runtime O(1)
     */
    private AVLPlayerNode balance() {
        AVLPlayerNode current = this;
        if (balanceFactor == 2) {
            if (leftChild.balanceFactor >= 0) {
                current = leftChild;
                rotateRight();
            }else{
                current = leftChild.rightChild;
                AVLPlayerNode leftSubTree = leftChild.rightChild;
                leftChild.rotateLeft();
                leftChild = leftSubTree;
                rotateRight();
            }
            rotateLeft();
        } else if (balanceFactor == -2) {
            if (rightChild.balanceFactor <= 0) {
                current = rightChild;
                rotateLeft();
            }else {
                current = rightChild.leftChild;
                AVLPlayerNode rightSubTree = rightChild.leftChild;
                rightChild.rotateRight();
                rightChild = rightSubTree;
                rotateLeft();
            }
        }
        current.updateBalanceFactor();
        return current;
    }

    /**
     * Do a left rotation
     * @runtime O(1)
     */
    private void rotateLeft() {
        AVLPlayerNode newRoot = rightChild;
        if(newRoot == null){
            return;
        }
        rightChild = newRoot.leftChild;
        newRoot.leftChild = this;
        newRoot.parent = parent;
        parent = newRoot;
        reconnectParentToChild();
        updateBalanceFactor();
        newRoot.updateBalanceFactor();
        rightWeight -= newRoot.rightWeight + 1;
    }

    /**
     * Do a right rotation
     * @runtime O(1)
     */
    private void rotateRight() {
        AVLPlayerNode newRoot = leftChild;
        if(newRoot == null){
            return;
        }
        leftChild = newRoot.rightChild;
        newRoot.rightChild = this;
        newRoot.parent = parent;
        parent = newRoot;
        reconnectParentToChild();
        updateBalanceFactor();
        newRoot.updateBalanceFactor();
        newRoot.rightWeight = rightWeight + 1;
    }

    /**
     * Reconnect left child's or right child's parent to the node
     * @runtime O(1)
     */
    private void reconnectParentToChild() {
        if(leftChild != null){
            leftChild.parent = this;
        }else if(rightChild != null){
            rightChild.parent = this;
        }
    }

    /**
     * Update the balance factor of the node
     * @runtime O(log n)
     */
    private void updateBalanceFactor() {
        int leftHeight = (leftChild == null) ? -1 : leftChild.getHeight();
        int rightHeight = (rightChild == null) ? -1 : rightChild.getHeight();
        balanceFactor = leftHeight - rightHeight;
    }

    /**
     * Get the height of the node
     * @return the height of the node
     * @runtime O(log n)
     */
    private int getHeight() {
        int leftHeight = (leftChild == null) ? -1 : leftChild.getHeight();
        int rightHeight = (rightChild == null) ? -1 : rightChild.getHeight();
        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * This should return the Player object stored in the node with this.value == value
     * @param value the value to search for
     * @return the player with the value
     * @runtime O(log n)
     */
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
    
    /**
     * This should return the rank of the node with this.value == value
     * @param value the value to search for
     * @return the rank of the node with the value
     * @runtime O(n)
     */
    public int getRank(double value){
        List<AVLPlayerNode> nodes = inOrderTraversal();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).getValue() == value) {
                return nodes.size() - i;
            }
        }
        return -1;
    }

    /**
     * In order traversal of the tree
     * @return the list of nodes in order
     * @runtime O(n)
     */
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


    /**
     * This returns the tree of names with parentheses separating subtrees
     * eg "((bob)alice(bill))"
     * @return the tree of names
     * @runtime O(n)
     */
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

    /**
     * This should return a formatted scoreboard in descending order of value
     * @return the scoreboard
     * @runtime O(n log n)
     */
    public String scoreboard(){
        StringBuilder sb = new StringBuilder();

        PriorityQueue<AVLPlayerNode> maxHeap = new PriorityQueue<>(Comparator.comparingDouble(AVLPlayerNode::getValue).reversed());

        traverseInOrder(this, maxHeap);

        sb.append(String.format("%-20s %2s %5s%n", "Name", "ID", "ELO"));

        while (!maxHeap.isEmpty()) {
            AVLPlayerNode node = maxHeap.poll();
            Player player = node.getData();
            sb.append(String.format("%-20s %2d %5.2f%n", player.getName(), player.getID(), node.getValue()));
        }

        return sb.toString();
    }

    /**
     * In order traversal of the tree
     * @param node the root of the tree
     * @param maxHeap the max heap
     * @runtime O(n)
     */
    private void traverseInOrder(AVLPlayerNode node, PriorityQueue<AVLPlayerNode> maxHeap) {
        if (node == null) {
            return;
        }

        traverseInOrder(node.getRightChild(), maxHeap);
        maxHeap.offer(node);
        traverseInOrder(node.getLeftChild(), maxHeap);
    }
	
}
    
	
