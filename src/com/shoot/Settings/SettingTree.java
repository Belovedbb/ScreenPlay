package com.shoot.Settings;

/**
 * Created by Beloved on 24-Jan-18.
 */

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
//tree class
public class SettingTree extends Pane
{

    TreeView<String> tree;
    Label lblShowName;
    TreeItem<String> root, appearance,opacity, directory,deleteFile,theme,
            color,subDirectory,opLev;
    public  SettingTree(Stage stage)
    {


        root = new TreeItem<>("Settings");
        root.setExpanded(true);
        appearance = makeShow("Appearance", root);
        theme=makeShow("Theme", appearance);
        color= makeShow("Color", appearance);

        directory = makeShow("File Directory", root);
        deleteFile=makeShow("Delete File", directory);
        subDirectory=makeShow("Select Directory", directory);

        opacity = makeShow("Canvas Opacity", root);
        opLev=makeShow("Opacity Level", opacity);


        tree = new TreeView<>(root);

        tree.minHeightProperty().bind(stage.heightProperty());
        // tree.setMinHeight(stage.getMinHeight());
        tree.setShowRoot(false);
        Settings.change(tree);
        lblShowName = new Label();
        //this.setPadding(new Insets(20,20,20,20));
        this.getChildren().addAll(tree, lblShowName);

    }
    public TreeItem<String> makeShow(String title,
                                     TreeItem<String> parent)
    {
        TreeItem<String> show = new TreeItem<>(title);
        show.setExpanded(true);
        parent.getChildren().add(show);
        return show;
    }

}

