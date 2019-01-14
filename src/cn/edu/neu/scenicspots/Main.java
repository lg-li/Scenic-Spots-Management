package cn.edu.neu.scenicspots;

import cn.edu.neu.scenicspots.datastructure.Map;
import cn.edu.neu.scenicspots.gui.UIEngine;
import com.jfoenix.controls.JFXDecorator;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {
    private static Main instance;

    public static void main(String[] args) {
        stageCache = new Map<>();
        launch(args);
    }

    public static Main get() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        try {
            URL location = getClass().getResource("gui/portal.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent root = fxmlLoader.load();
            primaryStage.setTitle("NEU 景区管理系统");
            JFXDecorator decorator = new JFXDecorator(primaryStage, root);
            decorator.setCustomMaximize(false);
            Scene scene = new Scene(decorator);
            primaryStage.setScene(scene);

            stageCache.put("gui/portal.fxml", primaryStage);

            Browser browser = new Browser();
            BrowserView browserView = new BrowserView(browser);
            browser.setZoomEnabled(false); // 禁用缩放
            primaryStage.setOnCloseRequest(event -> {
                // 关闭主窗体时销毁所有实例，释放资源
                Platform.runLater(() -> {
                    browser.dispose();
                    System.exit(0);
                });
            });

            UIEngine.initialize(browser, browserView);

            StackPane browserViewContainer = (StackPane) root.lookup("#browserViewContainer");
            browserViewContainer.getChildren().add(browserView);

            // UIEngine.showPromptDialog(root.lookup("#root"), "欢迎使用", "欢迎使用NEU景区管理系统。");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Stage> stageCache;

    public void destroyWindow(String xmlURL) {
        stageCache.get(xmlURL).close();
        stageCache.put(xmlURL, null);
    }

    public void refreshWindowInBackground(String xmlURL) {
        Platform.runLater(() -> {
            try {
                Parent root2 = FXMLLoader.load(Main.get().getClass().getResource(xmlURL));
                Stage primaryStage2 = stageCache.get(xmlURL);
                if (primaryStage2 != null) { // 替换原有实例
                    primaryStage2.close();
                }
                primaryStage2 = new Stage();
                primaryStage2.setTitle("NEU ScenicSpot Management System");
                JFXDecorator decorator2 = new JFXDecorator(primaryStage2, root2);
                decorator2.setCustomMaximize(false);
                primaryStage2.setScene(new Scene(decorator2));
                stageCache.put(xmlURL, primaryStage2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void newWindow(final String xmlURL) {
        Platform.runLater(() -> {
            try {
                Parent root2 = FXMLLoader.load(Main.get().getClass().getResource(xmlURL));
                Stage primaryStage2 = stageCache.get(xmlURL);
                if (primaryStage2 == null) { // 防止同一类型窗体创建多个实例
                    primaryStage2 = new Stage();
                    primaryStage2.setTitle("NEU ScenicSpot Management System");
                    JFXDecorator decorator2 = new JFXDecorator(primaryStage2, root2);
                    decorator2.setCustomMaximize(false);
                    primaryStage2.setScene(new Scene(decorator2));
                    stageCache.put(xmlURL, primaryStage2);
                }
                primaryStage2.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static Stage getStage(String name) {
        return stageCache.get(name);
    }
}
