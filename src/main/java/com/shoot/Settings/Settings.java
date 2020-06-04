package com.shoot.Settings;

import com.shoot.database.StartDatabaseLite;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Created by Beloved on 22-Jan-18.
 */
//class entry for settings
public class Settings{
    static Stage mainStage;
    static StackPane sp2;
    Scene scene;

    public Settings(){
        Stage stage=new Stage();
        mainStage=stage;
        scene=new Scene(mainPane(),800,500);
        TreeFunction.setSetting(scene,stage);
        final  String statmentGet="select color from "+ StartDatabaseLite.getTableName()+" where id='2'";
        try {
            scene.setFill(Color.valueOf(new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new javafx.scene.image.Image("a.jpg",200,200,true,true));
        stage.setAlwaysOnTop(true);
        stage.setTitle("Settings");
        stage.showAndWait();
    }

    protected static Stage getSettingStage(){
        return  mainStage;
    }


    private VBox mainPane(){
        VBox vbox=new VBox(menus(),splitPane());
        return vbox;
    }
    private SplitPane splitPane(){
        SplitPane sp = new SplitPane();
        StackPane sp1 = new StackPane();
        sp1.getChildren().add((new SettingTree(mainStage)));
        sp2 = new StackPane();
        sp2.getChildren().add(getNodeDefault());
        sp.setDividerPositions(0.2);
        sp1.setMaxWidth(200);
        sp.getItems().addAll(sp1, sp2);
        return sp;
    }
//initialize a default tree node for display
    Node getNodeDefault(){
        return new  TreeFunction().appearance();
    }
//listner method for tree class @SettingsTree.java
    public static void change(TreeView<String> tree){
        tree.getSelectionModel().selectedItemProperty()
                .addListener( (value, oldValue, newValue) ->
                        Settings.tree_SelectionChanged(newValue) );
    }
    //tree listener implementation
    protected static void tree_SelectionChanged(TreeItem<String> item)
    {
        if (item != null)
        {
            switch (item.getValue()){
                case  "Appearance":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new TreeFunction().appearance());
                    break;
                case  "Theme":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add( new  TreeFunction().theme());
                    break;
                case  "Color":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new TreeFunction().color());
                    break;
                case  "File Directory":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new  TreeFunction().fileDirectory());
                    break;
                case  "Delete File":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new  TreeFunction().deleteFile());
                    break;
                case  "Select Directory":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new TreeFunction().directory());
                    break;
                case  "Canvas Opacity":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new TreeFunction().canvasOpacity());
                    break;
                case  "Opacity Level":
                    sp2.getChildren().remove(0);
                    sp2.getChildren().add(new TreeFunction().opLev());
                    break;

                default :
                    break;
            }
            // lblShowName.setText(item.getValue());
        }

    }
    //sham menu
    private HBox menus(){
        MenuBar menubar=new MenuBar();

        Menu file=new Menu("");

        menubar.getMenus().addAll(file);
        menubar.setMinWidth(1000);
        return new HBox(menubar);
    }
}
