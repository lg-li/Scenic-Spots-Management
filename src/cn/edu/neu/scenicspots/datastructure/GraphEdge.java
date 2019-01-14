package cn.edu.neu.scenicspots.datastructure;

public class GraphEdge<T> {
    // 图 边类
    private GraphVertex<T, ? extends GraphEdge> targetVertex; // 节点类型泛型参数：1. T：数据泛型类， 2. 任意继承边类的类
    private double weight;

    public GraphEdge(GraphVertex<T, ? extends GraphEdge> targetVertex, double weight) {
        this.targetVertex = targetVertex;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public GraphVertex<T, ? extends GraphEdge> getTargetVertex() {
        return targetVertex;
    }

    public void setTargetVertex(GraphVertex<T, ? extends GraphEdge> targetVertex) {
        this.targetVertex = targetVertex;
    }
}