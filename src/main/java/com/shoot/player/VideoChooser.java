package com.shoot.player;

/**
 * Created by Beloved on 12-Feb-18.
 */

import com.shoot.PostHidden.Confirmation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
//class for video player
public class VideoChooser extends BorderPane{


    Stage mainStage,videoStage;
    File file;
    FileChooser pickFile;
    MediaPlayer player,oldPlayer,newPlayer;
    Scene scene;
    MediaView view;
    FileChooser.ExtensionFilter mp4Filter;
    public VideoChooser(Stage mainStage){
        this.mainStage=mainStage;
        mainStage.hide();
        launchChooser();
    }

    void launchChooser(){
        videoStage=new Stage();
        ProgressIndicator pI=new ProgressIndicator();
        pI.setMaxSize(50,50);
        videoStage.getIcons().add(new javafx.scene.image.Image("a.jpg",200,200,true,true));
        videoStage.setScene(new Scene(new StackPane(pI),200,200));
        videoStage.centerOnScreen();
        videoStage.show();
        pickFile=new FileChooser();
        mp4Filter=new FileChooser.ExtensionFilter("Mp4 files(*.mp4)","*.mp4");
        pickFile.getExtensionFilters().addAll(mp4Filter,new FileChooser.ExtensionFilter("3gp files(*.3gp)","*.3gp"));

        file=pickFile.showOpenDialog(videoStage);

        this.setStyle("-fx-background-color: black");
        Scene scene=new Scene(this,700,700);
        this.scene=scene;
        StackPane stackPane=new StackPane();

        if(file!=null){
            player=getPlayer(file);
            view=new MediaView(player);
            stackPane.getChildren().add(BasePlayFunction.play(player,view));
            this.setTop(controls());
            this.setCenter(stackPane);
            videoStage.centerOnScreen();
            view.fitWidthProperty().bind(scene.widthProperty().divide(1.2));
            view.fitHeightProperty().bind(scene.heightProperty().divide(1.2));
            videoStage.setScene(scene);
            videoStage.setTitle(file.getName());
            videoStage.setMaximized(true);
            this.requestFocus();
        }

        else{
            mainStage.show();
            videoStage.close();
        }

        videoStage.setOnCloseRequest(e->{
            e.consume();
            Confirmation closeBox=new Confirmation("Close Player","Are you sure you want to close up player?",videoStage,mainStage,player);
            closeBox.confirm();
        });
    }

//menus for player
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
            file=pickFile.showOpenDialog(videoStage);
            if(file!=null) {
                oldPlayer = player;
                newPlayer = getPlayer(file);
                oldPlayer.stop();
                view = new MediaView(newPlayer);
                this.setCenter(BasePlayFunction.play(newPlayer, view));
                view.fitWidthProperty().bind(scene.widthProperty().divide(1.2));
                view.fitHeightProperty().bind(scene.heightProperty().divide(1.2));
                videoStage.setTitle(file.getName());
                player = newPlayer;
            }else{
                player.stop();
                mainStage.show();
                videoStage.close();
            }
        });

        actionClose.setOnAction(e->{
            Confirmation closeBox=new Confirmation("Close Player","Are you sure you want to close up player?",videoStage,mainStage,player);
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

