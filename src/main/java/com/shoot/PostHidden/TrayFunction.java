package com.shoot.PostHidden;

/**
 * Created by Beloved on 03-Feb-18.
 */

import com.shoot.database.StaticDatabase;
import com.shoot.recording.RecordImage;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

// Java 8 code
public class TrayFunction   {
    private java.awt.MenuItem pauseRecording,stopRecording;

    private static final String iconImageLoc =
            "a.jpg";

    private Stage primaryStage;
    int i=0;
    RecordImage r;
    Scene scene;

    public  TrayFunction(RecordImage r,Scene scene, Stage primaryStage){
        this.r=r;
        this.scene=scene;
        this.primaryStage = primaryStage;
        startTray();

    }
    public void startTray() {
        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);

        // sets up the tray icon (using awt code run on the swing thread).
        Platform.runLater(()->{
            addAppToTray();
        });

    }


    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            URL imageLoc = new URL( iconImageLoc);
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            // if the user double-clicks on the tray icon, show the main app stage.
            // trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.


            pauseRecording = new java.awt.MenuItem("Pause Recording");
            pauseRecording.addActionListener(event -> {
                if(i%2==0) {
                    r.pauseRecording(true);
                    pauseRecording.setName("Resume Recording");
                }
                else {
                    r.pauseRecording(false);
                    pauseRecording.setName("Pause Recording");
                }
                i++;
            });

            stopRecording = new java.awt.MenuItem("Stop Recording");
            stopRecording.addActionListener(event -> {
                r.stopRecording();
                Platform.runLater(this::newStage);
                Platform.runLater(this::closePlayerStage);
                tray.remove(trayIcon);
                // Platform.exit();
                //enableTool(true);
            });



            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
                System.exit(0);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.addSeparator();
            popup.add(pauseRecording);
            popup.add(stopRecording);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon.setPopupMenu(popup);

            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }


    private void newStage(){
        new StaticDatabase().getStage().setScene(new StaticDatabase().getScene());
        new StaticDatabase().getStage().show();    }
    private void closePlayerStage(){
        new StaticDatabase().getControlPlayerStage().close();
    }
}
