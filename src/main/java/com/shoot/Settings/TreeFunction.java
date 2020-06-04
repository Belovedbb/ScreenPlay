package com.shoot.Settings;


import com.shoot.database.StartDatabaseLite;
import com.shoot.database.StaticDatabase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.tools.Borders;

import java.io.File;
import java.sql.SQLException;

import static javafx.scene.layout.BackgroundSize.AUTO;

/**
 * Created by Beloved on 24-Jan-18.
 */
public class TreeFunction extends Pane {
    private static Scene primaryScene,settingScene;
    private static Stage primaryStage,settingStage;
    static String pictureAddress="a.jpg";
    final static Button apply;
    final static Button finish;
    final  static Button cancel;
    static{
        apply=new Button("Apply");
        finish=new Button("Finish");
        cancel=new Button("Cancel");
    }
    //==================================================================================================================
    /*
    get methods utilized by @Settings.java
     */
      Node theme(){
        return getTheme();
    }
    protected  Node  color(){
        return getColor();
    }
       Node fileDirectory(){
        return getFileDirectory();
    }
        Node deleteFile(){
        return getDeleteFile();
    }
     Node directory(){
        return getSelectDirectory();
    }
       Node appearance(){
        return getAppearance();
    }
     Node  canvasOpacity(){
        return getCanvasOpacity();
    }
    Node  opLev(){
        return getOpLev();
    }
    //------------------------------------------------------------------------------------------------------------------
    //methods involving appearance
    private  Node getAppearance(){
        VBox box=new VBox(20);
        Hyperlink linkTheme=new Hyperlink("Theme");
        linkTheme.setOnAction(event -> Settings.tree_SelectionChanged(new TreeItem<>("Theme")));
        Hyperlink linkColor=new Hyperlink("Color");
        linkColor.setOnAction(event -> Settings.tree_SelectionChanged(new TreeItem<>("Color")));
        box.getChildren().addAll(linkTheme,linkColor);
        box.setPadding(new Insets(10,0,0,10));
        return box;
    }

