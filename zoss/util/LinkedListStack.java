package zoss.util;

public class LinkedListStack <E> implements Stack <E> {
    private Node <E> top;
    private int size;
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public boolean hasLoad() {
        return size > 0;
    }
    
    public void push(E element) throws StackOverflowException {
        top = new Node <E> (element, top);
        size++;
    }
    
    public E pop() throws StackUnderflowException {
        if (size == 0) throw new StackUnderflowException();
        E old = top.element();
        Node <E> oldNode = top;
        top = top.next();
        oldNode.setElement(null);
        oldNode.setNext(oldNode = null);
        size--;
        return old;
    }
    
    public E peek() throws StackUnderflowException {
        if (size == 0) throw new StackUnderflowException();
        return top.element();
    }
    
    public Iterator <E> iterator() {
        return new Iterator <E> (top, null);
    }
    
    public int search(E element) {
        for (Iterator <E> i = this.iterator(); i.hasNext(); i.progress()) {
            if (i.getData() == element) return i.getIndex();
        }
        return -1;
    }
    
    public Stack <E> clone() {
        Stack <E> clone = new LinkedListStack <E> ();
        Node <E> top = new Node <E> (this.top.element(), null);
        for (Node <E> pointer = top, source = this.top.next(); source != null; source = source.next(), pointer = pointer.next()) pointer.setNext(new Node <E> (source.element(), null));
        ((LinkedListStack <E>) clone).top = top;
        return clone;
    }
    
    public Object[] toArray() {
        Object[] array = new Object[size];
        for (Iterator <E> i = this.iterator(); i.hasNext(); i.progress()) {
            array[i.getIndex()] = i.getData();
        }
        return array;
    }
    
    public String toString() {
        StringBuilder string = new StringBuilder("[");
        if (top != null) string.append(top.element());
        Iterator <E> i = this.iterator();
        for (i.progress(); i.hasNext(); i.progress()) {
            string.append(", " + i.getData());
        }
        string.append("]");
        return string.toString();
    }
}