package com.runtimeverification.rvpredict.util.ll;

public class EfficientLinkedList<T> {
    private int size;
    private EfficientNode<T> headNode;
    private EfficientNode<T> tailNode;

    public EfficientLinkedList() {
        this.size = 0;
    }

    public int getLength() {
        return this.size;
    }

    public boolean isEmpty() {
        return (size <= 0);
    }

    /**
     * Retrieves the earliest inserted element (bottom element) from the store.
     *
     * <p>The method checks if the store is empty. If it is, an {@code IllegalArgumentException} is thrown.
     * Otherwise, the method returns the data of the head node, representing the earliest inserted element.
     *
     * @return The data of the head node, representing the earliest inserted element.
     * @throws IllegalArgumentException If the store is empty.
     * @see EfficientNode
     */
    public T bottom() {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("Cannot get bottom: Store is empty");
        }
        return this.headNode.getData();
    }

    /**
     * Retrieves the latest inserted element (top element) from the store.
     *
     * <p>The method checks if the store is empty. If it is, an {@code IllegalArgumentException} is thrown.
     * Otherwise, the method returns the data of the tail node, representing the latest inserted element.
     *
     * @return The data of the tail node, representing the latest inserted element.
     * @throws IllegalArgumentException If the store is empty.
     * @see EfficientNode
     */
    public T top() {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("Cannot get top: Store is empty");
        }
        return this.tailNode.getData();
    }

    /**
     * Removes and returns the earliest inserted element (bottom element) from the store.
     *
     * <p>The method first checks if the store is empty. If it is, an {@code IllegalArgumentException} is thrown
     * Otherwise, the method retrieves the data of the head node, representing the earliest inserted element. If
     * the size of the store is 1, indicating that it contains only one element, the head and tail nodes are set
     * to null. Otherwise, the head node is updated to the next node in the sequence.
     *
     * <p>The size of the store is decremented, and the retrieved data is returned.
     *
     * @return The data of the removed head node, representing the earliest inserted element.
     * @throws IllegalArgumentException If the store is empty.
     */
    public T removeBottom() {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("Cannot remove first: Store is empty");
        }
        T retData = this.headNode.getData();
        if (size == 1) {
            this.headNode = null;
            this.tailNode = null;
        } else {
            EfficientNode<T> nextNode = this.headNode.getNext();
            this.headNode = nextNode;
        }
        this.size = this.size - 1;
        return retData;
    }

    /**
     * Removes the bottom elements from the store up to the specified index.
     *
     * <p>The method first checks if the specified index is greater than the current size of the store.
     * If it is, an {@code IllegalArgumentException} is thrown with a descriptive error message. Otherwise,
     * the method iteratively removes elements from the bottom of the store up to the specified index using
     * the {@code removeBottom} method.
     *
     * @param index The index up to which bottom elements should be removed from the store.
     * @throws IllegalArgumentException If the specified index is greater than the current size of the store.
     * @see #removeBottom()
     */
    public void removeBottomPrefixOfLength(int index) {
        if (index > this.size) {
            throw new IllegalArgumentException(
                    "Array out of bound: removePrefix : index ="
                            + Integer.toString(index)
                            + ", size = "
                            + Integer.toString(this.size));
        }
        for (int k = 0; k < index; k++) {
            this.removeBottom();
        }
    }

    /**
     * Pushes a new node with the specified data to the top of the store.
     *
     * <p>If the store is empty, a new node is created and set as both the head and tail of the store.
     * If the store is not empty, the new node is appended to the current tail, and the tail is updated
     * to the newly added node. The size of the store is incremented.
     *
     * @param data The data to be added to the new node.
     * @see EfficientNode
     */
    public void pushTop(T data) {
        EfficientNode<T> newNode = new EfficientNode<T>(data);
        if (this.isEmpty()) {
            this.headNode = newNode;
            this.tailNode = newNode;
        } else {
            this.tailNode.setNext(newNode);
            this.tailNode = newNode;
        }
        this.size = this.size + 1;
    }

    /**
     * Sets the data of the top node in the store.
     *
     * <p>If the store is empty, an {@code IllegalArgumentException} is thrown with a message indicating
     * that the top cannot be retrieved from an empty store. If the store is not empty, the data of the
     * tail node (top node) is updated with the specified data.
     *
     * @param data The data to set for the top node.
     * @throws IllegalArgumentException If the store is empty.
     * @see EfficientNode
     */
    public void setTop(T data) {
        if (this.isEmpty()) {
            throw new IllegalArgumentException("Cannot get top: Store is empty");
        }
        this.tailNode.setData(data);
    }

    public EfficientNode<T> getHeadNode() {
        return this.headNode;
    }

    public String toString() {
        StringBuilder output = new StringBuilder("[");
        if (size >= 1) {
            output.append(this.headNode.getData().toString());
            EfficientNode<T> itrNode = this.headNode.getNext();
            for (int i = 1; i < size; i++) {
                output.append(", " + itrNode.getData().toString());
                itrNode = itrNode.getNext();
            }
        }
        output.append("]");
        return output.toString();
    }
}