    private  Node getTheme(){
        BorderPane box=new BorderPane();
        Label label=new Label("Click the button to choose background picture");
        Button button=new Button("Directory");
        button.setOnAction(event ->{
            pictureAddress= "file:"+ pictureChooser();
        });
        VBox vbox=new VBox(10,label,button);
        BorderPane.setAlignment(vbox, Pos.TOP_LEFT);
        box.setTop(vbox);
        box.setBottom(bottomButton());
        box.setPadding(new Insets(10,10,10,10));
        apply.setOnAction(event ->{
            try {
              new  StartDatabaseLite().setUpdatedTablePictureSwitch(new StartDatabaseLite().getConnection(),1);
               new  StartDatabaseLite().setUpdatedTablePicture(new StartDatabaseLite().getConnection(),pictureAddress);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } );
        finish.setOnAction(event -> {
            Background background=   new Background(new BackgroundImage(new Image(pictureAddress),BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT,new BackgroundSize(AUTO, AUTO, true, true, true, true)));

            new  StaticDatabase().getSceneLayout().setBackground(background);

        });
        return box;
    }
    private  Node getColor(){
        BorderPane box=new BorderPane();
        ColorPicker pick=new ColorPicker();
        pick.getStyleClass().add("split-button");
        BorderPane.setAlignment(pick, Pos.TOP_LEFT);
        box.setTop(pick);
        pick.setOnAction((ee)->{
                    apply.setOnAction(event -> {
                        try {
                           new  StartDatabaseLite().setUpdatedTablePictureSwitch(new StartDatabaseLite().getConnection(),0);
                            String statementSet="update "+StartDatabaseLite.getTableName()+" set color= \""+colorToHex(pick.getValue())+"\" where id='2'";
                          new   StartDatabaseLite().setUpdateCustomRow(new StartDatabaseLite().getConnection(), statementSet);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    finish.setOnAction(event -> {
                        String statmentGet="select color from "+StartDatabaseLite.getTableName()+" where id='2'";
                        try {
                            primaryScene.setFill(Color.valueOf(new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1)));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
        );


        box.setBottom(bottomButton());
        box.setPadding(new Insets(10,10,10,10));
        return box;
    }
    //choose image and return the address path
    String pictureChooser(){

        FileChooser pickFile =new FileChooser();
        File select;
        pickFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg files(*.jpg)","*.jpg"),
                new FileChooser.ExtensionFilter("png files(*.png)","*.png"));
        select=pickFile.showOpenDialog(Settings.getSettingStage());
        if(select==null){
            return "a.jpg";
        }
        return select.getAbsolutePath();
    }
//convert color to hexadecimal string for text storage into database
    private   String colorToHex(Color color){
        String hex1;
        String hex2;
        hex1=Integer.toHexString(color.hashCode()).toUpperCase();
        switch (hex1.length()){
            case 2:
                hex2="000000";
                break;
            case 3:
                hex2 = String.format("00000%s", hex1.substring(0,1));
                break;
            case 4:
                hex2 = String.format("0000%s", hex1.substring(0,2));
                break;
            case 5:
                hex2 = String.format("000%s", hex1.substring(0,3));
                break;
            case 6:
                hex2 = String.format("00%s", hex1.substring(0,4));
                break;
            case 7:
                hex2 = String.format("0%s", hex1.substring(0,5));
                break;
            default:
                hex2 = hex1.substring(0, 6);
        }
        return hex2;
    }
    //------------------------------------------------------------------------------------------------------------------
    //method involving files and directory
    private  Node getFileDirectory(){
        VBox box=new VBox(20);
        Hyperlink linkDelete=new Hyperlink("Delete File");
        linkDelete.setOnAction(event -> Settings.tree_SelectionChanged(new TreeItem<>("Delete File")));
        Hyperlink linkSelect=new Hyperlink("Select Directory");
        linkSelect.setOnAction(event -> Settings.tree_SelectionChanged(new TreeItem<>("Select Directory")));
        box.getChildren().addAll(linkDelete,linkSelect);
        box.setPadding(new Insets(10,0,0,10));
        return box;
    }
    static Label _directoryLabelText,_labelText,_labelError;
    static TextField _field;

    private  Node getDeleteFile(){
        BorderPane box=new BorderPane();
        Label label=new Label("Click the button to choose File to delete");
        Button button=new Button("File");
        button.setOnAction(event -> fileChooser());
        _field=new TextField();
        _field.setPromptText("Enter file directory address manually");
        _labelText=new Label();
        _labelError=new Label();
        _directoryLabelText=new Label();
        _labelText.textProperty().bind(_field.textProperty());
        VBox buttonText=new VBox(10,label,_field,button,_labelText,_directoryLabelText,_labelError);
        BorderPane.setAlignment(buttonText, Pos.TOP_LEFT);
        box.setTop(buttonText);
        apply.setOnAction(event -> {
            if(validateFile(_labelText.getText())==true){
                _labelError.setText("");
                if (deleteFile(_labelText.getText())==true){
                    _labelError.setText("");
                    _labelError.setText("File deleted successfully");
                }else{
                    _labelError.setText("File Not Deleted!");
                }
            }else{
                _labelError.setText("File Error!");
            }
        });
        finish.setOnAction(event -> {

        });
        box.setBottom(bottomButton());
        box.setPadding(new Insets(10,10,10,10));
        return box;
    }
    static Label directoryLabelText,labelText,labelError;
    static TextField field;
    private   Node getSelectDirectory(){
        BorderPane box=new BorderPane();
        Label label=new Label("Click the button to choose directory");
        Button button=new Button("Directory");
        button.setOnAction(event -> launchChooser());
        field=new TextField();
        field.setPromptText("Enter directory address manually");
        labelText=new Label();
        labelError=new Label();
        directoryLabelText=new Label();
        labelText.textProperty().bind(field.textProperty());
        VBox buttonText=new VBox(10,label,field,button,labelText,directoryLabelText,labelError);
        BorderPane.setAlignment(buttonText, Pos.TOP_LEFT);
        box.setTop(buttonText);
        apply.setOnAction(event -> {
            if(validateDirectory(labelText.getText())==true){
                System.out.print("Corr");
                labelError.setText("");
                try {
                    String labelTextResult=labelText.getText().replaceAll((char) 92+ ""+(char) 92,(char)47 +"");
                    String statementSet="update "+ StartDatabaseLite.getTableName()+" set file= \""+labelTextResult+"\" where id='2'";
                   new StartDatabaseLite().setUpdateCustomRow(new StartDatabaseLite().getConnection(), statementSet);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.print("Nahh");
                labelError.setText("Directory does not exist!");
                try {
                    String homeDirectory=System.getProperty("user.home").replaceAll((char) 92+ ""+(char) 92,(char)47 +"");
                    String statementSet="update "+StartDatabaseLite.getTableName()+" set file= \""+homeDirectory+"\" where id='2'";
                   new StartDatabaseLite().setUpdateCustomRow(new StartDatabaseLite().getConnection(), statementSet);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        finish.setOnAction(event -> {

        });
        box.setBottom(bottomButton());
        box.setPadding(new Insets(10,10,10,10));
        return box;
    }
    //choose file and set it to label
     void fileChooser(){

        FileChooser pickFile =new FileChooser();
        File select;
        FileChooser.ExtensionFilter mp4Filter;
        mp4Filter=new FileChooser.ExtensionFilter("Mp4 files(*.mp4)","*.mp4");
        pickFile.getExtensionFilters().addAll(mp4Filter,new FileChooser.ExtensionFilter("3gp files(*.3gp)","*.3gp"),
                new FileChooser.ExtensionFilter("Mp3 files(*.mp3)","*.mp3"),new FileChooser.ExtensionFilter("Mov files(*.mov)","*.mov"));

        select=pickFile.showOpenDialog(Settings.getSettingStage());

        _field.setDisable(true);
        _labelText.textProperty().unbind();
        _labelText.setText("");
        _labelText.setText(select.getAbsolutePath());
    }
    //chooser for directory and set to label text
     private void launchChooser(){
        DirectoryChooser  chooseDirectory =new DirectoryChooser();
        File select=chooseDirectory.showDialog(Settings.getSettingStage());
        field.setDisable(true);
        labelText.textProperty().unbind();
        labelText.setText("");
        labelText.setText(select.getAbsolutePath());
    }
    private    boolean deleteFile(String fileName){
        File file = new File(new File(fileName).getAbsolutePath());
        return file.delete();
    }
    private  boolean validateFile(String fileName) {
        File file = new File(new File(fileName).getAbsolutePath());
        return file.isFile();
    }
    private  boolean validateDirectory(String fileName) {
        File file = new File(new File(fileName).getAbsolutePath());
        return file.isDirectory();
    }
    //------------------------------------------------------------------------------------------------------------------
    //method involving canvas opacity
    private   Node getCanvasOpacity(){
        VBox box=new VBox(20);
        Hyperlink linkTheme=new Hyperlink("Opacity Level");
        linkTheme.setOnAction(event -> Settings.tree_SelectionChanged(new TreeItem<>("Opacity Level")));
        box.getChildren().addAll(linkTheme);
        box.setPadding(new Insets(10,0,0,10));
        return box;
    }
    private    Node getOpLev(){
        BorderPane box=new BorderPane();
        // Button button=new Button();
        BorderPane.setAlignment(opacityView(), Pos.TOP_LEFT);
        box.setTop(opacityView());
        box.setBottom(bottomButton());
        box.setPadding(new Insets(10,10,10,10));
        return box;
    }
    private  VBox opacityView(){
        VBox vbox=new VBox(10);
        //Button button = new Button("Hello World!");
        Node wrappedButton = Borders.wrap(new ImageView(new Image("a.jpg",200,200,true,true)))
                .lineBorder()
                .title("Opacity")
                .thickness(1)
                .radius(0, 5, 5, 0)
                .build()
                .emptyBorder()
                .padding(20)
                .build()
                .etchedBorder()
                .title("View")
                .build()
                .emptyBorder()
                .padding(20)
                .build()
                .build();
        vbox.getChildren().add(wrappedButton);
        vbox.getChildren().add(new Label("Opacity Control"));
        Slider slider=new Slider();
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        //set the value from the database
        try {
            final  String statmentGet="select opacity from "+ StartDatabaseLite.getTableName()+" where id='2'";
            double value=Double.parseDouble((new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1)));
            slider.setValue((value*100));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        vbox.getChildren().add(slider);
        wrappedButton.opacityProperty().bind(slider.valueProperty().divide(100));
        apply.setOnAction(event -> {
            try {
                String statementSet="update "+StartDatabaseLite.getTableName()+" set opacity= '"+(slider.getValue()/100)+"' where id='2'";
               new StartDatabaseLite().setUpdateCustomRow(new StartDatabaseLite().getConnection(), statementSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        finish.setOnAction(event -> {

        });
        return  vbox;
    }
    //------------------------------------------------------------------------------------------------------------------
    //method for button used by all settings node to display button -apply,-cancel and -finish
    private static HBox bottomButton(){
        HBox hbox =new HBox(20);

        cancel.setOnAction(event ->settingStage.close() );

        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().addAll(apply, finish,cancel);
        return hbox;
    }

    static boolean changesMade(){
        return false;
    }
    static void  setChangesToDatabase(){

    }
    static void getChanges(){

    }
    public  static void setSetting(final Scene scene){
        primaryScene=scene;

    }
    public  static void setSetting(final Scene scene,final Stage stage){
        settingScene=scene;
        settingStage=stage;
    }
}
