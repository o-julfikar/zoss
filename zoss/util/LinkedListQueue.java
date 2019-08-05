package zoss.util;

import static zoss.io.SmartWriter.*;

public class LinkedListQueue <E> implements Queue <E> {
    private Node <E> front, rear;
    private int size;
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public boolean hasPending() {
        return size > 0;
    }
    
    public void enqueue(E element) throws QueueOverflowException {
        if (size == 0) front = rear = new Node <E> (element, null);
        else rear = rear.setNext(new Node <E> (element, null));
        size++;
    }
    
    public E dequeue() throws QueueUnderflowException {
        E old = peek();
        Node <E> oldNode = front;
        front = front.next();
        size--;
        oldNode.setElement(null);
        oldNode.setNext(oldNode = null);
        return old;
    }
    
    public E peek() throws QueueUnderflowException {
        if (size == 0) throw new QueueUnderflowException();
        return front.element();
    }
    
    public Iterator <E> iterator() {
        return new Iterator <E> (front, null);
    }
    
    public Queue <E> clone() {
        Queue <E> clone = new LinkedListQueue <E>();
        for (Iterator <E> i = this.iterator(); i.hasNext(); i.progress()) {
            try {
                clone.enqueue(i.getData());
            } catch (QueueOverflowException e) {
                displayError("Queue if full");
            }
        }
        return clone;
    }
    
    public int search(E element) {
        for (Iterator <E> i = this.iterator(); i.hasNext(); i.progress()) if (i.getData() == element) return i.getIndex();
        return -1;
    }
    
    public Object[] toArray() {
        Object[] array = new Object[size];
        for (Iterator <E> i = this.iterator(); i.hasNext(); i.progress()) array[i.getIndex()] = i.getData();
        return array;
    }
    
    public String toString() {
        StringBuilder string = new StringBuilder("[");
        if (front != null) string.append(front.element());
        Iterator <E> i = this.iterator();
        for (i.progress(); i.hasNext(); i.progress()) string.append(", " + i.getData());
        string.append("]");
        return string.toString();
    }
}