import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DijkstraShortestPath extends JPanel {
    private int[] distances;
    private Set<Integer> visited;
    private PriorityQueue<Node> priorityQueue;
    private int numVert;
    private List<List<Node>> adjList;
    private int[] parent;

    public DijkstraShortestPath(int numVert) {
        this.numVert = numVert;
        distances = new int[numVert];
        visited = new HashSet<>();
        priorityQueue = new PriorityQueue<>(numVert, new Node());
        parent = new int[numVert];
    }

    public void dijkstra(List<List<Node>> adjList, int start) {
        this.adjList = adjList;

        for (int i = 0; i < numVert; i++) {
            distances[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }

        priorityQueue.add(new Node(start, 0));
        distances[start] = 0;

        while (visited.size() != numVert) {
            int currentVertex = priorityQueue.remove().node;
            visited.add(currentVertex);
            checkNeighbours(currentVertex);
        }
        repaint();
    }

    private void checkNeighbours(int currentVertex) {
        for (Node vertex : adjList.get(currentVertex)) {
            if (!visited.contains(vertex.node)) {
                int newDistance = distances[currentVertex] + vertex.cost;
                if (newDistance < distances[vertex.node]) {
                    distances[vertex.node] = newDistance;
                    priorityQueue.add(new Node(vertex.node, distances[vertex.node]));
                    parent[vertex.node] = currentVertex;  // Update parent
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraph(g);
    }

    private void drawGraph(Graphics g) {
        int[][] posns = {
                {50, 50}, {150, 50}, {250, 150}, {150, 250}, {50, 150}
        };
        int radius = 20;

        for (int i = 0; i < numVert; i++) {
            g.setColor(Color.WHITE);
            g.fillOval(posns[i][0] - radius, posns[i][1] - radius, radius * 2, radius * 2);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Router" + i, posns[i][0] - 6, posns[i][1] + 4);
        }

        g.setColor(Color.BLACK);
        for (int i = 0; i < numVert; i++) {
            for (Node node : adjList.get(i)) {
                int[] start = posns[i];
                int[] end = posns[node.node];
                g.drawLine(start[0], start[1], end[0], end[1]);
            }
        }


        g.setColor(Color.BLUE);
        for (int i = 0; i < numVert; i++) {
            if (parent[i] != -1) {
                int[] start = posns[i];
                int[] end = posns[parent[i]];
                g.drawLine(start[0], start[1], end[0], end[1]);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Arya Pathak");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        int numVert = 5;
        int start = 0;

        List<List<Node>> adjList = new ArrayList<>();

        for (int i = 0; i < numVert; i++) {
            adjList.add(new ArrayList<>());
        }

        adjList.get(0).add(new Node(1, 9));
        adjList.get(0).add(new Node(2, 6));
        adjList.get(0).add(new Node(3, 5));
        adjList.get(0).add(new Node(4, 3));

        adjList.get(2).add(new Node(1, 2));
        adjList.get(2).add(new Node(3, 4));

        DijkstraShortestPath dpq = new DijkstraShortestPath(numVert);
        dpq.dijkstra(adjList, start);

        frame.add(dpq);
        frame.setVisible(true);
    }
}

class Node implements Comparator<Node> {
    public int node;
    public int cost;

    public Node() {
    }

    public Node(int node, int cost) {
        this.node = node;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2) {
        return Integer.compare(node1.cost, node2.cost);
    }
}


