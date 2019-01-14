package cn.edu.neu.scenicspots.gui;

import cn.edu.neu.scenicspots.Constant;
import cn.edu.neu.scenicspots.Main;
import cn.edu.neu.scenicspots.storage.Storage;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    protected JFXTextField username;

    @FXML
    protected JFXTextField password;

    @FXML
    protected Label title;

    public MainController() {
    }

    @FXML
    public void login(){
        String strUsername = username.getText();
        String strPassword = password.getText();
        if(strUsername.equals(Constant.DEFAULT_USERNAME)&&strPassword.equals(Constant.DEFAULT_PASSWORD)){
            UIEngine.router("admin.html","UIControl.initData(\"" + UIEngine.formatForJS(Storage.getStarted().toString()) + "\");");
            Main.getStage("gui/login.fxml").hide();
            Main.get().refreshWindowInBackground("gui/scenicspot.fxml");
            // Platform.runLater(()->title.setText("NEU 景区管理系统 (管理员权限)")); // 挂在
        }
    }
}