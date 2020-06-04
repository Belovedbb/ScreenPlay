package com.shoot.go.rectangle;

import com.shoot.PostHidden.PostNotification;
import com.shoot.database.StartDatabaseLite;
import com.shoot.PostHidden.Parameter;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

/**
 * Created by Beloved on 15-Jan-18.
 */
//This class is for custom canvas to determine the rectangle
//USE CTRL C TO CAPTURE THE RECTANGLE
public class CanvasRectangle {
    private final int TRIM_COVER=100;
    static final double SCREEN_DEFAULT_WIDTH;
    static final double SCREEN_DEFAULT_HEIGHT;

    static {
        SCREEN_DEFAULT_WIDTH= Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        SCREEN_DEFAULT_HEIGHT= Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }

    private Parameter choice;
    private Stage primaryStage,canvasStage;
    private Rectangle captureRect;
    private Scene scene,primaryScene;
    private CaptureRectangle capt;
    private  String fileName,format;
    //Constructor for shot
    public CanvasRectangle(String fileName,String format,Parameter choice, Scene primaryScene, Stage primaryStage){
        this.fileName=fileName;
        this.format=format;
        this.choice=choice;
        this.primaryScene=primaryScene;
        this.primaryStage=primaryStage;
        canvasStage=new Stage();
        primaryStage.hide();
        //The main method
        canvasScene();
    }

    private void canvasScene(){
        StackPane pane=new StackPane(canvasPane(),new PostNotification().notify(canvasStage,"  Drag the mouse on the capture area \n Use the keycode CTRL+C to capture screen"));
        pane.setAlignment(Pos.TOP_LEFT);
        scene = new Scene(pane);
        canvasStage.initStyle(StageStyle.UNDECORATED);
        canvasStage.setMaximized(true);
        canvasStage.getIcons().add(new javafx.scene.image.Image("a.jpg",200,200,true,true));

        //Set transparency
        try {
            String statmentGet="select opacity from "+ StartDatabaseLite.getTableName()+" where id='2'";
            double value=Double.parseDouble((new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1)));
            if(value==0.0){
                value+=0.1;
            }
            canvasStage.setOpacity(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        canvasStage.setOnShowing(event -> pane.getChildren().remove(1));
        canvasStage.setScene(scene);
        canvasStage.show();
    }
    //Pane for the canvas
    private BorderPane canvasPane(){
        BorderPane pane=new BorderPane();
        //button=new Button("Click");
        SwingNode swingComponent=new SwingNode();
        transparentRGB(swingComponent);
        //pane.setTop(button);
        pane.setCenter(swingComponent);
        return pane;
    }
    //Inner rectangle
    private void transparentRGB(SwingNode swing){
        SwingUtilities.invokeLater(() -> {
            final   BufferedImage screen =new BufferedImage(getCanvasWidth(),getCanvasHeight(),BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g=screen.createGraphics();
            g.setBackground(new Color(0,true));
            g.dispose();
            swing.setContent(imageLocation(screen));

        });
    }
    //Listener for mouse movement
    private JPanel imageLocation(final BufferedImage screen){
        final BufferedImage screenCopy = new BufferedImage(
                screen.getWidth(),
                screen.getHeight(),
                screen.getType());
        final JLabel screenLabel = new JLabel(new ImageIcon(screenCopy));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(screenLabel, BorderLayout.CENTER);

        repaint(screen, screenCopy);
        screenLabel.repaint();

        screenLabel.addMouseMotionListener(new MouseMotionAdapter() {

            Point start = new Point();

            @Override
            public void mouseMoved(MouseEvent me) {
                start = me.getPoint();
                repaint(screen, screenCopy);
                screenLabel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                Point end = me.getPoint();
                captureRect=new Rectangle(start.x, start.y,end.x-start.x, end.y-start.y);
                repaint(screen, screenCopy);
                screenLabel.repaint();
                scene.setOnKeyPressed(e->{
                    if(e.getCode()== KeyCode.C&&e.isControlDown()){
                        // System.out.println("Rectangle : " + captureRect);
                        // primaryStage.hide();
                        canvasStage.close();
                        capt= new CaptureRectangle(fileName,format,captureRect,choice,primaryScene,primaryStage);

                    }
                });


            }

        });
        return panel;
    }
    CaptureRectangle capt(){
        return  capt;
    }
    private void repaint(BufferedImage orig, BufferedImage copy) {
        Graphics2D g = copy.createGraphics();
        g.drawImage(orig,0,0, null);
        if (captureRect!=null) {
            g.setColor(Color.RED);
            g.draw(captureRect);
            g.setColor(new Color(255,255,255,150));
            g.fill(captureRect);
        }
        g.dispose();
    }
    private int getCanvasWidth(){
        return  (int)(SCREEN_DEFAULT_WIDTH+TRIM_COVER);
    }
    private int getCanvasHeight(){
        return (int)(SCREEN_DEFAULT_WIDTH+TRIM_COVER);
    }
}
