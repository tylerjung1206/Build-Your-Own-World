package core;
import java.util.*;

public class Graph<roomNode> {

    private Map<roomNode, List<roomNode>> adjList;

    public Graph() {
        this.adjList = new HashMap<>();
    }

    public void addVertex(roomNode vertex) {
        adjList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(roomNode from, roomNode to) {
        addVertex(from);
        addVertex(to);
        adjList.get(from).add(to);
        adjList.get(to).add(from);
    }

    public List<roomNode> getNeighbors(roomNode vertex) {
        return adjList.getOrDefault(vertex, new ArrayList<>());
    }

    public Set<roomNode> get() {
        return adjList.keySet();
    }

    public int size() {
        return adjList.size();
    }

}
