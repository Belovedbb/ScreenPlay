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

    protected TreeItem<String> deleteFile;
    protected TreeItem<String> subDirectory;

    public  SettingTree(Stage stage)
    {


        TreeItem<String> root = new TreeItem<>("Settings");
        root.setExpanded(true);
        TreeItem<String> appearance = makeShow("Appearance", root);
        TreeItem<String> theme = makeShow("Theme", appearance);
        TreeItem<String> color = makeShow("Color", appearance);

        TreeItem<String> directory = makeShow("File Directory", root);
        deleteFile=makeShow("Delete File", directory);
        subDirectory=makeShow("Select Directory", directory);

        TreeItem<String> opacity = makeShow("Canvas Opacity", root);
        TreeItem<String> opLev = makeShow("Opacity Level", opacity);


        TreeView<String> tree = new TreeView<>(root);

        tree.minHeightProperty().bind(stage.heightProperty());
        // tree.setMinHeight(stage.getMinHeight());
        tree.setShowRoot(false);
        Settings.change(tree);
        Label lblShowName = new Label();
        //this.setPadding(new Insets(20,20,20,20));
        this.getChildren().addAll(tree, lblShowName);

    }
    private TreeItem<String> makeShow(String title,
                                      TreeItem<String> parent)
    {
        TreeItem<String> show = new TreeItem<>(title);
        show.setExpanded(true);
        parent.getChildren().add(show);
        return show;
    }

}

