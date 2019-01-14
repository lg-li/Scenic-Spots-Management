package cn.edu.neu.scenicspots.algorithm;


import cn.edu.neu.scenicspots.datastructure.*;

/**
 * 最短路径 Dijkstra 算法
 */
public class Dijkstra {
    private MinPriorityQueue<GraphVertex> vertexQueue;
    private GraphVertex startingVertex;
    private Graph graph;
    protected LinkedList<GraphVertex> adjacencyList;

    public Dijkstra(Graph graph, int startingVertexID) {
        this.graph = graph;
        this.startingVertex = graph.findVertexByUniqueID(startingVertexID);
        adjacencyList = graph.getAdjacencyList();
    }

    public Dijkstra(Graph graph, GraphVertex startingVertex) {
        this.graph = graph;
        this.startingVertex = startingVertex;
        adjacencyList = graph.getAdjacencyList();
    }

    public GraphVertex getStartingVertex() {
        return startingVertex;
    }

    public void setStartingVertex(GraphVertex startingVertex) {
        this.startingVertex = startingVertex;
    }

    private void init() {
        System.out.println("初始化信息 Vertex count = " + graph.getVertexCount() + " Edge count = " + graph.getEdgeCount());
        GraphVertex[] vertexList = new GraphVertex[graph.getVertexCount()];
        GraphVertex vertex;
        LinkedListIterator<GraphVertex> iterator = adjacencyList.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            vertex = iterator.next();
            // known.put(vertex.getUniqueID(), false); // 标记未被访问
            if (vertex.equals(startingVertex)) {
                startingVertex.shortestCost = 0.0;
            } else {
                vertex.shortestCost = Double.MAX_VALUE; // 设置cost为无穷大
            }
            vertex.shortestPathPrevVertexID = -1; //标记路径回溯为-1
            vertexList[i] = vertex; // 传递引用
            i++;
        }
        vertexQueue = new MinPriorityQueue<>(vertexList, graph.getVertexCount());
    }

    public void calculate() {
        init();
        // 起始节点cost为0，且标记未known
        startingVertex.shortestCost = 0.0;
        // GraphVertex<V, E> shortestVertex=findVertexByUniqueID(getShortestVertexID());GraphVertex<V, E> shortestVertex=findVertexByUniqueID(getShortestVertexID());
        GraphVertex shortestVertex;
        shortestVertex = vertexQueue.poll(); // 最小优先队列滚动
        while (shortestVertex != null) {
            System.out.println("polling..."+shortestVertex.getUniqueID());
            refreshShortestCostAndPathFromAdjacencyList(shortestVertex);
            shortestVertex = vertexQueue.poll(); // 最小优先队列滚动
        }
    }

    public void printResult() {
        System.out.println("各节点到 节点[" + startingVertex.getUniqueID() + "] 的最短距离和路径");
        GraphVertex currentVertex;
        GraphVertex vertexPointer;
        LinkedListIterator<GraphVertex> iterator = adjacencyList.iterator();
        while (iterator.hasNext()) {
            currentVertex = iterator.next();
            System.out.print("-节点[" + currentVertex.getUniqueID() + "]: 最短距离" + currentVertex.shortestCost
                    + "  最短路径: ");
            vertexPointer = currentVertex;
            do {
                System.out.print("节点[" + vertexPointer.getUniqueID() + "]");
                if (vertexPointer.shortestPathPrevVertexID != -1) {
                    System.out.print("->");
                    vertexPointer = (GraphVertex) graph.getVertexCache().get(vertexPointer.shortestPathPrevVertexID);
                    if (vertexPointer.shortestPathPrevVertexID == -1) {
                        System.out.print("节点[" + vertexPointer.getUniqueID() + "]");
                    }
                }
            } while (vertexPointer.shortestPathPrevVertexID != -1);
            System.out.println();
        }
    }

    private void refreshShortestCostAndPathFromAdjacencyList(GraphVertex shortestVertex) {
        // 刷新cost数组
        LinkedList<GraphEdge> edgeList = shortestVertex.getEdgeList();
        LinkedListIterator<GraphEdge> edgeIterator = edgeList.iterator();
        while (edgeIterator.hasNext()) {
            GraphEdge edge = edgeIterator.next();
            GraphVertex targetVertex = edge.getTargetVertex();
            // double newShortestCost = cost.get(shortestVertex.getUniqueID())+edge.getWeight();
            double newShortestCost = shortestVertex.shortestCost + edge.getWeight();
            // 若当前最短节点加上其邻接边的权小于cost中记录的最短cost大小，则替换cost中记录的最短cost为 （最短节点cost+当前边的weight）
            if (targetVertex.shortestCost > newShortestCost) {
                targetVertex.shortestCost = newShortestCost;
                targetVertex.shortestPathPrevVertexID = shortestVertex.getUniqueID();
            }
        }
    }
}