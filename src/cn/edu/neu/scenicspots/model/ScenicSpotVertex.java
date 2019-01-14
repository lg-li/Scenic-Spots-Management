package cn.edu.neu.scenicspots.model;

import cn.edu.neu.scenicspots.datastructure.GraphVertex;

public class ScenicSpotVertex extends GraphVertex<ScenicSpot, ScenicPath> {
    public ScenicSpotVertex(ScenicSpot data) {
        super(data);
    }

    @Override
    public int getUniqueID() {
        return getData().getUniqueID();
    }
}
