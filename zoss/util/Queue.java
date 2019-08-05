package zoss.util;

public interface Queue <E> {
    public int size();
    public boolean isEmpty();
    public boolean hasPending();
    public void enqueue(E element) throws QueueOverflowException;
    public E dequeue() throws QueueUnderflowException;
    public E peek() throws QueueUnderflowException;
    public int search(E element);
    public Iterator <E> iterator();
    public Queue <E> clone();
    public Object[] toArray();
    public String toString();
}