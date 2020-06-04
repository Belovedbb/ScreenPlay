package com.shoot.go;

import com.shoot.PostHidden.Confirmation;
import com.shoot.PostHidden.Parameter;
import com.shoot.PostHidden.TrayFunction;
import com.shoot.Settings.Settings;
import com.shoot.database.StartDatabaseLite;
import com.shoot.database.StaticDatabase;
import com.shoot.go.rectangle.CanvasRectangle;
import com.shoot.go.rectangle.CaptureRectangle;
import com.shoot.player.VideoChooser;
import com.shoot.recording.RecordImage;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.tools.Borders;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import static javafx.scene.layout.BackgroundSize.AUTO;

/**
 * Created by Beloved on 04-Mar-18.
 */
//The main class for screen play
public class ScreenPlay {
    private  static final double SCREEN_DEFAULT_WIDTH= Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private  static final double SCREEN_DEFAULT_HEIGHT= Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private Scene scene;
    private Stage primaryStage;
    private String filename,imageFormat,videoFormat;
    private StaticDatabase stageScene=new StaticDatabase();
//======================================================================================================================
// main method for screenplay
    //contains the content of  pane being drawn
    public void main(Stage primaryStage) throws SQLException {
        this.primaryStage=primaryStage;
        Pane stack = new Pane();
        scene=new Scene(stack,SCREEN_DEFAULT_WIDTH,SCREEN_DEFAULT_HEIGHT-50);
        stack.getChildren().add(finalPane());
        int pictureOrColor;
        pictureOrColor=new StartDatabaseLite().getUpdatedTablePictureSwitch(new StartDatabaseLite().getConnection());
        //0 for fetching color
        if(pictureOrColor==0) {
            final String statmentGet = "select color from " + StartDatabaseLite.getTableName() + " where id='2'";
            String getColor=new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(), statmentGet, 1);
            String _getColor=getColor.contains("#")?getColor:"#"+getColor;
            stack.setStyle("-fx-background-color: "+_getColor);
        }else{
            //1 for fetching picture
            Background background=   new Background(new BackgroundImage
                    (new javafx.scene.image.Image(new StartDatabaseLite().getUpdatedTablePicture(new StartDatabaseLite().getConnection())),BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,new BackgroundSize(AUTO, AUTO, true, true, true, true)));
            stack.setBackground(background);
        }
        //TreeFunction.setSetting(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.getIcons().add(new javafx.scene.image.Image("a.jpg",200,200,true,true));
        primaryStage.setTitle("Screen Play");
        primaryStage.show();
        stageScene.setSceneLayout(stack);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            Confirmation confirm=new Confirmation("Close ScreenPlay","Are you sure you want to close ScreenPlay ?",primaryStage);
            confirm.confirmClose();
        });
    }
