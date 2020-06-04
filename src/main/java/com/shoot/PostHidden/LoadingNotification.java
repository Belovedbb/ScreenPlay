package com.shoot.PostHidden;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Beloved on 28-Feb-18.
 */
//class for progressBar and progressIndicator used by @StartDatabaseLite.java && @Encode.java
public class LoadingNotification {
     Task task;
    ProgressIndicator progressIndicator;
    ProgressBar progressBar;
    private static int count;
    private static int currentCount;
    private  int limit;
    private  String message;
    private  Stage stage;
    private Parameter param;

    public LoadingNotification (String message, int limit, Control node){
        this.message=message;
        this.limit=limit;

        //PI-progressIndicator,bar-progressbar
        //since bar is a child of PI, make sure its "if" condition is loaded first
        //to avoid object of bar to be replaced by PI
        if(node instanceof ProgressBar) {
            progressBar = (ProgressBar) node;
            param=Parameter.BAR;
        }
        else  if(node instanceof ProgressIndicator) {
            progressIndicator = (ProgressIndicator) node;
            param=Parameter.INDICATOR;
        }

    }

    public  void start(Stage primaryStage) throws Exception {
        if( param==Parameter.INDICATOR)
            startIndicator(primaryStage,progressIndicator);
        else if(param==Parameter.BAR){
            startBar(primaryStage,progressBar);
        }
    }
    private  void startIndicator(Stage primaryStage,ProgressIndicator  pI) throws Exception {
        System.out.println("Ind");
        stage = primaryStage;
        pI.setProgress(-1);
        pI.setMaxSize(50,50);
        pI.setMinSize(50,50);
        primaryStage.setOnShown(event -> {
            //Timing to give the program time to display the task
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task = createTask();
            pI.progressProperty().unbind();
            pI.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        });
        VBox box = new VBox(pI, new Label(message));
        box.setAlignment(Pos.CENTER);
        startStage(primaryStage, box,null);
    }

    private  void startBar(Stage primaryStage,ProgressBar  pI) throws Exception {
        System.out.println("Bar");
        stage = primaryStage;
        pI.setProgress(-1);
        pI.setMinWidth(400);
        pI.setMaxHeight(10);
        pI.setMinHeight(10);
        primaryStage.setOnShowing(event -> {
            task = createTask();
            pI.progressProperty().unbind();
            pI.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        });
        VBox box = new VBox(pI, new Label(message));
        box.setAlignment(Pos.CENTER);
        ImageView image=new ImageView(new javafx.scene.image.Image("a.jpg",500,400,true,true));
        StackPane sp=new StackPane(image,box);
        Scene scene= new Scene(sp, 400, 300);
        startStage(primaryStage,new HBox(), scene);
    }


     private Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                while (getCount()<=limit) {
                    while(currentCount==getCount())
                        Thread.sleep(500);
                    updateProgress(getCount(), limit);
                    currentCount=getCount();
                }
                return true;
            }
        };
    }

    public void setCount(int count){
        LoadingNotification.count =count;
    }

    private  int getCount(){
        return  count;
    }
    public void closeStage(){
        stage.close();
    }
    //load up the scene and display
   private void startStage(Stage primaryStage, Node node, Scene scene) {
        StackPane sp=new StackPane(new ImageView(new javafx.scene.image.Image("a.jpg",400,400,true,true)),node);
        Scene _scene = scene == null ?  new Scene(sp, 200, 200):scene;
        _scene.setCursor(Cursor.WAIT);
        sp.setAlignment(Pos.CENTER);
        primaryStage.setScene(_scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.centerOnScreen();
        primaryStage.toFront();
        primaryStage.getIcons().add(new Image("a.jpg",200,200,true,true));
        primaryStage.show();
    }
}
