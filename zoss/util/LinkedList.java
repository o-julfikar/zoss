package zoss.util;

public class LinkedList<E> {
    private Node <E> head;
    private int size;
    
    public LinkedList() {
        head = new Node<E> (null, null);
    }
    
    public LinkedList(E elem, int length) {
        head = new Node <E> (null, null);
        Node <E> pointer = head;
        for (int i = 0; i < length; i++) {
            pointer = pointer.setNext(new Node <E> (elem, null));
        }
        size = length;
    }
    
    public LinkedList(E[] a) {
        head = new Node <E> (null, null);
        Node <E> pointer = head;
        for (int i = 0; i < a.length; i++) {
            pointer = pointer.setNext(new Node <E> (a[i], null));
        }
        size = a.length;
    }
    
    public LinkedList(Node <E> head) {
        this.head = head;
        size = countNode(head);
    }
    
    public int size() {
        return size;
    }
    
    public void printList() {
        System.out.print("[");
        Node <E> pointer = head.next();
        if (head.next() != null) {
            System.out.print(head.next().element());
            pointer = pointer.next();
        }
        for (; pointer != null; pointer = pointer.next()) System.out.printf(", %s", pointer.element());
        System.out.println("]");
    }
    
    public Node <E> nodeAt(int idx) {
        Node<E> pointer = head.next();
        for (int i = 0; pointer != null; pointer = pointer.next(), i++) if (i == idx) return pointer;
        return null;
    }
    
    public E get(int idx) {
        Node<E> pointer = head.next();
        for (int i = 0; pointer != null; pointer = pointer.next(), i++) if (i == idx) return pointer.element();
        return null;
    }
    
    public E set(int idx, E element) {
        Node<E> pointer = head.next();
        for (int i = 0; pointer != null; pointer = pointer.next(), i++) {
            if (i == idx) {
                E old = pointer.element();
                pointer.setElement(element);
                return old;
            }
        }
        return null;
    }
    
    public int indexOf(E element) {
        Node<E> pointer = head.next();
        for (int i = 0; pointer != null; pointer = pointer.next(), i++) if (pointer.element() == element) return i;
        return -1;
    }
    
    public boolean contains(E element) {
        return indexOf(element) > -1;
    }
    
    public Node <E> copyList() {
        Node <E> duplicateHead = new Node <E> (head.element(), null), pointer = duplicateHead, source = head.next();
        while (source != null) {
            pointer = pointer.setNext(new Node <E> (source.element(), null));
            source = source.next();
        }
        return duplicateHead;
    }
    
    public Node <E> reverseList() {
        Node <E> reversedHead = null, source = head;
        while ((source = source.next()) != null) {
            reversedHead = new Node <E> (source.element(), reversedHead);
        }
        return reversedHead = new Node <E> (null, reversedHead);
    }
    
    public void insert(E elem, int idx){
        if (idx < 0) {
            System.err.println("Invalid Index");
            return;
        }
        
        Node <E> pointer = head;
        for (int i = 0; pointer != null; pointer = pointer.next(), i++) {
            if (i == idx) {
                pointer.setNext(new Node <E> (elem, pointer.next()));
                size++;
                return;
            }
        }
        
        System.err.println("Invalid Index");
    }
    
    public E remove(int idx){
        if (idx < 0) return null;
        
        Node <E> pointer = head;
        for (int i = 0; pointer.next() != null; pointer = pointer.next(), i++) {
            if (i == idx) {
                E old = pointer.next().element();
                pointer.setNext(pointer.next().next());
                size--;
                return old;
            }
        }
        
        return null;
    }
    
    public void rotateLeft(){
        E oldElem = head.next().element();
        Node <E> pointer = head.setNext(head.next().next());
        while ((pointer = pointer.next()).next() != null);
        pointer.setNext(new Node <E> (oldElem, null));
    }
    
    public void rotateRight(){
        Node <E> pointer = head.next();
        while((pointer = pointer.next()).next().next() != null);
        head.setNext(new Node <E> (pointer.next().element(), head.next()));
        pointer.setNext(null);
    }
    
    public Iterator <E> iterator() {
        return new Iterator <E> (head.next(), null);
    }
    
    private int countNode(Node <E> node) {
        int count = 0;
        for(; node != null; node = node.next()) count++;
        return count;
    }
}