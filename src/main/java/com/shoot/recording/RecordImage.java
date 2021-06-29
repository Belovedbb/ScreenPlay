package com.shoot.recording;
import com.shoot.PostHidden.Parameter;
import com.shoot.database.StartDatabaseLite;
import com.shoot.encode.MP4Encode;
import com.shoot.encode.MP4EncodeVideoAudio;
import com.shoot.go.rectangle.CaptureRectangle;
import javafx.stage.Stage;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Beloved on 27-Jan-18.
 */
//This class is for audioless video recording, implements its own control listener
public class RecordImage implements  RecordImageInterface{
    private Rectangle rect;
    private boolean recording,pauseRecording;
    private Parameter choice;
    private static Stage primaryStage;
    private String filename,format;
    //Constructor used to implement screen recording
    public RecordImage(String filename, String format, Parameter choice, Stage primaryStage){
        this. filename=filename;
        this.format=format;
        this.choice=choice;
        RecordImage.primaryStage =primaryStage;
    }
    //Start record
    @Override
    public void startRecording() {
        recording=true;
        new Thread(new CaptureRecord()).start();
    }

    public void setCanvasRectangle(Rectangle rect){
        this.rect=rect;
    }

    @Override
    public void stopRecording() {
        recording=false;
        MP4Encode.stopEncoder(recording);
    }

    @Override
    public void pauseRecording(boolean pause) {
        MP4Encode.pauseRecord(pause);
    }



    private class CaptureRecord implements  Runnable{
        String getDatabaseAddress;
        String getDatabaseName;

        @Override
        public void run() {
            String statmentGet="select file from "+ StartDatabaseLite.getTableName()+" where id='2'";
            try {
                getDatabaseAddress= new StartDatabaseLite().getUpdateCustomRow(new StartDatabaseLite().getConnection(),statmentGet,1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(getDatabaseAddress.equals(null)){
                getDatabaseAddress=System.getProperty("user.home");
            }


            CaptureRectangle capt=  new CaptureRectangle(choice);

            if (rect==null)
                rect=capt.getRectangle();
            if(choice==Parameter.SCREEN_RECORD||choice==Parameter.SCREEN_RECORD_CUSTOM) {
                try {
                    new   MP4Encode().recordScreen(recording,false, getDatabaseAddress+"\\"+filename+"."+format.toLowerCase(), null, null, rect, getFramePerSecond(rect));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(choice==Parameter.SCREEN_RECORD_AUDIO||choice==Parameter.SCREEN_RECORD_AUDIO_CUSTOM){
                try {
                    MP4EncodeVideoAudio.recordScreen(recording,getDatabaseAddress+"\\"+filename+"."+format.toLowerCase(), null, null, rect, getFramePerSecond(rect));
                } catch (AWTException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    //get number of frames per sec
    private int getFramePerSecond(Rectangle rect){
        final long recordTime=1000;
        int countFrame=0;
        try {
            Robot  bot=new Robot();
            final long startTime=System.currentTimeMillis();
            while((System.currentTimeMillis()-startTime)<=recordTime){
                bot.createScreenCapture(rect);
                countFrame++;
            }
            System.out.println("FramePerSecond is : "+countFrame);
            return countFrame;
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return 0;
    }



}

