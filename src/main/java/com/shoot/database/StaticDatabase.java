package com.shoot.database;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by Beloved on 09-Mar-18.
 */
public class StaticDatabase {
    private static Stage stage,controlPlayerStage;
    private static Scene scene;
    private  static Pane pane;
    public void setStage(Stage stage){
        this.stage=stage;
    }
    public void setScene(Scene scene){
        this.scene=scene;
    }
    public void setSceneLayout(Pane pane){
        this.pane=pane;
    }
    public void setControlPlayerStage(Stage controlPlayerStage){
        this.controlPlayerStage=controlPlayerStage;
    }
    public  Stage getStage(){
        return stage;
    }
    public  Stage getControlPlayerStage(){
        return  controlPlayerStage;
    }
    public  Scene getScene(){
        return  scene;
    }
    public Pane getSceneLayout(){
        return  pane;
    }
}

