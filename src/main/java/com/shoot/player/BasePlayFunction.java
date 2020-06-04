package com.shoot.player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.glyphfont.Glyph;

/**
 * Created by Beloved on 02-Mar-18.
 */
//class utilized  by audio and video player
public class BasePlayFunction {
    static int i,j=0;
    static Duration duration;

    static   BorderPane play(MediaPlayer player,MediaView view){
        BorderPane paneMain=new BorderPane();

        // MediaView view=new MediaView(player);

        Glyph repeatGlyph = new Glyph("FontAwesome", "REPEAT");
        repeatGlyph.color(new Color( Math.random(), Math.random(), Math.random(), 1));
        repeatGlyph.sizeFactor(2);
        repeatGlyph.useGradientEffect();

        Glyph noRepeatGlyph = new Glyph("FontAwesome", "RANDOM");
        noRepeatGlyph.color(new Color( Math.random(), Math.random(), Math.random(), 1));
        noRepeatGlyph.sizeFactor(2);
        noRepeatGlyph.useGradientEffect();

        Button buttonCycle=new Button();
        buttonCycle.setGraphic(repeatGlyph);
        buttonCycle.setOnAction(e->{
            j++;

            if(j%2!=0){
                buttonCycle.setGraphic(noRepeatGlyph);
                player.setCycleCount(MediaPlayer.INDEFINITE);
            }
            else{
                buttonCycle.setGraphic(repeatGlyph);
                player.setCycleCount(0);
            }
        });

        Glyph playGlyph = new Glyph("FontAwesome", "PLAY");
        playGlyph.color(new Color( Math.random(), Math.random(), Math.random(), 1));
        playGlyph.sizeFactor(2);
        playGlyph.useGradientEffect();

        Glyph pauseGlyph = new Glyph("FontAwesome", "PAUSE");
        pauseGlyph.color(new Color( Math.random(), Math.random(), Math.random(), 1));
        pauseGlyph.sizeFactor(2);
        pauseGlyph.useGradientEffect();

        Button button=new Button();
        button.setGraphic(playGlyph);
        button.setOnAction(e->{

            if(button.getGraphic()==playGlyph){
                button.setGraphic(pauseGlyph);
                player.play();
            }
            else{
                button.setGraphic(playGlyph);
                player.pause();

            }
        });
        paneMain.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.SPACE&&e.getTarget()==button)	{
                i++;
                if(i%2!=0){
                    button.setGraphic(pauseGlyph);
                    player.play();
                }
                else{
                    button.setGraphic(playGlyph);
                    player.pause();
                }
            }
        });


        Slider  volumeSlider=new Slider();
        volumeSlider.setMinWidth(100);
        volumeSlider.setTooltip(new Tooltip("Volume"));
        volumeSlider.setValue(50);
        player.setOnReady(() -> duration = player.getMedia().getDuration());
        player.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

        HBox paneSlider=new HBox();
        paneSlider.getChildren().add(volumeSlider);

        Slider  mainSlider=new Slider();
        mainSlider.setMinWidth(700);
        mainSlider.setTooltip(new Tooltip("Seeker"));

        player.setOnPlaying(()->playingListeners(mainSlider,player,duration));

        paneSlider.getChildren().add(mainSlider);
        paneSlider.setPadding(new Insets(10,0,0,0));
        HBox paneH=new HBox(button,paneSlider,buttonCycle);
        paneH.setAlignment(Pos.TOP_CENTER);
        //paneH.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        paneH.prefHeight(Double.MAX_VALUE);
        //paneH.setFillHeight(true);
        paneMain.setCenter(view);
        paneMain.setBottom(paneH);
        paneMain.setPadding(new Insets(0,0,10,0));


        return paneMain;
    }
    @SuppressWarnings("deprecated")
    private static void playingListeners(Slider mainSlider, MediaPlayer player, Duration duration){
        mainSlider.setOnMousePressed(e->{
            player.seek(duration.multiply(mainSlider.getValue()/100));
        });
        mainSlider.setOnMouseDragged(e->{
            player.seek(duration.multiply(mainSlider.getValue()/100));
        });
        player.currentTimeProperty().addListener(e->{
            mainSlider.setValue(player.getCurrentTime().divide(duration).toMillis()*100);
        });
    }
}
