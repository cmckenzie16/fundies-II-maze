// Represents a mutable collection of items
interface ICollection<T> {
  // Is this collection empty?
  boolean isEmpty();
  // EFFECT: adds the item to the collection
  void add(T item);
  // Returns the first item of the collection
  // EFFECT: removes that first item
  T remove();
}

// Stack 
class Stack<T> implements ICollection<T> {
  Deque<T> contents;
  Stack() {
    this.contents = new Deque<T>();
  }
  public boolean isEmpty() {
    return this.contents.isEmpty();
  }
  public T remove() {
    return this.contents.removeFromHead();
  }
  public void add(T item) {
    this.contents.addAtHead(item);
  }
}

// Queue 
class Queue<T> implements ICollection<T> {
  Deque<T> contents;
  Queue() {
    this.contents = new Deque<T>();
  }
  
  public boolean isEmpty() {
    return this.contents.isEmpty();
  }
  
  public T remove() {
    return this.contents.removeFromHead();
  }
  
  public void add(T item) {
    this.contents.addAtTail(item); // NOTE: Different from Stack!
  }
}