package cn.edu.neu.scenicspots.model;

import cn.edu.neu.scenicspots.datastructure.*;

public class ScenicSpotsMap extends Graph<ScenicSpot, ScenicPath> {
    public ScenicSpotsMap() {
        super();
    }

    public String toReadableString() {
        StringBuilder buffer = new StringBuilder();
        LinkedListIterator<GraphVertex<ScenicSpot, ScenicPath>> graphNodeIterator = adjacencyList.iterator();
        GraphVertex<ScenicSpot, ScenicPath> node;
        while (graphNodeIterator.hasNext()) {
            node = graphNodeIterator.next();
            LinkedList<ScenicPath> edgeList = node.getEdgeList();
            GraphEdge<ScenicSpot> graphEdge;
            LinkedListIterator<ScenicPath> graphEdgeIterator = edgeList.iterator();
            buffer.append("[节点-").append(node.getData().getName()).append("-(识别码:").append(node.getUniqueID()).append(")]:");
            while (graphEdgeIterator.hasNext()) {
                graphEdge = graphEdgeIterator.next();
                buffer.append(" -(").append(graphEdge.getWeight()).append(")->").append("[节点-").append(graphEdge.getTargetVertex().getData().getName()).append("-(哈希识别码:").append(graphEdge.getTargetVertex().getUniqueID()).append(")]");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
