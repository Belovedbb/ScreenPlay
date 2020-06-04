package com.shoot.PostHidden;

/**
 * Created by Beloved on 12-Feb-18.
 */

import com.shoot.database.StartDatabaseLite;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
//class for screen play confirmation
public class Confirmation {
    private Stage stage;
    private String Title,Message;
    private Button buttonAccept,buttonDecline,buttonOk;
    private Label label;
    private Stage appStage,mainStage;
    private MediaPlayer player;
//======================================================================================================================
    //confirmation alert for player @AudiChooser && @VideoChooser
    public Confirmation(String Title, String Message, Stage appStage, Stage mainStage, MediaPlayer player){
        this.Title=Title;
        this.Message=Message;
        this.player=player;
        this.appStage=appStage;
        this.mainStage=mainStage;
    }
    public void confirm(){
        stage= new Stage();
        stage.setTitle(Title);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.toFront();
        label=new Label(Message);
        buttonAccept=new Button("YES");
        buttonAccept.setOnAction(e -> {
                    stage.close();
                    try{
                        player.stop();
                        appStage.close();
                        mainStage.show();
                    }catch(NullPointerException ee){}
                }
        );
        buttonDecline=new Button("NO");
        buttonDecline.setOnAction(e -> stage.close()
        );
        HBox paneBtn = new HBox(20);
        paneBtn.getChildren().addAll(buttonAccept, buttonDecline);
        BorderPane pane = new BorderPane();
        pane.setCenter(label);
        pane.setBottom(paneBtn);
        paneBtn.setAlignment(Pos.TOP_CENTER);
        // pane.setStyle("-fx-background-image:url(file:earth.jpg)");
        StackPane stackPane=new StackPane(pane);
        stackPane.setPadding(new Insets(0,0,10,0));
        Scene scene=new Scene(stackPane,500,200);
        stage.setScene(scene);
        stage.getIcons().add(new Image("a.jpg"));
        stage.show();
        scene.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.ENTER&&e.getTarget()==buttonAccept)	{
                stage.close();
                try{
                    player.stop();
                    appStage.close();
                    mainStage.show();
                }catch(NullPointerException ee){}
            }else if(e.getCode()== KeyCode.ENTER&&e.getTarget()==buttonDecline){
                stage.close();
            }
        });
        stage.requestFocus();
    }
    //==================================================================================================================
    //Confirmation for exiting screen play
    public Confirmation(String Title, String Message, Stage mainStage){
        this.Title=Title;
        this.Message=Message;
        this.mainStage=mainStage;
    }
    public void confirmClose(){

        stage= new Stage();
        stage.setTitle(Title);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.toFront();
        label=new Label(Message);
        buttonAccept=new Button("YES");
        buttonAccept.setOnAction(e -> {
                    stage.close();
                    try {
                        new StartDatabaseLite().closeConnection();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    mainStage.close();
                    System.exit(0);
                }
        );
        buttonDecline=new Button("NO");
        buttonDecline.setOnAction(e -> {
                    stage.close();
                }
        );
        HBox paneBtn = new HBox(20);
        paneBtn.getChildren().addAll(buttonAccept, buttonDecline);
        BorderPane pane = new BorderPane();
        pane.setCenter(label);
        pane.setBottom(paneBtn);
        paneBtn.setAlignment(Pos.TOP_CENTER);
        // pane.setStyle("-fx-background-image:url(file:earth.jpg)");
        StackPane stackPane=new StackPane(pane);
        stackPane.setPadding(new Insets(0,0,10,0));
        Scene scene=new Scene(stackPane,500,200);
        stage.setScene(scene);
        stage.getIcons().add(new Image("a.jpg"));
        stage.show();
        scene.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.ENTER&&e.getTarget()==buttonAccept)	{

                stage.close();
                try {
                    new StartDatabaseLite().closeConnection();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                mainStage.close();
                System.exit(0);
            }else if(e.getCode()== KeyCode.ENTER&&e.getTarget()==buttonDecline){
                stage.close();
            }
        });
        stage.requestFocus();
    }
    //==================================================================================================================
    //confirmation for "help:@About us"
    public Confirmation(String Title, String Message){
        this.Title=Title;
        this.Message=Message;
    }
    public void confirmAboutHelp(){
        stage= new Stage();
        stage.setTitle(Title);

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.toFront();
        label=new Label(Message);

        buttonOk=new Button("OK");
        buttonOk.setOnAction(e -> stage.close()
        );

        HBox paneBtn = new HBox(20);
        paneBtn.getChildren().addAll(buttonOk);
        BorderPane pane = new BorderPane();
        pane.setTop(label);
        pane.setBottom(paneBtn);
        paneBtn.setAlignment(Pos.TOP_CENTER);
        // pane.setStyle("-fx-background-image:url(file:earth.jpg)");
        StackPane stackPane=new StackPane(pane);
        stackPane.setPadding(new Insets(0,0,10,5));
        Scene scene=new Scene(stackPane,500,200);
        stage.setScene(scene);
        stage.getIcons().add(new Image("a.jpg"));
        stage.show();
        scene.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.ENTER)	{
                stage.close();
            }
        });
        stage.requestFocus();
    }
//======================================================================================================================
}

