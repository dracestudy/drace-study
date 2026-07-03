package com.runtimeverification.rvpredict.util.ll;

public class EfficientNode<T> {
    private T data;
    private EfficientNode<T> next;

    public EfficientNode() {}

    public EfficientNode(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public EfficientNode<T> getNext() {
        return this.next;
    }

    public void setNext(EfficientNode<T> node) {
        this.next = node;
    }

    public boolean hasNext() {
        return this.next != null;
    }
}
