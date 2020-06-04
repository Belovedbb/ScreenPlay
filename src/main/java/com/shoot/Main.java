package com.shoot;

import com.shoot.database.StartDatabaseLite;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;


public class Main extends Application {

    public void start(Stage primaryStage){
        Application.setUserAgentStylesheet(STYLESHEET_CASPIAN);
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