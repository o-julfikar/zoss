package zoss.util;

public interface Stack <E> {
    public int size();
    public boolean isEmpty();
    public boolean hasLoad();
    public void push(E element) throws StackOverflowException;
    public E pop() throws StackUnderflowException;
    public E peek() throws StackUnderflowException;
    public int search(E element);
    public Iterator <E> iterator();
    public Stack <E> clone();
    public Object[] toArray();
    public String toString();
}