//======================================================================================================================

    /*
      Pane thats  added to the scene contains hiddenside pane at the left hand side
      and the functional pane @method mainPane at its center
     */
    private Pane finalPane() {
        StackPane stackPane = new StackPane();
        //-box contains the functionality content drawn into the @finalpane method
        HBox box = new HBox();
        HiddenSidesPane pane = new HiddenSidesPane();

        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        pane.setContent(box);

        SideNode left= new SideNode( Side.LEFT, pane, box,mainPane(), scene,primaryStage);
        left.setStyle("-fx-background-color: rgba(0,0, 255,.25)");
        pane.setLeft(left);

        stackPane.getChildren().add(pane);
        stackPane.minWidthProperty().bind(scene.widthProperty());
        stackPane.minHeightProperty().bind(scene.heightProperty());
        return stackPane;
    }

    /*
     Set at  the center, contains
     @method labels()
     @method recentActivities()
     @method mainActivities()
     */
    private HBox mainPane(){
        VBox menuLabelPane=new VBox(10,menus());
        Node wrappedButton = Borders.wrap(labels())
                .lineBorder()
                .thickness(0.5)
                .radius(15, 15, 15,15)
                .build()
                .build();

        HBox lower=new HBox(recentActivities(),mainActivities());

        menuLabelPane.setMaxSize(SCREEN_DEFAULT_WIDTH-1,SCREEN_DEFAULT_HEIGHT/2);
        menuLabelPane.setPrefSize(SCREEN_DEFAULT_WIDTH-1,SCREEN_DEFAULT_HEIGHT/2);
        menuLabelPane.setMinSize(SCREEN_DEFAULT_WIDTH-1,SCREEN_DEFAULT_HEIGHT/2);

        menuLabelPane.getChildren().addAll(wrappedButton,lower);
        HBox bd=new HBox();
        bd.getChildren().add(menuLabelPane);

        return bd;
    }

    /*
      The box at the lower left hand side
      Rely on the database to output the default address directory and
      opacity value
     */
    private Node recentActivities(){
        String statmentGet="select opacity from "+ StartDatabaseLite.getTableName()+" where id='2'";
        double value= 0;
        try {
            value = Double.parseDouble((new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String directoryString="";
        String statmentDirectory="select file from "+ StartDatabaseLite.getTableName()+" where id='2'";
        try {
            directoryString=(new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(), statmentDirectory,1));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        VBox activity=new VBox(10);
        LinearGradient gradient1 =
                new LinearGradient(
                        0, 0,
                        0, 1,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0.2, javafx.scene.paint.Color.WHITE),
                        new Stop(0.8, javafx.scene.paint.Color.BLACK));


        Text headingActivity=new Text("Recent Activity");
        headingActivity.setFont(new javafx.scene.text.Font("Times New Roman",30));
        headingActivity.setStroke(javafx.scene.paint.Color.BLACK);
        headingActivity.setFill(gradient1);
        headingActivity.setStrokeWidth(2);

        activity.getChildren().add(headingActivity);
        activity.setAlignment(Pos.TOP_LEFT);

        Text directoryActivity=new Text("Current Directoy  : "+ directoryString+"\n");
        activity.getChildren().add(directoryActivity);

        Text opacityActivity=new Text("Current Canvas Opacity  : "+reduceDecPlaces(value)*100+"%\n");
        activity.getChildren().add(opacityActivity);

        Node wrapped = Borders.wrap(activity)
                .lineBorder()
                .thickness(0.5)
                .radius(0, 0, 0,0)
                .build()
                .build();

        StackPane pane=new StackPane(wrapped);
        pane.setMaxSize((SCREEN_DEFAULT_WIDTH/3),(SCREEN_DEFAULT_HEIGHT/2)-10);
        pane.setPrefSize(SCREEN_DEFAULT_WIDTH/3,(SCREEN_DEFAULT_HEIGHT/2)-10);
        pane.setMinSize(SCREEN_DEFAULT_WIDTH/3,(SCREEN_DEFAULT_HEIGHT/2)-10);
        pane.setAlignment(Pos.TOP_LEFT);
        return pane;
    }
 //reduce opacity value from ridiculous long length
    private  float reduceDecPlaces(double value){
        String str= Double.toString(value);
        if(str.length()<4){
            str+=00000;
        }
        return  Float.parseFloat(str.substring(0,4));
    }
    /*
      The box at the lower right hand side
      handles the functionality of selecting options
      @code screenshot
      @code screenRecord
     */
    private Node mainActivities(){
        javafx.scene.control.Label errorLabel=new javafx.scene.control.Label();
        javafx.scene.control.Label firstText=new javafx.scene.control.Label("Select Tool option (Default: Screen shot) ");
        firstText.setTextAlignment(TextAlignment.RIGHT);
        ComboBox<String> tool= new ComboBox<>();
        tool.getItems().addAll("Screen Shot","Screen Record");
        tool.getSelectionModel().select(0);
        tool.setEditable(false);

        javafx.scene.control.Label secondText=new javafx.scene.control.Label("Select Region option (Default: Full Screen) ");
        secondText.setTextAlignment(TextAlignment.RIGHT);
        ComboBox<String> region= new ComboBox<>();
        region.getItems().addAll("Full Screen","Custom Screen");
        region.getSelectionModel().select(0);
        region.setEditable(false);

        javafx.scene.control.Label thirdText=new javafx.scene.control.Label("Select Picture Format (Default: PNG) ");
        thirdText.setTextAlignment(TextAlignment.RIGHT);
        ComboBox<String> pictureFormat= new ComboBox<>();
        pictureFormat.getItems().addAll("PNG","JPG");
        pictureFormat.getSelectionModel().select(0);

        javafx.scene.control.Label fourthText=new javafx.scene.control.Label("Select Video Format (Default: MP4) ");
        fourthText.setTextAlignment(TextAlignment.RIGHT);
        ComboBox<String> videoFormat= new ComboBox<>();
        videoFormat.getItems().addAll("MP4","MOV");
        videoFormat.getSelectionModel().select(0);
        videoFormat.setEditable(false);

        javafx.scene.control.Label nameText=new javafx.scene.control.Label("File Name ");
        nameText.setTextAlignment(TextAlignment.RIGHT);

        // TextField with learning auto-complete functionality
        //Stores new word in a database
        javafx.scene.control.TextField learningTextField;

        AutoCompletionBinding<String> autoCompletionBinding;
        Set<String> possibleSuggestions = null;
        try {
            possibleSuggestions = new HashSet<>(new StartDatabaseLite().getTableAutoComplete(new StartDatabaseLite().getConnection()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        learningTextField = new javafx.scene.control.TextField();

        autoCompletionBinding = TextFields.bindAutoCompletion(learningTextField, possibleSuggestions);

        javafx.scene.control.Button continueButton=new javafx.scene.control.Button("Continue");
        Set<String> _PossibleSuggestions = possibleSuggestions;
        //--------------------------------------------------------------------------------------------------------------
        //button action
        continueButton.setOnAction(event ->{
            stageScene.setScene(scene);
            stageScene.setStage(primaryStage);
            if(learningTextField.getText().equals("")){
                errorLabel.setText("Please input a name");
            }else {
                errorLabel.setText("");
                if(pictureFormat.getValue().equals("PNG")||(pictureFormat.getValue().equals("JPG"))){
                    imageFormat=pictureFormat.getValue();
                }
                if(videoFormat.getValue().equals("MP4")||(videoFormat.getValue().equals("MOV"))){
                    this.videoFormat=videoFormat.getValue();
                }
                filename=learningTextField.getText().trim();
                autoCompletionLearnWord(learningTextField.getText().trim(), autoCompletionBinding, _PossibleSuggestions, learningTextField);
                butonAction(tool, region);
            }
        });
        //--------------------------------------------------------------------------------------------------------------
        HBox labelFieldPane=new HBox(100,firstText,new VBox(10,nameText,learningTextField));
        BorderPane border=new BorderPane();

        VBox pane1 = new VBox(10,labelFieldPane,tool,secondText,region,thirdText,pictureFormat,fourthText,videoFormat,errorLabel);
        BorderPane.setAlignment(pane1,Pos.TOP_LEFT);
        border.setTop(pane1);
        BorderPane.setAlignment(continueButton,Pos.BOTTOM_RIGHT);
        border.setBottom(continueButton);
        Node wrapped = Borders.wrap(border)
                .lineBorder()
                .thickness(0.5)
                .radius(0, 0, 0,0)
                .build()
                .build();
        StackPane pane=new StackPane(wrapped);
        pane.setMaxSize((SCREEN_DEFAULT_WIDTH/1.5),(SCREEN_DEFAULT_HEIGHT/2)-10);
        pane.setPrefSize(SCREEN_DEFAULT_WIDTH/1.5,(SCREEN_DEFAULT_HEIGHT/2)-10);
        pane.setMinSize(SCREEN_DEFAULT_WIDTH/1.5,(SCREEN_DEFAULT_HEIGHT/2)-10);
        pane.setAlignment(Pos.TOP_RIGHT);

        return pane;
    }

    /*
      For the @Button continue listener
      Defines what to do on each combo box option for tool selection
     */
    void butonAction(ComboBox<String> tool,ComboBox<String> region){
        if(tool.getValue().equals("Screen Shot")&&region.getValue().equals("Full Screen")){
            //Capture rectangle class
            primaryStage.hide();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            new CaptureRectangle(filename,imageFormat, Parameter.SCREEN_CAPTURE,scene, primaryStage);
        }else if(tool.getValue().equals("Screen Shot")&&region.getValue().equals("Custom Screen")){
            //Custom capture rectangle class
            new CanvasRectangle(filename,imageFormat,Parameter.SCREEN_CAPTURE_CUSTOM,scene, primaryStage);
        }
        else if(tool.getValue().equals("Screen Record")&&region.getValue().equals("Full Screen")){
            primaryStage.hide();
            // primaryStage.setOpacity(0.0);
            RecordImage r=new RecordImage(filename,videoFormat,Parameter.SCREEN_RECORD,primaryStage);
            r.startRecording();
            new TrayFunction(r,scene,primaryStage);
        }
        else if(tool.getValue().equals("Screen Record")&&region.getValue().equals("Custom Screen")){
            new CanvasRectangle(filename,videoFormat,Parameter.SCREEN_RECORD_CUSTOM,scene, primaryStage);
        }
        else if(tool.getValue().equals("Screen Record with Audio")&&region.getValue().equals("Full Screen")){
            primaryStage.hide();
            RecordImage r=new RecordImage(filename,videoFormat,Parameter.SCREEN_RECORD_AUDIO,primaryStage);
            r.startRecording();
            new TrayFunction(r,scene,primaryStage);
        }
        else if(tool.getValue().equals("Screen Record with Audio")&&region.getValue().equals("Custom Screen")){
            new CanvasRectangle(filename,videoFormat,Parameter.SCREEN_RECORD_AUDIO_CUSTOM,scene, primaryStage);
        }

    }

    /*
      Rely on stored words at database
      stores/learn new word
     */
    private void autoCompletionLearnWord(String newWord, AutoCompletionBinding<String> autoCompletionBinding,
                                         Set<String> possibleSuggestions, javafx.scene.control.TextField learningTextField){
        try {
           new StartDatabaseLite().setTableAutoComplete(new StartDatabaseLite().getConnection(),newWord);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        possibleSuggestions.add(newWord);

        // we dispose the old binding and recreate a new binding
        if (autoCompletionBinding != null) {
            autoCompletionBinding.dispose();
        }
        autoCompletionBinding = TextFields.bindAutoCompletion(learningTextField, possibleSuggestions);
    }


    //------------------------------------------------------------------------------------------------------------------
    // Top menus with action for individual selection
    private HBox menus(){
        javafx.scene.control.MenuBar menubar=new javafx.scene.control.MenuBar();

        javafx.scene.control.Menu file=new javafx.scene.control.Menu("File");
        javafx.scene.control.Menu tool=new javafx.scene.control.Menu("Tools");


        javafx.scene.control.MenuItem nnew=new javafx.scene.control.MenuItem("New");
        javafx.scene.control.MenuItem openFile=new javafx.scene.control.MenuItem("Open File");
        javafx.scene.control.MenuItem close=new javafx.scene.control.MenuItem("Close");
        javafx.scene.control.MenuItem settings=new javafx.scene.control.MenuItem("Setting");

        javafx.scene.control.Menu screenShot=new javafx.scene.control.Menu("ScreenShot");
        javafx.scene.control.Menu screenRecord=new javafx.scene.control.Menu("Screen Record");
        javafx.scene.control.Menu screenRecordAudio=new javafx.scene.control.Menu("Screen Record With Audio");

        javafx.scene.control.MenuItem screenShotFullShot=new javafx.scene.control.MenuItem("Full Screen");
        javafx.scene.control.MenuItem screenShotCustomShot=new javafx.scene.control.MenuItem("Custom Screen");

        javafx.scene.control.MenuItem screenRecordFullShot=new javafx.scene.control.MenuItem("Full Screen");
        javafx.scene.control.MenuItem screenRecordCustomShot=new javafx.scene.control.MenuItem("Custom Screen");

        javafx.scene.control.MenuItem screenRecordAudioFullShot=new javafx.scene.control.MenuItem("Full Screen");
        javafx.scene.control.MenuItem screenRecordAudioCustomShot=new javafx.scene.control.MenuItem("Custom Screen");

        javafx.scene.control.MenuItem video=new javafx.scene.control.MenuItem("Video Player");
        filename="Screen";
        videoFormat="MP4";
        imageFormat="JPG";
        close.setOnAction(event -> {
            Confirmation confirm =new Confirmation("Close ScreenPlay","Are you sure you want to close ScreenPlay ?",primaryStage);
            confirm.confirmClose();
        });
        video.setOnAction(event -> new VideoChooser(primaryStage));
        stageScene.setScene(scene);
        stageScene.setStage(primaryStage);
        screenShot.setOnAction(e->{
            screenShotFullShot.setOnAction(event -> {
                primaryStage.hide();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                new CaptureRectangle(filename,imageFormat,Parameter.SCREEN_CAPTURE,scene, primaryStage);
            });
            screenShotCustomShot.setOnAction(event -> {
                new CanvasRectangle(filename,imageFormat,Parameter.SCREEN_CAPTURE_CUSTOM,scene, primaryStage);
            });
        });

        screenRecord.setOnAction(e->{
            screenRecordFullShot.setOnAction(event -> {
                primaryStage.hide();
                RecordImage r=new RecordImage(filename,videoFormat,Parameter.SCREEN_RECORD,primaryStage);
                r.startRecording();
                new TrayFunction(r,scene,primaryStage);
            });
            screenRecordCustomShot.setOnAction(event -> {
                new CanvasRectangle(filename,videoFormat,Parameter.SCREEN_RECORD_CUSTOM,scene, primaryStage);
            });
        });
        screenRecordAudio.setOnAction(e->{
            screenRecordAudioFullShot.setOnAction(event -> {
                primaryStage.hide();
                RecordImage r=new RecordImage(filename,videoFormat,Parameter.SCREEN_RECORD_AUDIO,primaryStage);
                r.startRecording();
                new TrayFunction(r,scene,primaryStage);
            });
            screenRecordAudioCustomShot.setOnAction(event -> {
                new CanvasRectangle(filename,videoFormat,Parameter.SCREEN_RECORD_AUDIO_CUSTOM,scene, primaryStage);
            });
        });
        settings.setOnAction(e->{
            new Settings();
        });

        file.getItems().addAll(nnew,new SeparatorMenuItem(),settings,new SeparatorMenuItem(),close);
        screenShot.getItems().addAll(screenShotFullShot,screenShotCustomShot);
        screenRecord.getItems().addAll(screenRecordFullShot,screenRecordCustomShot);
        screenRecordAudio.getItems().addAll(screenRecordAudioFullShot,screenRecordAudioCustomShot);
        tool.getItems().addAll(screenShot,screenRecord,new SeparatorMenuItem(),video);

        menubar.getMenus().addAll(file,tool);
        menubar.minWidthProperty().bind(scene.widthProperty());
        return new HBox(menubar);
    }
//----------------------------------------------------------------------------------------------------------------------

    //labels for image  and text displayed @mainPane()
    private VBox labels(){
        LinearGradient gradient =
                new LinearGradient(
                        0, 0,
                        0, 1,
                        true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0.2, javafx.scene.paint.Color.WHITE),
                        new Stop(0.8, javafx.scene.paint.Color.AQUAMARINE));
        ImageView img= new ImageView(new javafx.scene.image.Image("a.jpg",(SCREEN_DEFAULT_WIDTH/4)-20,(SCREEN_DEFAULT_HEIGHT/4)-20,true,true));
        Text lb=new Text("Screen Player");
        lb.setFont(javafx.scene.text.Font.font("Times New Roman",30));
        lb.setStroke(javafx.scene.paint.Color.BLACK);
        lb.setFill(gradient);
        lb.setStrokeWidth(2);
        javafx.scene.control.Label label=new javafx.scene.control.Label("",img);
        VBox vb=new VBox(30,lb,label);
        vb.setAlignment(Pos.CENTER);
        return vb;
    }
}