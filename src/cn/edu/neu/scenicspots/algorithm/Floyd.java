package cn.edu.neu.scenicspots.algorithm;

import cn.edu.neu.scenicspots.datastructure.Graph;
import cn.edu.neu.scenicspots.datastructure.LinkedList;

public class Floyd {
    private static double INF = Double.MAX_VALUE;
    // dist[i][j]=INF 即两者无连接
    private double[][] dist;
    // 顶点i 到 j的最短路径长度，初值是i到j的边的权重
    private int[][] path;
    private LinkedList<Integer> result=new LinkedList<>();

    public LinkedList<Integer> findCheapestPath(int begin, int end, Graph graph){
        floyd(graph.toAdjacencyMatrix());
        result.insert(begin);
        findPath(begin,end);
        result.insert(end);
        return result;
    }

    public void findPath(int i,int j){
        int k=path[i][j];
        if(k==-1)return;
        findPath(i,k);   //递归
        result.insert(k);
        findPath(k,j);
    }

    public void floyd(int[][] matrix){
        int size=matrix.length;
        //initialize dist and path
        for(int i=0;i< size;i++){
            for(int j=0;j< size;j++){
                path[i][j]=-1;
                dist[i][j]=matrix[i][j];
            }
        }
        for(int k=0;k< size;k++){
            for(int i=0;i< size;i++){
                for(int j=0;j< size;j++){
                    if(dist[i][k]!=INF&&
                        dist[k][j]!=INF&&
                        dist[i][k]+dist[k][j]< dist[i][j]){
                        dist[i][j]=dist[i][k]+dist[k][j];
                        path[i][j]=k;
                    }
                }
            }
        }

    }

    public Floyd(int size){
        this.path=new int[size][size];
        this.dist=new double[size][size];
    }
}
