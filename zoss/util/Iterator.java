package zoss.util;

public class Iterator <E> {
    Node <E> start, end;
    int index;
    
    public Iterator(Node <E> start, Node <E> end) {
        this.start = start;
        this.end = end;
    }
    
    public boolean hasNext() {
        return start != end;
    }
    
    public Node <E> getCursor() {
        return start;
    }
    
    public int getIndex() {
        return index;
    }
    
    public E getData() {
        if (start == null) return null;
        return start.element();
    }
    
    public void progress() {
        if (start == null) return;
        start = start.next();
        index++;
    }
    
    public void close() {
        start = end = null;
        index = 0;
    }
}