import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javalib.impworld.WorldScene;
import javalib.worldimages.LineImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import tester.Tester;

// examples for graph 
public class ExamplesGraph {
  MazeWorld maze1; 
  HashMap<Vertex,Vertex> map1; 
  HashMap<Vertex,Vertex> map2; 
  HashMap<Vertex,Vertex> map3;
  
  Vertex A; 
  Vertex B; 
  Vertex C; 
  Vertex D; 
  Vertex E;
  Vertex F; 
  
  Edge EC; 
  Edge CD; 
  Edge AB; 
  Edge BE; 
  Edge BC; 
  Edge FD; 
  Edge AE; 
  Edge BF; 
  
  Exception error; 
  
  IComparator<Integer> byWeight; 
  
  // test grid
  ArrayList<ArrayList<Vertex>> testGrid; 
  ArrayList<ArrayList<Vertex>> emptyGrid; 
  
  // test array
  ArrayList<Vertex> testArray; 
  ArrayList<Vertex> emptyArray; 
  ArrayList<Edge> listEdge; 
  
  
 

  void initData() {
    // error 
    error = new UnsupportedOperationException("Key does not exist"); 
    
    // comparator
    byWeight = new ByWeight();
    
    // mazeworld 
    maze1 = new MazeWorld();
    
    // map 
    map1 = new HashMap<Vertex,Vertex>(); 
    map2 = new HashMap<Vertex,Vertex>(); 
    map3 = new HashMap<Vertex, Vertex>(); 
    
    // vertices
    A = new Vertex(1, 0, 0); 
    map1.put(this.A, this.A);
    B = new Vertex(2, 0, 1); 
    map1.put(this.B, this.B);
    C = new Vertex(3, 0, 2); 
    map1.put(this.C, this.C);
    D = new Vertex(4, 1, 0); 
    map1.put(this.D, this.D);
    E = new Vertex(5, 1, 1); 
    map1.put(this.E, this.E);
    F = new Vertex(6, 1, 2); 
    map1.put(this.F, this.F);
    
    
    // build map2
    map2.put(this.A, this.C);
    map2.put(this.B, this.C); 
    map2.put(this.C, this.C); 
    map2.put(this.D, this.E); 
    map2.put(this.E, this.E); 
    map2.put(this.F, this.E);
   
    
    // edges
    EC = new Edge(this.E, this.C, 15);
    CD = new Edge(this.C, this.D, 25);
    AB = new Edge(this.A, this.B, 30);
    BE = new Edge(this.B, this.E, 35);
    BC = new Edge(this.B, this.C, 40);
    FD = new Edge(this.F, this.D, 45);
    AE = new Edge(this.A, this.E, 50);
    BF = new Edge(this.B, this.F, 55);
    
    // list of edges
    listEdge = new ArrayList<Edge>(Arrays.asList(this.EC, this.CD));
    
    // grids
    testGrid = new ArrayList<ArrayList<Vertex>>(); 
    emptyGrid = new ArrayList<ArrayList<Vertex>>();
    
    testArray = new ArrayList<Vertex>();
    emptyArray = new ArrayList<Vertex>();
    
    // build grid
    testGrid = new ArrayList<ArrayList<Vertex>>(
        Arrays.asList(
            new ArrayList<Vertex>(Arrays.asList(
        this.A, this.B)), 
                new ArrayList<Vertex>(Arrays.asList(
            this.C, this.D)), 
                    new ArrayList<Vertex>(Arrays.asList(
                this.E, this.F))));
    
    // build array
    testArray = 
        new ArrayList<Vertex>(Arrays.asList(
            this.A, this.B, this.C, this.D, this.E, this.F));
  }
  
  // tests------------------------------------------------------
  
  // tests for MazeWorld
  void testMazeWorld(Tester t) {
    this.initData();
    
    t.checkExpect(this.maze1.verticesGrid.size(), MazeWorld.MAZE_HEIGHT);
    t.checkExpect(this.maze1.verticesGrid.get(0).size(), MazeWorld.MAZE_WIDTH);
    
    int prevWeight = 0;
    for (Edge edge : this.maze1.worklist) {
      // check that weight is within expected range
      t.checkRange(edge.weight, 0, 50);
      // check that the list is sorted by weight
      t.checkExpect(prevWeight <= edge.weight, true);
      prevWeight = edge.weight;
    }
    
    for (Edge edge : this.maze1.walls) {
      // check that each item is not in the spanning tree
      t.checkExpect(this.maze1.edgesInTree.contains(edge), false);
    }
    
    t.checkExpect(this.maze1.vertices.size(), MazeWorld.MAZE_WIDTH * MazeWorld.MAZE_HEIGHT);
    t.checkExpect(this.maze1.vertices.size(), this.maze1.edgesInTree.size() + 1);
    
  }
  
  // tests for compareTo
  void testCompareTo(Tester t) {
    this.initData();
    
    t.checkExpect(this.EC.compareTo(this.CD), -10);
    t.checkExpect(this.CD.compareTo(this.CD), 0);
    t.checkExpect(this.BF.compareTo(EC), 40);
  }
  
