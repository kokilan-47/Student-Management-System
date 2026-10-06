package com.example.nithuinstitueapp.common;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationController {

    public static void errorNotification(String title, String message) {
        build(title, message).showError();
    }

    public static void informNotification(String title, String message) {
        build(title, message).showInformation();
    }

    public static void warningNotification(String title, String message) {
        build(title, message).showWarning();
    }

    private static Notifications build(String title, String message) {
        return Notifications.create()
                .title(title)
                .text(message)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(4));
    }
}
