package cn.edu.neu.scenicspots.model;

public class ScenicSpot {
    private int uniqueID;
    private String name;
    private int popular;
    private String description; //景点描述
    private boolean hasRestArea; //有无休息区
    private boolean hasToilet; //有无公厕

    public ScenicSpot(int id, String name, int popular, String description, boolean hasRestArea, boolean hasToilet) {
        this.uniqueID = id;
        this.name = name;
        this.popular = popular;
        this.description = description;
        this.hasRestArea = hasRestArea;
        this.hasToilet = hasToilet;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopular() {
        return popular;
    }

    public void setPopular(int popular) {
        this.popular = popular;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasRestArea() {
        return hasRestArea;
    }

    public void setHasRestArea(boolean hasRestArea) {
        this.hasRestArea = hasRestArea;
    }

    public boolean isHasToilet() {
        return hasToilet;
    }

    public void setHasToilet(boolean hasToilet) {
        this.hasToilet = hasToilet;
    }
}
