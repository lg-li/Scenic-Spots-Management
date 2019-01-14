package cn.edu.neu.scenicspots.model;

import cn.edu.neu.scenicspots.datastructure.GraphEdge;
import cn.edu.neu.scenicspots.datastructure.GraphVertex;

public class ScenicPath extends GraphEdge<ScenicSpot> {

    public ScenicPath(GraphVertex targetNode, double weight) {
        super(targetNode, weight);
    }

}
