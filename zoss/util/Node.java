package zoss.util;

public class Node <E> {
    private E element;
    private Node <E> next;
    
    public Node(E element, Node <E> next) {
        setElement(element);
        setNext(next);
    }
    
    public E setElement(E element) {
        return this.element = element;
    }
    
    public Node <E> setNext(Node <E> next) {
        return this.next = next;
    }
    
    public E element() {
        return element;
    }
    
    public Node <E> next() {
        return next;
    }
}