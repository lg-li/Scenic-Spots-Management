package cn.edu.neu.scenicspots.gui;

import cn.edu.neu.scenicspots.Constant;
import cn.edu.neu.scenicspots.Main;
import cn.edu.neu.scenicspots.model.ScenicSpot;
import cn.edu.neu.scenicspots.storage.Storage;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import org.json.JSONObject;

public class ScenicSpotController {

    public static ScenicSpotController getController() {
        return controller;
    }

    private static ScenicSpotController controller;

    public ScenicSpotController() {
        controller = this;
    }

    @FXML
    protected JFXTextField name;
    @FXML
    protected JFXTextArea description;
    @FXML
    protected JFXTextField popular;
    @FXML
    protected JFXToggleButton hasRestArea;
    @FXML
    protected JFXToggleButton hasToilet;

    private static String editingID = "";

    public static void setEditingID(String editingID) {
        ScenicSpotController.editingID = editingID;
    }

    public void setScenicSpot(ScenicSpot spot) {
        name.setText(spot.getName());
        popular.setText(String.valueOf(spot.getPopular()));
        description.setText(spot.getDescription());
        hasRestArea.setSelected(spot.isHasRestArea());
        hasToilet.setSelected(spot.isHasToilet());
        editingID = String.valueOf(spot.getUniqueID());
        System.out.println("Setting spot:" + new JSONObject(spot).toString());
    }

    public void reset() {
        name.setText("");
        popular.setText("");
        description.setText("");
        hasRestArea.setSelected(false);
        hasToilet.setSelected(false);
        editingID = "";
    }

    @FXML
    public void saveScenicSpot() {
        String spotName = name.getText();
        String spotPopular = popular.getText();
        String spotDescription = description.getText();
        boolean spotHasRestArea = hasRestArea.isSelected();
        boolean spotHasToilet = hasToilet.isSelected();
        if(spotName.equals("")||spotPopular.equals("")||!Constant.isNumeric(spotPopular)||spotDescription.equals("")){
            UIEngine.executeJS("mdui.alert('不正确的输入格式。')");
        }
        JSONObject newSpot = new JSONObject();
        newSpot.put("name", spotName);
        newSpot.put("description", spotDescription);
        newSpot.put("popular", Integer.valueOf(spotPopular));
        newSpot.put("hasRestArea", spotHasRestArea);
        newSpot.put("hasToilet", spotHasToilet);
        if (!editingID.equals("")) {
            newSpot.put("id", editingID);
        }
        System.out.println("Saving..." + newSpot.toString() + "  ID=" + editingID);
        // Add to
        Storage.getStarted().update("ScenicSpot", editingID, newSpot);
        Storage.getStarted().saveAndRefresh();
        if (editingID.equals("")) {
            UIEngine.addVertex(newSpot); // 新建
            UIEngine.addToVertexList(newSpot);
        }
        UIEngine.refreshGraph();
        UIEngine.initData();
    }
}
