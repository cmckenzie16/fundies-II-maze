import tester.Tester;

//Represents a boolean-valued question over values of type T

interface IPred<T> {

  boolean apply(T t);

}

//---------------------------------------------------------------------------------

class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> value)  {
    this.header = value;
  }

  //effect: consumes a T value and inserts it at the front of this
  void addAtHead(T data) {
    this.header.addAtHead(data);
  }

  //effect: consumes a T value and inserts it at the end of this
  void addAtTail(T data) {
    this.header.addAtTail(data);
  }

  //removes the first Node from this and returns it's data
  T removeFromHead() {
    return this.header.removeFromHead();
  }

  //removes the last Node from this and returns it's data
  T removeFromTail() {
    return this.header.removeFromTail();
  }

  //counts the number of Nodes in this, not including the Sentinel
  int size() {
    return this.header.next.size();
  }

  //produces the first Node in this for which the given pred returns true
  ANode<T> find(IPred<T> pred) {
    return this.header.next.find(pred);
  }

  //effect: removes the given Node from this
  void removeNode(ANode<T> given) {
    if (!this.header.next.equals(this.header)) {
      given.remove();
    }
  }
  
  // returns true if the size = 0 
  boolean isEmpty() {
    return this.size() == 0; 
  }
  
  // returns true if the Deque contains the given data
  boolean contains(T given) {
    return this.header.next.contains(given);
  }
}

//---------------------------------------------------------------------------------
//abstract class for Node and Sentinel

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // constructor 
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next; 
    this.prev = prev; 
  }

  //remove this ANode<T>
  abstract T remove();

  //counts the number of Nodes 
  abstract int size();

  //produces the first Node for which the given pred returns true
  abstract ANode<T> find(IPred<T> pred);
  
  // returns true if the given data is in the deque
  abstract boolean contains(T given);
}

//---------------------------------------------------------------------------------
//class Node

class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    super(null, null);
    this.data = data;   
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    super(next, prev);
    this.data = data;

    if (next == null || prev == null) {
      throw new IllegalArgumentException("Given nodes are null!"); 
    }
    else {
      next.prev = this;
      prev.next = this;
    }
  }

  //remove this from the list and returns it's data
  T remove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;

    return this.data;
  }

  //counts the number of Nodes in the list 
  int size() {
    return 1 + this.next.size();
  }

  //produces the first Node for which the given pred returns true
  ANode<T> find(IPred<T> pred) {
    if (pred.apply(this.data)) {
      return this;
    }
    else {
      return this.next.find(pred);
    }
  }
  
  // returns true if the given node is in this deque
  boolean contains(T given) {
    return this.data.equals(given) || this.next.contains(given);
  }
}

//---------------------------------------------------------------------------------

class Sentinel<T> extends ANode<T> {

  Sentinel() {
    super(null, null);
    this.next = this; 
    this.prev = this;
  }

  //effect: consumes a T value and inserts it at the front of the list
  void addAtHead(T data) {
    new Node<T>(data, this.next, this); 
    //this.add(newNode);
  }

  //effect: consumes a T value and inserts it at the end of the list
  void addAtTail(T data) {
    new Node<T>(data, this, this.prev);
    // this.prev.add(newNode);
  }

  //removes this sentinels first node and returns it's data
  T removeFromHead() {
    return this.next.remove();
  }

  //removes this sentinels previous  node and returns it's data
  T removeFromTail() {
    return this.prev.remove();
  }

  // removes this node
  // can't remove a sentinel, throws an exception
  T remove() {
    throw new IllegalArgumentException("Empty list!"); 
  }

  //counts the number of Sentinels in the list
  int size() {
    return 0;
  }

  //produces the first Node in the list for which the given pred returns true
  ANode<T> find(IPred<T> pred) {
    return this;
  }
  
  // returns true if the given node is in this deque
  boolean contains(T given) {
    return false;
  }
}
