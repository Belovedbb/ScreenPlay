package com.shoot.player;

import com.shoot.PostHidden.Confirmation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by Beloved on 01-Mar-18.
 */
//class for audio player
public class AudioChooser extends BorderPane {

    Stage mainStage,audioStage;
    File file;
    FileChooser pickFile;
    MediaPlayer player,oldPlayer,newPlayer;
    MediaView view;
    Scene scene;

    public AudioChooser(Stage mainStage){
        this.mainStage=mainStage;
        mainStage.hide();
        launchChooser();
    }

    void launchChooser(){
        audioStage=new Stage();
        ProgressIndicator pI=new ProgressIndicator();
        pI.setMaxSize(50,50);
        audioStage.setScene(new Scene(new StackPane(pI),200,200));
        audioStage.getIcons().add(new javafx.scene.image.Image("file:resource\\a.jpg",200,200,true,true));
        audioStage.centerOnScreen();
        audioStage.show();
        pickFile=new FileChooser();
        pickFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Mp3 files(*.mp3)","*.mp3"));

        file=pickFile.showOpenDialog(audioStage);

        this.setStyle("-fx-background-color: black");
        Scene scene=new Scene(this,700,700);
        this.scene=scene;
        StackPane stackPane=new StackPane();

        if(file!=null){
            player=getPlayer(file);
            view=new MediaView(player);
            stackPane.getChildren().addAll(BasePlayFunction.play(player,view),new ImageView(new Image("file:resource\\a.jpg",200,200,true,true)));
            this.setTop(controls());
            this.setCenter(stackPane);
            audioStage.centerOnScreen();
            view.fitWidthProperty().bind(scene.widthProperty().divide(1.2));
            view.fitHeightProperty().bind(scene.heightProperty().divide(1.2));
            audioStage.setScene(scene);
            audioStage.setTitle(file.getName());
            audioStage.setMaximized(true);
            this.requestFocus();
        }

        else{
            mainStage.show();
            audioStage.close();
        }

        audioStage.setOnCloseRequest(e->{
            e.consume();
            Confirmation closeBox=new Confirmation("Close Player","Are you sure you want to close up player?",audioStage,mainStage,player);
            closeBox.confirm();
        });
    }

//menus for the player
    HBox controls(){
        HBox pane=new HBox();
        MenuBar menuBar=new MenuBar();

        Menu menuAction=new Menu("Action");
        Menu menuControl =new Menu("Desk");


        MenuItem actionOpen=new MenuItem("Open");
        MenuItem actionClose=new MenuItem("Close");
        MenuItem deskMute=new MenuItem("Mute");
        MenuItem deskUnmute=new MenuItem("Unmute");


        actionOpen.setOnAction(e->{
            file=pickFile.showOpenDialog(audioStage);
            if(file!=null) {
                oldPlayer = player;
                newPlayer = getPlayer(file);
                oldPlayer.stop();
                view = new MediaView(newPlayer);

                this.setCenter(new StackPane(BasePlayFunction.play(newPlayer, view), new ImageView(new Image("file:resource\\a.jpg",200,200,true,true))));
                view.fitWidthProperty().bind(scene.widthProperty().divide(1.2));
                view.fitHeightProperty().bind(scene.heightProperty().divide(1.2));
                audioStage.setTitle(file.getName());
                player = newPlayer;
            }else{
                player.stop();
                mainStage.show();
                audioStage.close();
            }
        });

        actionClose.setOnAction(e->{
            Confirmation closeBox=new Confirmation("Close Player","Are you sure you want to close up player?",audioStage,mainStage,player);
            closeBox.confirm();
        });

        deskMute.setOnAction(e->{
            player.setMute(true);
        });

        deskUnmute.setOnAction(e->{
            player.setMute(false);
        });

        menuAction.getItems().addAll(actionOpen,new SeparatorMenuItem(),actionClose);
        menuControl.getItems().addAll(deskMute,new SeparatorMenuItem(),deskUnmute,new SeparatorMenuItem());

        menuBar.getMenus().addAll(menuAction,menuControl);
        menuBar.prefWidthProperty().bind(scene.widthProperty());
        pane.getChildren().add(menuBar);

        return pane;
    }
    private MediaPlayer getPlayer(File file){
        Media media =new Media(file.toURI().toString());
        return new MediaPlayer(media);
    }

}