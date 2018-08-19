package com.shoot.PostHidden;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by Beloved on 08-Mar-18.
 */
//class to display notification
public class PostNotification {
    private static Stage _stage;
    private static Pane pane;
    static String text ;
    static Node[] _node;
//======================================================================================================================
    //method displays information for @canvasRectangle.java
    public  Node notify(Stage stage,String message) {
        _stage=stage;
        text=message;
        pane = new Pane() ;
        createPaneChildren();
        return pane;
    }
    private  void createPaneChildren() {
        _stage.setOnShown(event ->notification(Pos.TOP_RIGHT) );
    }
    private  void notification(Pos pos) {
        Notifications notificationBuilder = Notifications.create()
                .title("Hello!"  )
                .text(text)
                .darkStyle()
                .hideAfter(Duration.seconds(6))
                .position(pos)
                .graphic(new ImageView(new Image("file:resource\\a.jpg",50,50,true,true)));
        notificationBuilder.show();
    }
//======================================================================================================================
    //Method displays information for @Mp4Encode.java
    public  Node notify(Stage stage,Node... node) {
        _stage=stage;
        _node=node;
        pane = new Pane() ;
        createPaneChildrenControl();
        return pane;
    }
    private  void createPaneChildrenControl() {
        _stage.setOnShown(event ->notificationControl(Pos.TOP_CENTER) );
    }


    private  void notificationControl(Pos pos) {
        Notifications notificationBuilder = Notifications.create()
                .title("Record Control"  )
                .darkStyle()
                .hideCloseButton()
                .hideAfter(Duration.seconds(Double.POSITIVE_INFINITY))
                .position(pos)
                .graphic(getControl());
        notificationBuilder.show();
    }
    private  HBox getControl(){
        HBox box=new HBox(10);
        for (Node a_node : _node)
            box.getChildren().add(a_node);
        return  box;
    }
    //==================================================================================================================
}
