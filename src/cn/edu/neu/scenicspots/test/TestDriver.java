package cn.edu.neu.scenicspots.test;

import cn.edu.neu.scenicspots.algorithm.Dijkstra;
import cn.edu.neu.scenicspots.algorithm.Floyd;
import cn.edu.neu.scenicspots.datastructure.Graph;
import cn.edu.neu.scenicspots.datastructure.GraphVertex;
import cn.edu.neu.scenicspots.datastructure.LinkedList;
import cn.edu.neu.scenicspots.datastructure.LinkedListIterator;

public class TestDriver {

    public static void main(String[] args){
        testDijkstra();
        //testFloyd();
    }

    public static void testDijkstra(){
        Graph graph = new Graph();
        GraphVertex vertex =  new GraphVertex(1);
        GraphVertex vertex2 =  new GraphVertex(2);
        GraphVertex vertex3 =  new GraphVertex(3);
        GraphVertex vertex4=  new GraphVertex(4);
        graph.addVertex(vertex);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);
        graph.addVertex(vertex4);
        graph.addLinkBetween(vertex, vertex2, 12);
        graph.addLinkBetween(vertex2, vertex3, 10);
        graph.addLinkBetween(vertex3, vertex4, 5);
        graph.addLinkBetween(vertex2, vertex4, 3);
        Dijkstra dijkstra = new Dijkstra(graph, vertex);
        dijkstra.calculate();
        dijkstra.printResult();
    }

    public static void testFloyd(){
        Graph graph = new Graph();
        GraphVertex vertex =  new GraphVertex(1);
        GraphVertex vertex2 =  new GraphVertex(2);
        GraphVertex vertex3 =  new GraphVertex(3);
        GraphVertex vertex4=  new GraphVertex(4);
        graph.addVertex(vertex);
        graph.addVertex(vertex2);
        graph.addVertex(vertex3);
        graph.addVertex(vertex4);
        graph.addLinkBetween(vertex, vertex2, 12);
        graph.addLinkBetween(vertex2, vertex3, 10);
        graph.addLinkBetween(vertex3, vertex4, 5);
        graph.addLinkBetween(vertex2, vertex4, 3);
        Floyd floyd =  new Floyd(graph.getVertexCount());
        LinkedList<Integer> path = floyd.findCheapestPath(0,3,graph);
        LinkedListIterator<Integer> iterator = path.iterator();
        int[][] mat = graph.toAdjacencyMatrix();
        for(int i=0;i<mat.length;i++){
            for(int j = 0;j< mat[i].length; j++){
                System.out.print(mat[i][j]+"\t");
            }
            System.out.println();
        }
//        while(iterator.hasNext()) {
//            System.out.print("->"+(iterator.next()+1));
//        }
    }
}
