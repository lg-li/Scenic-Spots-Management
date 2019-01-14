package cn.edu.neu.scenicspots.datastructure;

import cn.edu.neu.scenicspots.algorithm.Dijkstra;

/**
 * 图 类
 * @param <V> 节点内部数据类型
 * @param <E> 边数据类型
 */
public class Graph<V, E extends GraphEdge> {
    protected LinkedList<GraphVertex<V, E>> adjacencyList;
    private Map<Integer, GraphVertex<V, E>> vertexCache;
    private Map<Integer, Integer> indexToVertexID;
    private Map<Integer, Integer> vertexIDToIndex;
    private int currentIDPointer = 0;
    private int vertexCount = 0;
    private int edgeCount = 0;
    private Dijkstra dijkstra = null;

    public Graph() {
        adjacencyList = new LinkedList<>();
        vertexCache = new Map<>();
        indexToVertexID = new Map<>();
        vertexIDToIndex = new Map<>();
    }

    public LinkedList<GraphVertex<V, E>> getAdjacencyList() {
        return adjacencyList;
    }

    public Map<Integer, GraphVertex<V, E>> getVertexCache() {
        return vertexCache;
    }

    public Map<Integer, Integer> getIndexToVertexID() {
        return indexToVertexID;
    }

    public Map<Integer, Integer> getVertexIDToIndex() {
        return vertexIDToIndex;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public void addVertex(GraphVertex<V, E> vertexToAdd) {
        adjacencyList.insert(vertexToAdd); // 邻接表新建一行
        vertexCache.put(vertexToAdd.getUniqueID(), vertexToAdd); // 缓存节点到哈希表，以实现快速获取
        indexToVertexID.put(currentIDPointer, vertexToAdd.getUniqueID());
        vertexIDToIndex.put(vertexToAdd.getUniqueID(), currentIDPointer);
        currentIDPointer++;
        vertexCount++; // 计数器自增
    }

    public double lengthBetween(int uIDA, int uIDB){
        LinkedListIterator<E> edgeIterator = findVertexByUniqueID(uIDA).getEdgeList().iterator();
        while(edgeIterator.hasNext()){
            E edge = edgeIterator.next();
            if(edge.getTargetVertex().getUniqueID()==uIDB){
                return edge.getWeight();
            }
        }
        return 0;
    }

    public void addLinkBetween(GraphVertex<V, E> vertexSource, GraphVertex<V, E> vertexTarget, double weight) {
        addLinkByID(vertexSource.getUniqueID(), vertexTarget.getUniqueID(), weight);
    }

    public void addLinkByID(int sourceID, int targetID, double weight) {
        //优化后的加边代码 时间复杂度 O(1)
        if (sourceID == targetID) return;
        GraphVertex<V, E> sourceVertex = vertexCache.get(sourceID);
        GraphVertex<V, E> targetVertex = vertexCache.get(targetID);
        if (sourceVertex != null && targetVertex != null) {
            sourceVertex.addEdge((E) new GraphEdge<>(targetVertex, weight));
            targetVertex.addEdge((E) new GraphEdge<>(sourceVertex, weight));
        } else {
            System.out.println("正在尝试添加边到不存在的节点");
        }
        edgeCount++;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        LinkedListIterator<GraphVertex<V, E>> graphNodeIterator = adjacencyList.iterator();
        GraphVertex<V, E> node;
        while (graphNodeIterator.hasNext()) {
            node = graphNodeIterator.next();
            LinkedList<E> edgeList = node.getEdgeList();
            E graphEdge;
            LinkedListIterator<E> graphEdgeIterator = edgeList.iterator();
            buffer.append(node.getUniqueID()).append(":");
            while (graphEdgeIterator.hasNext()) {
                graphEdge = graphEdgeIterator.next();
                buffer.append(" -(")
                        .append(graphEdge.getWeight()).append(")->")
                        .append(graphEdge.getTargetVertex().getUniqueID());
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public GraphVertex<V, E> findVertexByUniqueID(int ID) {
        // 通过ID从缓存快速获取节点
        return vertexCache.get(ID);
    }

    public int[][] toAdjacencyMatrix(){
        int [][] adjacencyMatrix = new int[vertexCount][vertexCount];
        LinkedListIterator<GraphVertex<V, E>> graphNodeIterator = adjacencyList.iterator();
        GraphVertex<V, E> node;
        while (graphNodeIterator.hasNext()) {
            node = graphNodeIterator.next();
            int nodeIndex = vertexIDToIndex.get(node.getUniqueID());
            LinkedList<E> edgeList = node.getEdgeList();
            E graphEdge;
            LinkedListIterator<E> graphEdgeIterator = edgeList.iterator();
            while (graphEdgeIterator.hasNext()) {
                graphEdge = graphEdgeIterator.next();
                // 邻接矩阵内标记相邻为 1
                adjacencyMatrix[nodeIndex][vertexIDToIndex.get(graphEdge.getTargetVertex().getUniqueID())] = (int)graphEdge.getWeight();
            }
        }
        return adjacencyMatrix;
    }
}