  // tests for addEdge
  void testAddEdge(Tester t) {
    this.initData();
    
    t.checkExpect(this.A.outEdges, new ArrayList<Edge>());
    this.A.addEdge(this.EC);
    t.checkExpect(this.A.outEdges, Arrays.asList(this.EC));
    this.A.addEdge(this.CD);
    t.checkExpect(this.A.outEdges, this.listEdge);
    this.C.addEdge(this.CD);
    t.checkExpect(this.C.outEdges, Arrays.asList(this.CD));
  }
  
  // test apply
  void testApply(Tester t) {
    this.initData();
    
    t.checkExpect(this.byWeight.apply(this.CD.weight, this.AB.weight), true);
    t.checkExpect(this.byWeight.apply(this.CD.weight, this.CD.weight), false);
    t.checkExpect(this.byWeight.apply(this.BF.weight, this.EC.weight), false);
    t.checkExpect(this.byWeight.apply(10, 20), true);
    t.checkExpect(this.byWeight.apply(20, 10), false);
    t.checkExpect(this.byWeight.apply(10, 10), false);
  }
  
  // testConvertToArrayList
  void testConvertToArrayList(Tester t) {
    this.initData();
    
    t.checkExpect(this.maze1.convertToArrayList(this.testGrid), this.testArray);
    t.checkExpect(this.maze1.convertToArrayList(this.emptyGrid), this.emptyArray);
  }
  
  // test MakeScene
  void testMakeScene(Tester t) {
    this.initData();
    
    // test scene
    WorldScene s = new WorldScene(
        MazeWorld.MAZE_HEIGHT * 10, 
        MazeWorld.MAZE_WIDTH * 10);
    //background
    s.placeImageXY(new RectangleImage(MazeWorld.MAZE_HEIGHT * 10, 
        MazeWorld.MAZE_WIDTH * 10, OutlineMode.SOLID, Color.gray), 
        MazeWorld.MAZE_HEIGHT * 10 / 2, MazeWorld.MAZE_WIDTH * 10 / 2);
    // start
    s.placeImageXY(new RectangleImage(10, 
        10, OutlineMode.SOLID, Color.GREEN), 
        5, 5);
    // end
    s.placeImageXY(new RectangleImage(10, 
        10, OutlineMode.SOLID, new Color(255, 0, 255)), 
        MazeWorld.MAZE_HEIGHT * 10 - 5, MazeWorld.MAZE_WIDTH * 10 - 5);
    //outer walls
    s.placeImageXY(new RectangleImage(MazeWorld.MAZE_HEIGHT * 10, 
        MazeWorld.MAZE_WIDTH * 10, OutlineMode.OUTLINE, Color.black), 
        MazeWorld.MAZE_HEIGHT * 10 / 2, MazeWorld.MAZE_WIDTH * 10 / 2);
    
    this.maze1.walls = new ArrayList<Edge>();
    t.checkExpect(this.maze1.makeScene(), s);
    
    // lines
    s.placeImageXY(new LineImage(new Posn(0, 10), Color.black), 0, 25);
    s.placeImageXY(new LineImage(new Posn(0, 10), Color.black), 10, 5);
    
    this.maze1.walls = this.listEdge;
    
    t.checkExpect(this.maze1.makeScene(), s);
    
    this.AB.to.x = this.CD.from.x; 
    this.listEdge.add(this.AB);
    this.maze1.walls = this.listEdge;
    s.placeImageXY(new LineImage(new Posn(10, 0), Color.black), 5, 10);
    t.checkExpect(this.maze1.makeScene(), s);
  }
  
  // tests for find
  void testFind(Tester t) {
    this.initData();
    
    t.checkExpect(this.maze1.find(this.A, this.map1), this.A); 
    t.checkExpect(this.maze1.find(this.A, this.map2), this.C); 
    t.checkExpect(this.maze1.find(this.C, this.map2), this.C); 
    
    t.checkException(this.error, this.maze1, "find", new Vertex(100, 0, 0), this.map2);
    t.checkException(this.error, this.maze1, "find", this.B, this.map3);
  }
  
  // tests for union
  void testUnion(Tester t) {
    this.initData();
    
    t.checkExpect(this.maze1.find(this.A, this.map1), this.A); 
    this.maze1.union(this.A, this.C, this.map1);
    t.checkExpect(this.maze1.find(this.A, this.map1), this.C); 
    
    t.checkExpect(this.maze1.find(this.A, this.map2), this.C); 
    this.maze1.union(this.A, this.C, this.map2);
    t.checkExpect(this.maze1.find(this.A, this.map2), this.C); 
    
    this.maze1.union(this.B, this.F, this.map2);  
    t.checkExpect(this.maze1.find(this.B, this.map2), this.E); 
    
    t.checkException(this.error, this.maze1, "union", new Vertex(100, 0, 0), this.A, this.map2);
    t.checkException(this.error, this.maze1, "union", this.B , this.A, this.map3);
  }
  
  // tests drawing
  void testDraw(Tester t) {
    this.initData();
   
   this.maze1.bigBang(MazeWorld.MAZE_WIDTH * 10, MazeWorld.MAZE_HEIGHT * 10, .01);
  }
}
