package com.shoot.go;

import com.shoot.database.StartDatabaseLite;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

//This is the entry class for screen play

public class Main extends Application {

    public void start(Stage primaryStage){
        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
        //the progressbar for loading the database will be slow if being loaded from a javafx thread
        //thereby being loaded from a normal thread
        new Thread(()->{
            try {
              new  StartDatabaseLite().initializeDatabase(primaryStage);
            } catch (ClassNotFoundException | SQLException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String... args){
        Application.launch(args);
    }
}