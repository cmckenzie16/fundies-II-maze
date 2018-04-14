import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//*--------------------------------------------------------------------------*
//|  Playing Instructions/Documentation:                                     |
//|  -----------------------------------                                     |
//| -Press "b" if you would like the maze to solve itself breadth-first.     |
//| -Press "d" if you would like the maze to solve itself depth-first.       |
//| -Use the arrow keys to solve the maze manually, if you would like the    |
//| maze to solve itself after you have already started solving it manually, |
//| still press "b" or "d".                                                  |
//| -Press "r" to create a new random maze.                                  |
//|                                                                          |
//|  Extra Credit:                                                           |
//|  -------------                                                           |
//| -Press "v" to toggle the view of visited paths                           |
//| -Automatically builds maze (knocks down walls on each tick) at run       |
//| -Displays total moves and total wrong moves                              |
//*--------------------------------------------------------------------------*

//IComparator interface
interface IComparator<T> {

  // apply 
  boolean apply(T item1, T item2);
}
//-----------------------------------------------------------------

class ByWeight implements IComparator<Integer> {
  ByWeight(){}

  // apply
  public boolean apply(Integer item1, Integer item2) {
    return (item1 < item2);
  }
}

//-----------------------------------------------------------------
// represents a maze 

class MazeWorld extends World {
  //list of vertices
  ArrayList<Vertex> vertices;
  //list of a list of vertices
  ArrayList<ArrayList<Vertex>> verticesGrid;
  //representatives for vertices
  HashMap<Vertex, Vertex> representatives;
  HashMap<Vertex, Vertex> cameFromMap; 
  ArrayList<Edge> edgesInTree;
  // all edges in graph, sorted by weight
  ArrayList<Edge> worklist;
  //walls in the maze
  ArrayList<Edge> walls;
  //list of vertices that are already seen
  ArrayList<Vertex> alreadySeen;
  //final path that leads to end of maze
  ArrayList<Vertex> finalPath;
  Boolean isDraw;
  int counter;
  Player player;

  // defines an int constant for the width of the maze
  static final int MAZE_WIDTH = 60;

  //defines an int constant for the width of the maze
  static final int MAZE_HEIGHT = 60;

  //constructor
  MazeWorld() {
    this.edgesInTree = new ArrayList<Edge>();
    this.worklist = new ArrayList<Edge>();
    this.vertices = new ArrayList<Vertex>(); 
    this.verticesGrid = new ArrayList<ArrayList<Vertex>>(); 
    this.representatives = new HashMap<Vertex, Vertex>();
    this.cameFromMap = new HashMap<Vertex, Vertex>();
    this.alreadySeen = new ArrayList<Vertex>();
    this.finalPath = new ArrayList<Vertex>();
    this.isDraw = false;
    this.counter = 0;

    this.initMaze();

    this.player = new Player(vertices.get(0));
  }

  void initMaze() {
    // create list of list of vertices
    //row
    int counter = 0;

    for (int i = 0; i < MazeWorld.MAZE_HEIGHT; i++) {

      ArrayList<Vertex> row = new ArrayList<Vertex>();

      //column (vertex)
      for (int j = 0; j < MazeWorld.MAZE_WIDTH; j++) {
        row.add(new Vertex(counter, j, i));
        counter++;
      }

      this.verticesGrid.add(row);

    }

    Random rand = new Random(); 
    // create list of edges from the vertices
    for (int i = 0; i < MazeWorld.MAZE_HEIGHT; i++) {

      for (int j = 0; j < MazeWorld.MAZE_WIDTH; j++) {

        if (j < MazeWorld.MAZE_WIDTH - 1) {
          Edge newEdge = new Edge(this.verticesGrid.get(i).get(j), 
              this.verticesGrid.get(i).get(j + 1), 
              rand.nextInt(50));
          this.worklist.add(newEdge);
        }  
        if (i < MazeWorld.MAZE_HEIGHT - 1) {
          Edge newEdge = new Edge(this.verticesGrid.get(i).get(j), 
              this.verticesGrid.get(i + 1).get(j), 
              rand.nextInt(50));
          this.worklist.add(newEdge);
        }
      }
    }

    // convert from ArrayList<ArrayList>> grid of vertices to ArrayList
    this.vertices = this.convertToArrayList(this.verticesGrid);  

    // sort the worklist
    Collections.sort(worklist);

    // initialize all vertices to represent themselves
    for (ArrayList<Vertex> row : this.verticesGrid) {
      for (Vertex vertex : row) {
        representatives.put(vertex, vertex);
      }
    }

    int counter1 = 0;
    // while size of vertices list = n  > n - 1 size of edges list
    while (this.vertices.size() > this.edgesInTree.size() - 1 
        && counter1 < this.worklist.size()) {

      // pick the next cheapest edge of the graph
      Edge cheapestEdge = this.worklist.get(counter1);

      // if find(representatives, X) equals find(representatives, Y):
      if (!(this.find(cheapestEdge.from, this.representatives).equals(
          this.find(cheapestEdge.to, this.representatives)))) {

        // record this edge in edgesInTree
        this.edgesInTree.add(cheapestEdge);
        // union(representatives, find(representatives, X), find(representatives, Y))
        this.union(this.find(cheapestEdge.to, this.representatives), 
            this.find(cheapestEdge.from, this.representatives) , this.representatives);
      }
      counter1++;
    }


    // for each edge in minimum spanning tree, 
    // add the edge to the to and from vertices
    for (Edge edge : this.edgesInTree) {
      edge.to.addEdge(edge); 
      edge.from.addEdge(edge);
    }

    //for each edge that is in the worklist and not the spanning tree,
    //add as walls
    this.walls = new ArrayList<Edge>();
    for (Edge edge : this.worklist) {
      if (!this.edgesInTree.contains(edge)) {
        walls.add(edge);
      }
    } 
  }

