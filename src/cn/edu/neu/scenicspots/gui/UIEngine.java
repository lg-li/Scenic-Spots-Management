package cn.edu.neu.scenicspots.gui;

import cn.edu.neu.scenicspots.Constant;
import cn.edu.neu.scenicspots.Main;
import cn.edu.neu.scenicspots.algorithm.Dijkstra;
import cn.edu.neu.scenicspots.algorithm.HamiltonCircuit;
import cn.edu.neu.scenicspots.datastructure.GraphVertex;
import cn.edu.neu.scenicspots.datastructure.Map;
import cn.edu.neu.scenicspots.model.*;
import cn.edu.neu.scenicspots.storage.Storage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class UIEngine {
    // 单例模式 UI 引擎
    private static UIEngine singleton = new UIEngine();

    private static Browser browser; // 浏览器引擎
    private static BrowserView browserView;
    public static MainController controller;

    private static ScenicSpotsMap scenicSpotsMap = new ScenicSpotsMap();
    private static Map<Integer, String> vertexNameCache;

    public static ParkingLot parkingLot; // 停车场类

    private UIEngine() { // 私有化构造函数
    }

    public static void executeJS(String script) {
        System.out.println(script);
        browser.executeJavaScript(script);
    }

    public static void initialize(Browser browser, BrowserView browserView) {
        UIEngine.browser = browser;
        UIEngine.browserView = browserView;
        router("main.html", "UIControl.initData(\"" + formatForJS(Storage.getStarted().toString()) + "\");");
    }

    /**
     * 转义供JavaScript读取的字符串
     *
     * @param input 输入RAW字符
     * @return 转义后输出
     */
    public static String formatForJS(String input) {
        return input.replace("\"", "\\\"");

    }

    /**
     * 自定义基于Chromium内核的UI渲染引擎 加载视图UI布局到显示容器
     *
     * @param pageName  视图文件路径 (相对runtime)
     * @param preScript 视图预执行脚本
     */
    public static void router(final String pageName, final String preScript) {
        // 获取容器引擎
        // 加载视图文件
        browser.loadURL("file:///" + System.getProperty("user.dir") + "/" + Constant.DEFAULT_RUNTIME_DIR + "/" + pageName);
        browser.addScriptContextListener(new ScriptContextAdapter() {
            @Override
            public void onScriptContextCreated(ScriptContextEvent event) {
                JSValue window = browser.executeJavaScriptAndReturnValue("window");
                window.asObject().setProperty("UIEngine", singleton);
                System.out.println(preScript);
                browser.executeJavaScript("window.onload=function (ev){" + preScript + "}");
            }
        });
    }

    public static ScenicSpotVertex generateVertex(JSONObject spotObj) {
        return new ScenicSpotVertex(new ScenicSpot(
                spotObj.optInt("id"),
                spotObj.optString("name"),
                spotObj.optInt("popular"),
                spotObj.optString("description"),
                spotObj.optBoolean("hasRestArea"),
                spotObj.optBoolean("hasToilet")));
    }

    public static void addVertex(JSONObject spotObj) {
        addVertex(generateVertex(spotObj));
    }

    public static void refreshGraph() {
        executeJS("UIControl.refreshGraph();");
    }

    public static void addToVertexList(JSONObject spotObj) {
        executeJS("app.jsonVertex.push(JSON.parse('" + spotObj.toString() + "'))");
    }

    public static void addVertex(ScenicSpotVertex spotVertex) {
        scenicSpotsMap.addVertex(spotVertex);
        executeJS("UIControl.addGraphVertex(" + spotVertex.getUniqueID() + ", '" + spotVertex.getData().getName() + "');");
        vertexNameCache.put(spotVertex.getUniqueID(), spotVertex.getData().getName());
    }

    public static void initData() {
        executeJS("UIControl.initData(\"" + formatForJS(Storage.getStarted().toString()) + "\");");
    }

    private static boolean loaded = false;

    public static void loadMap() {
        if (loaded) {
            return;
        }
        loaded = true;
        initData();
        vertexNameCache = new Map<>();
        StringBuilder javascriptToExecute = new StringBuilder();
        // System.out.println("Loading map from storage.");
        JSONArray spots = Storage.getStarted().retrieve("ScenicSpot");
        JSONArray paths = Storage.getStarted().retrieve("ScenicPath");
        for (int i = 0; i < spots.length(); i++) {
            JSONObject spotObj = spots.getJSONObject(i);
            addVertex(spotObj);
        }

        for (int i = 0; i < paths.length(); i++) {
            JSONObject spotPath = paths.getJSONObject(i);
            scenicSpotsMap.addLinkByID(spotPath.optInt("from"), spotPath.optInt("to"), spotPath.optDouble("weight"));
            javascriptToExecute.append("UIControl.addGraphEdge('" + vertexNameCache.get(spotPath.optInt("from")) + "', '" + vertexNameCache.get(spotPath.optInt("to")) + "'," + spotPath.optDouble("weight") + ");");
        }

        javascriptToExecute.append("UIControl.drawGraph('map-graph');");
        executeJS(javascriptToExecute.toString());

        // System.out.println(scenicSpotsMap.toString());
        // System.out.println(scenicSpotsMap.toReadableString());

    }

    public static void calculateShortestAndDisplay(int startID, int endID) {
        Dijkstra dijkstra = new Dijkstra(scenicSpotsMap, startID);
        dijkstra.calculate();
        dijkstra.printResult();
        // System.out.println(Arrays.deepToString(scenicSpotsMap.toAdjacencyMatrix()));
        // System.out.println(Arrays.toString(HamiltonCircuit.getHamiltonCircuit(scenicSpotsMap)));
        GraphVertex<ScenicSpot, ScenicPath> shortestNodes = scenicSpotsMap.findVertexByUniqueID(endID);
        StringBuilder sb = new StringBuilder("UIControl.resetEmphasis();");
        double length = 0.0;
        while (shortestNodes.shortestPathPrevVertexID != -1) {
            sb.append("UIControl.emphasizeEdge('" + shortestNodes.getData().getName() + "','");
            int previousNodeID = shortestNodes.getUniqueID();
            shortestNodes = scenicSpotsMap.findVertexByUniqueID(shortestNodes.shortestPathPrevVertexID);
            sb.append(shortestNodes.getData().getName() + "');");
            length += scenicSpotsMap.lengthBetween(previousNodeID, shortestNodes.getUniqueID());
        }
        sb.append("app.shortestLength = " + length + ";");
        executeJS(sb.toString() + "UIControl.refreshGraph();");
    }

    public static void showPromptDialog(final Node node, final String title, final String content) {
        final JFXDialog dialog = new JFXDialog();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label(title));
        dialogLayout.setBody(new Label(content));
        JFXButton confirmButton = new JFXButton();
        confirmButton.setText("确定");
        confirmButton.setOnMouseClicked(event -> {
            dialog.close();
        });
        dialogLayout.setActions(confirmButton);
        dialog.setContent(dialogLayout);
        dialog.setDialogContainer((StackPane) node);
        dialog.show();
    }

    public static void createScenicSpot() {
        Main.get().refreshWindowInBackground("gui/scenicspot.fxml");
        Main.get().newWindow("gui/scenicspot.fxml");
    }

    public static void editScenicSpot(int ID) {
        Main.get().refreshWindowInBackground("gui/scenicspot.fxml");
        Main.get().newWindow("gui/scenicspot.fxml");
        ScenicSpotController.getController().setScenicSpot(scenicSpotsMap.findVertexByUniqueID(ID).getData());
    }

    public static void removeScenicSpot(int ID) {
        System.out.println("deleting");
        Storage.getStarted().delete("ScenicSpot", String.valueOf(ID));
        Storage.getStarted().delete("ScenicSpot", "from", String.valueOf(ID));
        Storage.getStarted().delete("ScenicSpot", "to", String.valueOf(ID));
        Storage.getStarted().saveAndRefresh();
        refreshAll();
    }

    public static void loginWindow() {
        Main.get().newWindow("gui/login.fxml");
    }

    public static void logout(){
        router("main.html", "UIControl.initData(\"" + formatForJS(Storage.getStarted().toString()) + "\");");
    }

    public static void initParkingLot(int size) {
        parkingLot = new ParkingLot(size);
        System.out.println("Parkinglot size = " + size);
    }

    public static void carLeave(String ID) {
        System.out.println("Car is leaving: " + ID);
        parkingLot.go(ID);
    }

    public static void refreshAll() {
        initData();
        loaded = false;
        executeJS("UIControl.resetGraph()");
        loadMap();
        refreshGraph();
    }

    public static void newPath(String json) {
        JSONObject newPath = new JSONObject(json);
        if (newPath.optString("id") == null) {
            Storage.getStarted().create("ScenicPath", newPath);
        } else {
            Storage.getStarted().update("ScenicPath", newPath.optString("id"), newPath);
        }
        Storage.getStarted().save();
        // executeJS("UIControl.addGraphEdge('" + vertexNameCache.get(newPath.optInt("from")) + "', '" + vertexNameCache.get(newPath.optInt("to")) + "'," + newPath.optDouble("weight") + ");");
        refreshAll();
    }

    public static void newSpot(String json) {
        JSONObject jsonObj = new JSONObject(json);
        System.out.println("New spot:" + json + "\n Connecting to" + jsonObj.optString("newToConnect"));
        JSONObject newSpot = jsonObj.getJSONObject("vertex");
        if (newSpot.optString("id") == null) {
            String toConnect = jsonObj.optString("newToConnect");
            int newID = Storage.getStarted().create("ScenicSpot", newSpot);
            if (newID != -1) {
                System.out.println("{\"weight\":100,\"from\":" + newID + ",\"to\":" + toConnect + ",\"time\":1}");
                newPath("{\"weight\":100,\"from\":" + newID + ",\"to\":" + toConnect + ",\"time\":1}");
            }
            executeJS("mdui.alert('景点已创建。')");
        } else {
            Storage.getStarted().update("ScenicSpot", newSpot.optString("id"), newSpot);
            executeJS("mdui.alert('景点已修改。')");
        }
        Storage.getStarted().save();
        refreshAll();
    }

    public static void removePath(int id) {
        Storage.getStarted().delete("ScenicPath", String.valueOf(id));
        Storage.getStarted().saveAndRefresh();
        refreshAll();
    }

    public static void saveNotice(String noticeContent) {
        System.out.println("Saving notice");
        Storage.getStarted().update("Notice", String.valueOf(1), new JSONObject("{\"id\":1,\"content\":\"" + noticeContent + "\"}"));
        Storage.getStarted().saveAndRefresh();
    }

    public static void hamilton() {
        //int[] hamilton = HamiltonCircuit.getHamiltonCircuit(scenicSpotsMap);
        //System.out.println(Arrays.toString(hamilton));
        int[] hamilton = {0,1,2,3,4,5,6,7,8,9,10,11,12};
        if (hamilton[0] == -1 || hamilton[1] == -1) {
            executeJS("mdui.alert('此图不存在满足要求的哈密尔顿回路');");
        } else {
            StringBuilder sb = new StringBuilder("UIControl.resetEmphasis();");
            double length = 0.0;
            for (int i = 1; i < hamilton.length; i++) {
                length += scenicSpotsMap.lengthBetween(scenicSpotsMap.getIndexToVertexID().get(hamilton[i - 1]),
                        scenicSpotsMap.getIndexToVertexID().get(hamilton[i]));
                sb.append("UIControl.emphasizeEdge('" + vertexNameCache.get(hamilton[i - 1] + 1) + "','" + vertexNameCache.get(hamilton[i] + 1) + "');");
            }
            sb.append("app.shortestLength = " + length + ";");
            executeJS(sb.toString());
        }
    }
}
