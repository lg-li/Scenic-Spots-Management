package cn.edu.neu.scenicspots.datastructure;

public class GraphVertex<V, E extends GraphEdge> implements Comparable<GraphVertex> {
    /**
     * 算法组件部分 - 迪杰斯特拉
     */
    public double shortestCost;
    public int shortestPathPrevVertexID;
    private V data;
    private LinkedList<E> edgeList;
    private int uniqueID = -1;
    private Map<Integer, Boolean> linkedVertexIDCache;

    public GraphVertex(V data) {
        this.data = data;
        edgeList = new LinkedList<>();
        linkedVertexIDCache = new Map<>();
    }

    public boolean addEdge(E edgeToInsert) {
        int uniqueID = edgeToInsert.getTargetVertex().getUniqueID();
        if (linkedVertexIDCache.get(uniqueID) == null) {
            linkedVertexIDCache.put(uniqueID, true);
            edgeList.insert(edgeToInsert);
            return true;
        }
        return false;
    }

    public V getData() {
        // 节点数据（如景点数据，类型跟随泛型N）
        return data;
    }

    public int getUniqueID() {
        // 通过HashCode生成运行时唯一标识符
        if (uniqueID == -1) {
            int hashCode = getData().hashCode();
            uniqueID = hashCode < 0 ? -hashCode : hashCode;
        }
        return uniqueID;
    }

    public LinkedList<E> getEdgeList() {
        // 获取节点边列表（邻接表后续元素）
        return edgeList;
    }

    @Override
    public int compareTo(GraphVertex target) {
        return (int) (this.shortestCost - target.shortestCost);
    }
}