  // finds the representative of the given vertex in the representatives hashmap
  public Vertex find(Vertex key, HashMap<Vertex, Vertex> map) {
    if (map.containsKey(key)) {
      if (map.get(key).equals(key)) {
        return key;
      }
      else {
        return this.find(map.get(key), map);
      }
    } 
    else {
      throw new UnsupportedOperationException("Key does not exist");
    }
  }

  // assigns the second vertex as the firsts vertices' representative
  public void union(Vertex key, Vertex value, HashMap<Vertex, Vertex> map) {
    if (map.containsKey(key)) {
      map.put(key, value);
    }
    else {
      throw new UnsupportedOperationException("Key does not exist");
    }
  }

  //converts the given ArrayList<ArrayList<Vertex>> into an ArrayList<Vertex>
  public ArrayList<Vertex> convertToArrayList(ArrayList<ArrayList<Vertex>> gridOfVertices) {
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();

    //for each arraylist (row)
    for (int i = 0; i < gridOfVertices.size(); i = i + 1) {
      //for the objects in the inner arraylists (column)
      for (int j = 0; j < gridOfVertices.get(i).size(); j = j + 1) {
        vertices.add(gridOfVertices.get(i).get(j));
      }
    }
    return vertices;
  }

  // draws the maze
  public WorldScene makeScene() { 
    WorldScene scene = new WorldScene(
        MazeWorld.MAZE_HEIGHT * 10, 
        MazeWorld.MAZE_WIDTH * 10);


    //background
    scene.placeImageXY(new RectangleImage(MazeWorld.MAZE_WIDTH * 10, 
        MazeWorld.MAZE_HEIGHT * 10, OutlineMode.SOLID, Color.gray), 
        MazeWorld.MAZE_WIDTH * 10 / 2, MazeWorld.MAZE_HEIGHT * 10 / 2);

    // start
    scene.placeImageXY(new RectangleImage(10, 
        10, OutlineMode.SOLID, Color.GREEN), 
        5, 5);

    // end
    scene.placeImageXY(new RectangleImage(10, 
        10, OutlineMode.SOLID, new Color(255, 0, 255)), 
        MazeWorld.MAZE_WIDTH * 10 - 5, MazeWorld.MAZE_HEIGHT * 10 - 5);

    //outer walls
    scene.placeImageXY(new RectangleImage(MazeWorld.MAZE_WIDTH * 10, 
        MazeWorld.MAZE_HEIGHT * 10, OutlineMode.OUTLINE, Color.black), 
        MazeWorld.MAZE_WIDTH * 10 / 2, MazeWorld.MAZE_HEIGHT * 10 / 2);

    //color rectangles that are already seen while solving
    for (int i = 0; i < counter && i < alreadySeen.size(); i++) {
      Vertex curDraw = alreadySeen.get(i);
      WorldImage colorCell = new RectangleImage(10, 
          10, OutlineMode.SOLID, new Color(66, 217, 244));

      scene.placeImageXY(colorCell, (curDraw.x * 10) + 5, (curDraw.y * 10) + 5);
    }

    //draw final path solution
    if (counter >= alreadySeen.size() || this.player.onVertex.equals(vertices.get(vertices.size() - 1))) {
      for (int i = 0; i < finalPath.size(); i++) {
        Vertex curDraw = finalPath.get(i);
        WorldImage colorCell = new RectangleImage(10, 
            10, OutlineMode.SOLID, new Color(66, 66, 244));

        scene.placeImageXY(colorCell, (curDraw.x * 10) + 5, (curDraw.y * 10) + 5);
      }
    }

    //inner walls
    for (Edge edge : this.worklist) {   
      if (edge.to.x == edge.from.x) {
        int x = edge.to.x * 10 + 5;
        int y = edge.to.y * 10;
        scene.placeImageXY(new LineImage(new Posn(10,0), Color.black), x, y);
      }
      else {
        int x = edge.to.x * 10;
        int y = edge.to.y * 10 + 5;
        scene.placeImageXY(new LineImage(new Posn(0,10), Color.black), x, y);
      }
    }

    //vertex that the player is on 
    scene.placeImageXY(new RectangleImage(10, 
        10, OutlineMode.SOLID, new Color(167, 66, 244)), 
        this.player.x * 10 + 5, this.player.y * 10 + 5);


    return scene;
  }

