package com.shoot.encode;

/**
 * Created by Beloved on 11-Feb-18.
 */

import com.shoot.PostHidden.LoadingNotification;
import com.shoot.PostHidden.PostNotification;
import com.shoot.database.StaticDatabase;
import com.shoot.recording.ConcurrentQueue;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MP4Encode {
    static boolean isRecording;
    static  boolean _pauseRecording;
    static int numberOfShot,pauseCount;
    static  Robot robot;
    private static Muxer _muxer;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    static  LoadingNotification loadingNotification;

    /**
     * Records the screen
     */
    public void recordScreen(boolean recording, boolean pauseRecording, String filename, String formatname,
                             String codecname, Rectangle rect, final int snapsPerSecond) throws Exception {
        /**
         * Set up the AWT infrastructure to take screenshots of the desktop.
         */
        {
            isRecording=recording;
            _pauseRecording=pauseRecording;
        }
        loadingNotification=new LoadingNotification("Please Wait...",5,new ProgressIndicator());
        Platform.runLater(MP4Encode::stage);

        loadingNotification.setCount(0);
        //notificationStage.show();
        numberOfShot=snapsPerSecond;
        final Rational framerate = Rational.make(1, snapsPerSecond);

        /** First we create a muxer using the passed in filename and formatname if given. */
        final Muxer muxer = Muxer.make(filename, null, formatname);
        _muxer=muxer;

        /** Now, we need to decide what type of codec to use to encode video. Muxers

         * have limited sets of codecs they can use. We're going to pick the first one that

         * works, or if the user supplied a codec name, we're going to force-fit that

         * in instead.
         */


        final MuxerFormat format = muxer.getFormat();

        final Codec codec;

        if (codecname != null) {
            codec = Codec.findEncodingCodecByName(codecname);

        } else {
            codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());

        }

        /**
         * Now that we know what codec, we need to create an encoder
         */

        Encoder encoder = Encoder.make(codec);
/**
 * Video encoders need to know at a minimum:
 *   width
 *   height
 *   pixel format
 * Some also need to know frame-rate (older codecs that had a fixed rate at which video files could
 * be written needed this). There are many other options you can set on an encoder, but we're
 * going to keep it simpler here.
 */
        encoder.setWidth(rect.width);
        encoder.setHeight(rect.height);
        // We are going to use 420P as the format because that's what most video formats these days use

        final PixelFormat.Type pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;

        encoder.setPixelFormat(pixelformat);

        encoder.setTimeBase(framerate);


        /** An annoynace of some formats is that they need global (rather than per-stream) headers,
         * and in that case you have to tell the encoder. And since Encoders are decoupled from
         * Muxers, there is no easy way to know this beyond
         */
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

        /** Open the encoder. */
        encoder.open(null, null);
/** Add this stream to the muxer. */
        muxer.addNewStream(encoder);
/** And open the muxer for business. */
        muxer.open(null, null);
/** Next, we need to make sure we have the right MediaPicture format objects
 * to encode data with. Java (and most on-screen graphics programs) use some
 * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
 * codecs use some variant of YCrCb formatting. So we're going to have to
 * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
 */
        MediaPictureConverter converter = null;

        MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),pixelformat);

        picture.setTimeBase(framerate);


        /** Now begin our main loop of taking screen snaps.
         * We're going to encode and then write out any resulting packets. */
        final MediaPacket packet = MediaPacket.make();
        //----------------------

        for(int i=1;i<=5;i++){
            Thread.sleep(2000);
            loadingNotification.setCount(i);
        }

        Thread.sleep(100);
        Platform.runLater(MP4Encode::close);

        Platform.runLater(() -> {
            try {
                stageStart();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //new TrayFunction();
        MP4Encode.image(converter,picture,packet,encoder,muxer,rect);

        /** Encoders, like decoders, sometimes cache pictures so it can do the right key-frame optimizations.
         * So, they need to be flushed as well. As with the decoders, the convention is to pass in a null
         * input until the output is not complete.
         */

        do {
            encoder.encode(packet, null);
            if (packet.isComplete())
                muxer.write(packet,  false);
        }
        while (packet.isComplete());


        /** Finally, let's clean up after ourselves. */
        muxer.close();
    }

    private final static void image(MediaPictureConverter converter, MediaPicture picture, MediaPacket packet, Encoder encoder, Muxer muxer,final Rectangle rect){
        ConcurrentQueue queue=new ConcurrentQueue(rect);
        int i=0;
        long startTime=System.currentTimeMillis();
        queue.clearQueue();
        while ( isRecording) {
            /** Make the screen capture && convert image to TYPE_3BYTE_BGR */
            final BufferedImage screen = convertToType(rawBufferedImage(queue,rect), BufferedImage.TYPE_3BYTE_BGR);
            //System.out.println(i);
/** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */

            if (converter == null)

                converter = MediaPictureConverterFactory.createConverter(screen, picture);

            converter.toPicture(picture, screen, i);
            do {
                encoder.encode(packet, picture);

                if (packet.isComplete())

                    muxer.write(packet, false);

            } while (packet.isComplete());
            i++;
            System.out.println("**"+ (System.currentTimeMillis()-startTime)/1000 +"**");
            System.out.println("*"+i++ +"*");
        }
    }

    public static void stopEncoder(boolean nope){
        isRecording=nope;
    }
    public static void pauseRecord(boolean pause){
        _pauseRecording=pause;
    }
    private static BufferedImage rawBufferedImage(ConcurrentQueue queue, Rectangle rect){
        if(numberOfShot>=30){
            while (queue.getSize() == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (_pauseRecording){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return queue.getImage();
        }

        else{
            while (_pauseRecording){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return   robot.createScreenCapture(rect);
        }
    }
    @SuppressWarnings("static-access")



    /**
     * Convert a {@link BufferedImage} of any type, to {@link BufferedImage} of a
     * specified type. If the source image is the same type as the target type,
     * then original image is returned, otherwise new image of the correct type is
     * created and the content of the source image is copied into the new image.
     *
     * @param sourceImage
     *          the image to be converted
     * @param targetType
     *          the desired BufferedImage type
     *
     * @return a BufferedImage of the specifed target type.
     *
     * @see BufferedImage
     */


    public static BufferedImage convertToType(BufferedImage sourceImage,
                                              int targetType)
    {

        BufferedImage image;


        // if the source image is already the target type, return the source image


        if (sourceImage.getType() == targetType)

            image = sourceImage;


// otherwise create a new image of the target type and draw the new

            // image


        else
        {

            image = new BufferedImage(sourceImage.getWidth(),sourceImage.getHeight(), targetType);

            image.getGraphics().drawImage(sourceImage, 0, 0, null);

        }

        return image;

    }

    static void  stage(){
        try {
            loadingNotification.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void  close(){
        try {
            loadingNotification.closeStage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stageStart() throws InterruptedException {

        Button buttonStop=new Button("Stop");
        Button buttonPause=new Button("Pause");
        Button buttonQuit=new Button("Exit");
        Stage st=new Stage();
        st.setOpacity(0);
        st.setResizable(false);
        st.toFront();
        st.sizeToScene();
        Thread.sleep(100);
        st.setIconified(true);
        st.setScene(new Scene(new StackPane(new PostNotification().notify(st,buttonStop,buttonPause,buttonQuit)),1,1));
        st.getIcons().add(new javafx.scene.image.Image("a.jpg",200,200,true,true));
        st.show();
        new StaticDatabase().setControlPlayerStage(st);
        buttonStop.setOnAction(event -> {
            MP4Encode.stopEncoder(false);
            while(_muxer.getState()!= Muxer.State.STATE_CLOSED) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> newStage());
            st.close();
        });
        buttonPause.setOnAction(event ->{
            pauseCount++;
            if(pauseCount%2!=0) {
                pauseRecord(true);
                buttonPause.setText("Resume");
            }else{
                pauseRecord(false);
                buttonPause.setText("Pause");
            }
        } );
        buttonQuit.setOnAction(event -> {
            MP4Encode.stopEncoder(false);
            while(_muxer.getState()!= Muxer.State.STATE_CLOSED) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.close();
            Platform.exit();
            System.exit(0);

        });
    }
    void newStage(){
        new StaticDatabase().getStage().setScene(new StaticDatabase().getScene());
        new StaticDatabase().getStage().show();
    }
}
