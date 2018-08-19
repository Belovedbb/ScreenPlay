package com.shoot.go.rectangle;

import com.shoot.PostHidden.Parameter;
import com.shoot.PostHidden.TrayFunction;
import com.shoot.Settings.Settings;
import com.shoot.database.StartDatabaseLite;
import com.shoot.player.VideoChooser;
import com.shoot.recording.RecordImage;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Beloved on 18-Jan-18.
 */
//This class is for full screenSize capturing the image and saving for full screen only
public class CaptureRectangle{
    int newRectWidth,newRectHeight;
    Stage primaryStage;
    Scene scene,primaryScene;
    BufferedImage screen;
    Robot robot;
    Rectangle rect;
    String fileName,format;
    Parameter choice;
    static final double SCREEN_DEFAULT_WIDTH;
    static final double SCREEN_DEFAULT_HEIGHT;

    static {
        SCREEN_DEFAULT_WIDTH= Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        SCREEN_DEFAULT_HEIGHT= Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }
    //==================================================================================================================
//Constructor for image capture
    public CaptureRectangle(String fileName,String format,Parameter choice,Scene primaryScene,Stage primaryStage){
        this.format=format;
        this.fileName=fileName;
        this.choice=choice;
        this.primaryScene=primaryScene;
        this.primaryStage=primaryStage;
        //initialize capture
        try {
            robot = new Robot();

        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        //Capture method
        setCapture();
    }
    //image capture
    private void setCapture()  {
//Embedding swing in javafx
        SwingNode swingNode=new SwingNode();
        //Main method that out image on javafx scene
        setImage(swingNode);
        BorderPane pane=new BorderPane();
        pane.setTop(menus());
        ScrollPane scrollPane=new ScrollPane(swingNode);
        Label preview=new Label("Preview Image");
        Label lb=new Label("",scrollPane);
        lb.setPadding(new Insets(0,10,0,0));
        pane.setCenter(new VBox(10,preview,lb));
        Button butCancel=new Button("Cancel");
        butCancel.setOnAction(event -> {
            primaryStage.setScene(primaryScene);
            primaryStage.centerOnScreen();
            primaryStage.setMaximized(true);
            primaryStage.show();
        });

        pane.setBottom(new HBox(10,buttonSave(),butCancel));
        scene=new Scene(pane);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        //Ensure it is not transparent
        primaryStage.setOpacity(1);
        primaryStage.show();
    }
    private void setImage(SwingNode e){
        //Using swing thread, capture image on JLabel into javafx scene
        SwingUtilities.invokeLater(() ->
                e.setContent(new JLabel(new ImageIcon(capture()))));
    }
    protected   BufferedImage capture(){
        System.out.print("My canvas rectangle : "+getRectangle());
        screen =robot.createScreenCapture(getRectangle());
        return  screen;
    }
    //------------------------------------------------------------------------------------------------------------------
    //=================================================================================================================
    //Constructor for recording
    CaptureRectangle(String fileName,String format,Rectangle rect,Parameter choice,Scene primaryScene ,Stage primaryStage){
        this.primaryScene=primaryScene;
        this.format=format;
        this.fileName=fileName;
        this.choice=choice;
        this.rect=rect;
        this.primaryStage=primaryStage;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        //capture image from @CanvasRectangle.java
        if((choice==Parameter.SCREEN_CAPTURE)|| (choice==Parameter.SCREEN_CAPTURE_CUSTOM)){
            try {
                robot = new Robot();

            } catch (AWTException e1) {
                e1.printStackTrace();
            }
            setCapture();
        }
        //Image recording
        else{
            RecordImage r=new RecordImage(fileName,format,choice,primaryStage);
            r.setCanvasRectangle(getRectangle());
            r.startRecording();
            new TrayFunction(r,scene,primaryStage);
        }
    }
    public CaptureRectangle(Parameter choice){
        this.choice=choice;
        try {
            robot = new Robot();

        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }



    //Method to get screen dimension
    public final Rectangle getRectangle(){
        Rectangle rect;
        if ((choice==Parameter.SCREEN_CAPTURE)||(choice==Parameter.SCREEN_RECORD)||(choice==Parameter.SCREEN_RECORD_AUDIO) ){
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            rect=new Rectangle(screenSize);

            return   rect;
        }
        //Ensure its from the canvas scene
        else if ((choice==Parameter.SCREEN_CAPTURE_CUSTOM)||(choice==Parameter.SCREEN_RECORD_CUSTOM)||(choice==Parameter.SCREEN_RECORD_AUDIO_CUSTOM)) {
            //Ensure the rectangle gotten is even
            //since the encoder @Mp4encode.java cant capture odd bounds
            if ((this.rect.getWidth() % 2 == 0) && (this.rect.getHeight() % 2 == 0))
                return this.rect;
            else{
                newRectWidth =(int) this.rect.getWidth();
                newRectHeight = (int) this.rect.getHeight();

                if (this.rect.getWidth() % 2 != 0) {
                    System.out.println("Rectangle : b" + this.rect);
                    newRectWidth = (int) this.rect.getWidth() - 1;
                }
                if (this.rect.getHeight() % 2 != 0) {
                    System.out.println("Rectangle : c " + this.rect);
                    newRectHeight = (int) this.rect.getHeight() - 1;
                }
                return new Rectangle(this.rect.x, this.rect.y, newRectWidth, newRectHeight);
            }

        }
        return  null;
    }


    //Menus
    private HBox menus(){
        // menubar.setStyle("-fx-background-color: brown");
        MenuBar menubar=new MenuBar();

        Menu file=new Menu("File");
        Menu  tool=new Menu("Tools");
        Menu  navigate=new Menu("Navigate");
        MenuItem settings=new MenuItem("Setting");

        MenuItem nnew=new MenuItem("New");
        MenuItem openFile=new MenuItem("Open File");
        MenuItem close=new MenuItem("Close");

        MenuItem homePage=new MenuItem("Home");

        Menu screenShot=new Menu("ScreenShot");
        Menu screenRecord=new Menu("Screen Record");
        Menu screenRecordAudio=new Menu("Screen Record With Audio");

        MenuItem screenShotFullShot=new MenuItem("Full Screen");
        MenuItem screenShotCustomShot=new MenuItem("Custom Screen");

        MenuItem screenRecordFullShot=new MenuItem("Full Screen");
        MenuItem screenRecordCustomShot=new MenuItem("Custom Screen");

        MenuItem screenRecordAudioFullShot=new MenuItem("Full Screen");
        MenuItem screenRecordAudioCustomShot=new MenuItem("Custom Screen");

        MenuItem video=new MenuItem("Video Player");
        video.setOnAction(event -> new VideoChooser(primaryStage));

        homePage.setOnAction(event ->{
            primaryStage.setScene(primaryScene);
            primaryStage.centerOnScreen();
            primaryStage.setMaximized(true);
        });
        if(fileName==null)
            fileName="Screen1";

        screenShot.setOnAction(e->{
            if(format==null)
                format="JPG";
            screenShotFullShot.setOnAction(event -> {
                primaryStage.hide();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                new CaptureRectangle(fileName,format,Parameter.SCREEN_CAPTURE,scene, primaryStage);
            });
            screenShotCustomShot.setOnAction(event -> {
                new CanvasRectangle(fileName,format,Parameter.SCREEN_CAPTURE_CUSTOM,scene, primaryStage);
            });
        });

        screenRecord.setOnAction(e->{
            if(format==null)
                format="MP4";
            screenRecordFullShot.setOnAction(event -> {
                primaryStage.hide();
                RecordImage r=new RecordImage(fileName,format,Parameter.SCREEN_RECORD,primaryStage);
                r.startRecording();
                new TrayFunction(r,scene,primaryStage);
            });
            screenRecordCustomShot.setOnAction(event -> {
                new CanvasRectangle(fileName,format,Parameter.SCREEN_RECORD_CUSTOM,scene, primaryStage);
            });
        });
        screenRecordAudio.setOnAction(e->{
            if(format==null)
                format="MP4";
            screenRecordAudioFullShot.setOnAction(event -> {
                primaryStage.hide();
                RecordImage r=new RecordImage(fileName,format,Parameter.SCREEN_RECORD_AUDIO,primaryStage);
                r.startRecording();
                new TrayFunction(r,scene,primaryStage);
            });
            screenRecordAudioCustomShot.setOnAction(event -> {
                new CanvasRectangle(fileName,format,Parameter.SCREEN_RECORD_AUDIO_CUSTOM,scene, primaryStage);
            });
        });
        settings.setOnAction(event -> new Settings());
        file.getItems().addAll(nnew,new SeparatorMenuItem(),openFile,new SeparatorMenuItem(),close);
        screenShot.getItems().addAll(screenShotFullShot,screenShotCustomShot);
        screenRecord.getItems().addAll(screenRecordFullShot,screenRecordCustomShot);
        screenRecordAudio.getItems().addAll(screenRecordAudioFullShot,screenRecordAudioCustomShot);
        tool.getItems().addAll(screenShot,screenRecord,new SeparatorMenuItem(),video,new SeparatorMenuItem(),settings);
        navigate.getItems().add(homePage);
        menubar.getMenus().addAll(file,tool,navigate);
        menubar.minWidthProperty().bind(primaryStage.widthProperty());
        return new HBox(menubar);
    }
    //==================================================================================================================
    //Saving image capture
    String getDatabaseAddress;
    private Node buttonSave(){

        String statmentGet="select file from "+ StartDatabaseLite.getTableName()+" where id='2'";
        try {
            getDatabaseAddress= new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(getDatabaseAddress.equals(null)){
            getDatabaseAddress=System.getProperty("user.home");
        }
        Button button=new  Button("Save");
        button.setOnAction(e->{
            try {
                saveCapture(new FileOutputStream(getDatabaseAddress+"\\"+fileName+"."+format.toLowerCase()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });
        return button;
    }

    protected void saveCapture(FileOutputStream stream) throws IOException {
        ImageIO.write(screen, format.toLowerCase(), stream);
        stream.close();

    }
//======================================================================================================================
}