  //does something each tick 
  public void onTick() {
    
    //add 1 to counter 
    if (this.isDraw) {
      counter++;
    }

    this.removeOne();

  }

  //removes edge from worklist on each tick
  void removeOne() {
    ArrayList<Edge> worklistCopy = new ArrayList<Edge>(this.worklist);

    for (Edge edge : this.worklist) {
      if (!this.walls.contains(edge)) {
        worklistCopy.remove(edge);
        break;
      }
    } 
    this.worklist = worklistCopy;
  }

  //does something when specific keys are pressed
  public void onKeyEvent(String key) {
    //solves breadth-first
    if (key.equals("b")) {
      this.bfs(this.vertices.get(0), this.vertices.get(vertices.size() - 1)); 
      this.isDraw = true;
    }
    //solves depth-first
    if (key.equals("d")) {
      this.dfs(this.vertices.get(0), this.vertices.get(vertices.size() - 1));
      this.isDraw = true;
    }
    //creates new random maze
    if (key.equals("r")) {
      this.edgesInTree = new ArrayList<Edge>();
      this.worklist = new ArrayList<Edge>();
      this.vertices = new ArrayList<Vertex>(); 
      this.verticesGrid = new ArrayList<ArrayList<Vertex>>(); 
      this.representatives = new HashMap<Vertex, Vertex>();
      this.cameFromMap = new HashMap<Vertex, Vertex>();
      this.alreadySeen = new ArrayList<Vertex>();
      this.finalPath = new ArrayList<Vertex>();
      this.isDraw = false;
      this.counter = 0;

      this.initMaze();

      this.player = new Player(vertices.get(0));
    }
    //moves the player right, left, up, and down
    else if (key.equals("right")) {

      if (!alreadySeen.contains(this.player.onVertex)) {
        this.alreadySeen.add(this.player.onVertex);
      }

      if (this.player.x < MazeWorld.MAZE_WIDTH - 1
          && this.player.onVertex.hasEdge(verticesGrid.get(this.player.y).get(this.player.x + 1))) {

        cameFromMap.put(verticesGrid.get(this.player.y).get(this.player.x),
            (verticesGrid.get(this.player.y).get(this.player.x + 1)));

        this.player.updateVertex(verticesGrid.get(this.player.y).get(this.player.x + 1));

        counter++;
      }

      if (this.player.onVertex == vertices.get(vertices.size() -1)) {
        reconstruct(cameFromMap, this.vertices.get(0), vertices.get(vertices.size() -1));
        this.isDraw = true;
      }
    }

    else if (key.equals("left")) {

      if (!alreadySeen.contains(this.player.onVertex)) {
        this.alreadySeen.add(this.player.onVertex);
      }

      if (this.player.x > 0
          && this.player.onVertex.hasEdge(verticesGrid.get(this.player.y).get(this.player.x - 1))) {

        cameFromMap.put(verticesGrid.get(this.player.y).get(this.player.x),
            (verticesGrid.get(this.player.y).get(this.player.x - 1)));

        this.player.updateVertex( verticesGrid.get(this.player.y).get(this.player.x - 1));

        counter++;
      }

      if (this.player.onVertex == vertices.get(vertices.size() -1)) {
        reconstruct(cameFromMap, this.vertices.get(0), vertices.get(vertices.size() -1));
        this.isDraw = true;
      }
    }

    else if (key.equals("down")) {


      if (!alreadySeen.contains(this.player.onVertex)) {
        this.alreadySeen.add(this.player.onVertex);
      }

      if (this.player.y < MazeWorld.MAZE_HEIGHT - 1
          && this.player.onVertex.hasEdge(verticesGrid.get(this.player.y + 1).get(this.player.x))) {

        cameFromMap.put(verticesGrid.get(this.player.y).get(this.player.x),
            (verticesGrid.get(this.player.y + 1).get(this.player.x)));

        this.player.updateVertex(verticesGrid.get(this.player.y + 1).get(this.player.x));

        counter++;
      } 
      if (this.player.onVertex == vertices.get(vertices.size() -1)) {
        reconstruct(cameFromMap, this.vertices.get(0), vertices.get(vertices.size() -1));
        this.isDraw = true;
      }
    }

    else if (key.equals("up")) {

      if (!alreadySeen.contains(this.player.onVertex)) {
        this.alreadySeen.add(this.player.onVertex);
      }

      if (this.player.y > 0 
          && this.player.onVertex.hasEdge(verticesGrid.get(this.player.y - 1).get(this.player.x))) {

        cameFromMap.put(verticesGrid.get(this.player.y).get(this.player.x),
            (verticesGrid.get(this.player.y - 1).get(this.player.x)));

        this.player.updateVertex(verticesGrid.get(this.player.y - 1).get(this.player.x));

        counter++;

      } 
      if (this.player.onVertex == vertices.get(vertices.size() -1)) {
        reconstruct(cameFromMap, this.vertices.get(0), vertices.get(vertices.size() -1));
        this.isDraw = true;
      }
    }

    //System.out.println(this.isDraw);
  }


