package com.gn4k.dailybites.SendNotificationClasses;

public class Data {
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    String  Title;

    public Data(String title, String message) {
        Title = title;
        Message = message;
    }

    public Data() {}
    String Message;
}
