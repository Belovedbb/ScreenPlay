package com.shoot.go;

import com.shoot.PostHidden.Confirmation;
import com.shoot.Settings.Settings;
import com.shoot.player.AudioChooser;
import com.shoot.player.VideoChooser;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.glyphfont.Glyph;

/**
 * Created by Beloved on 16-Feb-18.
 */
//----------------------------------------------------------------------------------------------------------------------
    /*
     *
     *This class is responsible for the whole sidePane Collection
     */
class SideNode extends VBox {
    private Button glyphButton;
    private HBox labelGlyphContainer;
    private Label pinLabel;
    private Stage primaryStage;
    /*
construct for locking and unlocking glyph container
*/
    SideNode(final Side side, final HiddenSidesPane pane, HBox box, Pane mainPane, Scene scene, Stage stage) {
            primaryStage=stage;
        hiddenPaneBox(this);


        box.getChildren().add(mainPane);


                labelGlyphContainer.setOnMouseClicked(event -> {
                    if (pane.getPinnedSide() != null) {
                        pinLabel.setText( " Unpinned");
                        glyphButton= new javafx.scene.control.Button("", new Glyph("FontAwesome", "UNLOCK"));
                        repaintGlyph(glyphButton);
                        pane.setPinnedSide(null);
                        box.getChildren().clear();
                        box.setPadding(new javafx.geometry.Insets(0,0,0,0));
                        box.minHeightProperty().bind(scene.heightProperty().subtract(20000));
                        box.getChildren().add(0,mainPane);
                    } else {
                        pinLabel.setText( " Pinned");
                        glyphButton= new javafx.scene.control.Button("", new Glyph("FontAwesome", "LOCK"));
                        repaintGlyph(glyphButton);
                        pane.setPinnedSide(side);
                        box.getChildren().clear();
                        box.setPadding(new javafx.geometry.Insets(0,0,0,200));
                        box.getChildren().add(0,mainPane);
                    }
                });
    }
    //
    private void hiddenPaneBox(VBox box){
        labelGlyphContainer=new HBox(70);
        pinLabel=new javafx.scene.control.Label("Unpinned");
        pinLabel.setStyle("-fx-font-size : 15pt");
        glyphButton= new javafx.scene.control.Button("", new Glyph("FontAwesome", "UNLOCK"));

        labelGlyphContainer.getChildren().addAll(pinLabel,glyphButton);
        //setAlignment(Pos.TOP_RIGHT);
        box.setPrefSize(200, 200);
        setPadding(new javafx.geometry.Insets(20,0,0,0));
        setSpacing(10);
        // box.prefHeightProperty().bind(scene.heightProperty());
        box.getChildren().addAll(labelGlyphContainer,sideContent());
    }

    /*
      Drawing glyphs on side pane
      @return vbox
     */
    private VBox sideContent(){
/*
Declaring glyphs for the toggle buttons
 */
        Glyph videoGlyph = new Glyph("FontAwesome", "FILM");
        videoGlyph.color(new javafx.scene.paint.Color( Math.random(), Math.random(), Math.random(), 1));
        videoGlyph.sizeFactor(4);
        videoGlyph.useGradientEffect();
        //videoGlyph.size(60);

        Glyph settingGlyph=new Glyph("FontAwesome", "GEAR");
        settingGlyph.color(new javafx.scene.paint.Color( Math.random(), Math.random(), Math.random(), 1));
        //settingGlyph .size(60);
        settingGlyph.sizeFactor(4);
        settingGlyph.useGradientEffect();


        Glyph audioGlyph=new Glyph("FontAwesome", "MUSIC");
        audioGlyph.color(new javafx.scene.paint.Color( Math.random(), Math.random(), Math.random(), 1));
        // deleteGlyph.size(60);
        audioGlyph.sizeFactor(4);
        audioGlyph.useGradientEffect();

        Glyph aboutGlyph=new Glyph("FontAwesome", "INFO");
        aboutGlyph.color(new javafx.scene.paint.Color( Math.random(), Math.random(), Math.random(), 1));
        aboutGlyph.sizeFactor(4);
        aboutGlyph.useGradientEffect();

    /*
    Toggle buttons
     */
        ToggleButton videoButton=new ToggleButton("",videoGlyph);
        videoButton.setOnAction(e->{
            //primaryStage.hide();
            new VideoChooser(primaryStage);

        });
        videoButton.setTooltip(new Tooltip("Video Player"));

        ToggleButton settingButton=new ToggleButton("",settingGlyph);
        settingButton.setOnAction(e-> new Settings());
        settingButton.setTooltip(new Tooltip("Settings"));

        ToggleButton audioeButton= new ToggleButton("",audioGlyph );
        audioeButton.setOnAction(e-> new AudioChooser(primaryStage));
        audioeButton.setTooltip(new Tooltip("Audio Player"));

        ToggleButton aboutButton= new ToggleButton("",aboutGlyph );
        aboutButton.setOnAction(e->{
            Confirmation confirm=new Confirmation("About Us","check belovedbb1@gmail.com");
            confirm.confirmAboutHelp();
        });
        aboutButton.setTooltip(new Tooltip("About Us"));
/*
Add to vertical pane
 */
        VBox vbox=new VBox(videoButton,settingButton,audioeButton,aboutButton);
        vbox.setPadding(new javafx.geometry.Insets(50,0,0,50));
        vbox.setSpacing(50);
        return vbox;
    }
    /*
    repaint "lock" glyph button
     */
    private void repaintGlyph(javafx.scene.control.Button newGlyphButton){
        labelGlyphContainer.getChildren().remove(1);
        labelGlyphContainer.getChildren().add(newGlyphButton);
    }

}
//------------------------------------------------------------------------------------------------------------------
