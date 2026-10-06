package com.example.nithuinstitueapp.common;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;


public class Common {
    public static void loadFXML(String fxmlPath, AnchorPane loadPane) {
            try {

                FXMLLoader loader = new FXMLLoader(Common.class.getResource(fxmlPath));
                Node content = loader.load();

                loadPane.getChildren().setAll(content);

                AnchorPane.setTopAnchor(content, 0.0);
                AnchorPane.setRightAnchor(content, 0.0);
                AnchorPane.setBottomAnchor(content, 0.0);
                AnchorPane.setLeftAnchor(content, 0.0);

            } catch (Exception e) {
                e.printStackTrace();
                NotificationController.errorNotification("Failed to Load the view",  fxmlPath);
            }
        }


}