  //breadth first search
  boolean bfs(Vertex from, Vertex to) {
    return this.searchHelp(from, to, new Queue<Vertex>());
  }

  // depth first search
  boolean dfs(Vertex from, Vertex to) {
    return this.searchHelp(from, to, new Stack<Vertex>());
  }

  // helper for BFS and DFS
  boolean searchHelp(Vertex from, Vertex to, ICollection<Vertex> worklist) {
    HashMap<Vertex, Vertex> cameFrom = new HashMap<Vertex, Vertex>();
    ArrayList<Vertex> alreadySeen = new ArrayList<Vertex>();

    // initialize the worklist with the from vertex
    worklist.add(from);
    // as long as the worklist isn't empty...
    while (!worklist.isEmpty()) {
      Vertex next = worklist.remove();
      if (next.equals(to)) {
        for(Vertex v: cameFrom.keySet()) {
          System.out.println("key " + v.x + ", " + v.y);
          System.out.println("value " + cameFrom.get(v).x + ", " + cameFrom.get(v).y);
        }
        this.reconstruct(cameFrom, to, from);
        return true; // success!
      }
      else {
        // add all the neighbors of next to the worklist for further processing
        for (Edge e : next.outEdges) {
          // record the edge (next->n) in the cameFromEdge map
          if (e.to.equals(next) && !alreadySeen.contains(e.from)) {
            cameFrom.put(e.from , next);
            worklist.add(e.from);
          } 
          else if (e.from.equals(next) && !alreadySeen.contains(e.to)) {
            cameFrom.put(e.to, next);
            worklist.add(e.to);
          }
        } 
        // add next to alreadySeen, since we're done with it
        alreadySeen.add(next);
        this.alreadySeen = alreadySeen;
      }
    }
    // we haven't found the to vertex, and there are no more to try
    return false;
  }

  // reconstructs the path from end to start
  void reconstruct(HashMap<Vertex, Vertex> cameFrom, Vertex to, Vertex from) {
    Vertex currentVertex = to;

    this.finalPath.add(to);

    while (!currentVertex.equals(from)) {
      currentVertex = cameFrom.get(currentVertex);

      this.finalPath.add(currentVertex);
    }  
  }

}

// -----------------------------------------------------------------
// represents an edge between two vertices

class Edge implements Comparable<Edge> {
  Vertex from; 
  Vertex to; 
  int weight; 

  Edge(Vertex from, Vertex to, int weight) {
    this.from = from; 
    this.to = to; 
    this.weight = weight;
  }

  //compares this weight to given edge's weight
  public int compareTo(Edge given) {
    return this.weight - given.weight;
  }
}
//-----------------------------------------------------------------
// represents a vertex 

class Vertex {
  int id;
  //list of edges that connect vertices 
  ArrayList<Edge> outEdges;
  int x;
  int y;

  Vertex(int id, int x, int y){
    this.id = id; 
    this.outEdges = new ArrayList<Edge>();
    this.x = x;
    this.y = y;
  }

  // adds the given Edge to this Vertex's outEdges
  void addEdge(Edge given) {
    this.outEdges.add(given);
  }

  //determine if there is an edge between this vertex and given
  boolean hasEdge(Vertex given) {
    for (Edge e: outEdges) {
      if (e.to.equals(given) || e.from.equals(given)) {
        return true;
      }
    }
    return false; 
  }

}
//-----------------------------------------------------------------
//represents the player's place
class Player {
  int x;
  int y;
  Vertex onVertex;

  Player(Vertex onVertex) {
    this.x = onVertex.x;
    this.y = onVertex.y;
    this.onVertex = onVertex;
  }

  //updates the vertex the player is on
  void updateVertex(Vertex onVertex) {

    this.x = onVertex.x;
    this.y = onVertex.y;
    this.onVertex = onVertex;
  }

}














