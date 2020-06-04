package com.shoot.encode;
/**
 * Created by Beloved on 21-Feb-18.
 */

import com.shoot.recording.ConcurrentQueue;
import io.humble.ferry.Buffer;
import io.humble.video.*;
import io.humble.video.AudioFormat;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//Audio issues with embedding with video
@Deprecated
public class  MP4EncodeVideoAudio {
    static boolean isRecording;
    static Encoder audioEncoder;
    static TargetDataLine tLine ;
    static MediaAudio aSample;
    static  Buffer _audioBuffer;
    /**
     * Records the screen
     */
    public static void recordScreen(boolean recording, String filename, String formatname, String codecname, Rectangle rect, int snapsPerSecond) throws AWTException, InterruptedException, IOException {
        /**
         * Set up the AWT infrastructure to take screenshots of the desktop.
         */
        {
            isRecording=recording;
        }

        final Rational framerate = Rational.make(1, snapsPerSecond);


        /** First we create a muxer using the passed in filename and formatname if given. */
        final Muxer muxer = Muxer.make(filename, null, formatname);
        System.out.println("format name");

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

        embedAudio(muxer,format,framerate);
/** And open the muxer for business. */
        muxer.open(null, null);
        tLine.start();

/** Next, we need to make sure we have the right MediaPicture format objects
 * to encode data with. Java (and most on-screen graphics programs) use some
 * variant of Red-Green-Blue image encoding (a.k.a. RGB or BGR). Most video
 * codecs use some variant of YCrCb formatting. So we're going to have to
 * convert. To do that, we'll introduce a MediaPictureConverter object later. object.
 */
        MediaPictureConverter converter = null;

        final MediaPicture picture = MediaPicture.make(
                encoder.getWidth(),
                encoder.getHeight(),pixelformat);

        picture.setTimeBase(framerate);


        /** Now begin our main loop of taking screen snaps.
         * We're going to encode and then write out any resulting packets. */
        final MediaPacket packet = MediaPacket.make();

        //----------------------
        MP4EncodeVideoAudio.image(converter,picture,packet,encoder,muxer,rect);

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

    private final static void image(MediaPictureConverter converter, MediaPicture picture, MediaPacket packet, Encoder encoder, Muxer muxer,Rectangle rect){
        ConcurrentQueue queue=new ConcurrentQueue(rect);
        int i=0;int j=0;
        long startTime=System.currentTimeMillis();
        // MediaPacket pk=MediaPacket.make();
        MediaAudioResampler audioResampler = MediaAudioResampler.make(audioEncoder.getChannelLayout(), audioEncoder.getSampleRate(), audioEncoder.getSampleFormat(), AudioChannel.Layout.CH_LAYOUT_STEREO, 44100, AudioFormat.Type.SAMPLE_FMT_S16);
        audioResampler.open();

        MediaAudio rawAudio = MediaAudio.make(1024/2, 44100, 2,AudioChannel.Layout.CH_LAYOUT_STEREO, AudioFormat.Type.SAMPLE_FMT_S16);
        rawAudio.setTimeBase(Rational.make(1, 44100));
        while ( isRecording) {
            /** Make the screen capture && convert image to TYPE_3BYTE_BGR */
            final BufferedImage screen = convertToType(rawBufferedImage(queue), BufferedImage.TYPE_3BYTE_BGR);
            //System.out.println(i);
/** This is LIKELY not in YUV420P format, so we're going to convert it using some handy utilities. */

            if (converter == null)

                converter = MediaPictureConverterFactory.createConverter(screen, picture);

            converter.toPicture(picture, screen, i);
            tLine.read(_audioBuffer.getByteArray(0, _audioBuffer.getBufferSize()), 0, _audioBuffer.getBufferSize());
            rawAudio.getData(0).put(_audioBuffer.getByteArray(0, _audioBuffer.getBufferSize()), 0, 0,_audioBuffer.getBufferSize());

            // int sampleCount = i/2;
//            rawAudio.setNumSamples(sampleCount);
            // rawAudio.setTimeStamp(j);
            j+=i;
            //rawAudio.setComplete(true);

            ///////

            // aSample.setComplete(true);
            do {
                encoder.encode(packet, picture);
                audioEncoder.encode(packet, rawAudio);
                System.out.println("Got to this point");
                //audioEncoder.encodeAudio(pk,aSample);

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

    private static BufferedImage rawBufferedImage(ConcurrentQueue queue){

        while(queue.getSize()==0){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return queue.getImage();
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

    private static void embedAudio( Muxer muxer,MuxerFormat format,Rational framerate){
        AudioFormat.Type fileType = io.humble.video.AudioFormat.Type.SAMPLE_FMT_S16;
        checkAudioFormats();
        javax.sound.sampled.AudioFormat aFormat = new javax.sound.sampled.AudioFormat(javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,44100,16,1,2,24,false);
        TargetDataLine line=null;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, aFormat);
        if(!AudioSystem.isLineSupported(info)){
            System.out.println("Line is not supported");
        }

        try {
            line=(TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        try {
            line.open(aFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        tLine=line;
        int bufferSize = (aFormat.getSampleSizeInBits() * aFormat.getFrameSize()*10);
        Buffer audioBuffer = Buffer.make(null, bufferSize);
        _audioBuffer=audioBuffer;
        MediaAudio sample = MediaAudio.make(audioBuffer,(int) aFormat.getSampleRate(), (int)aFormat.getSampleRate(), aFormat.getChannels(),AudioChannel.Layout.CH_LAYOUT_MONO, fileType);
        aSample=sample;
        Codec acodec = Codec.findEncodingCodec(format.getDefaultAudioCodecId());
        Encoder aEncoder = Encoder.make(acodec);
        System.out.println("Audio codec is"+ acodec.getName());
        aEncoder.setChannels(1);
        aEncoder.setChannelLayout(AudioChannel.Layout.CH_LAYOUT_MONO);
        aEncoder.setSampleFormat(fileType);
        aEncoder.setSampleRate((int) aFormat.getSampleRate());
        aEncoder.setTimeBase(framerate);
        aEncoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER,true);
        aEncoder.open(null, null);
        audioEncoder=aEncoder;
        muxer.addNewStream(aEncoder);

    }
    private static void checkAudioFormats() {
        Mixer mixer = AudioSystem.getMixer(null); // default mixer
        try {
            mixer.open();
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.printf("Supported SourceDataLines of default mixer (%s):\n\n", mixer.getMixerInfo().getName());
        for(Line.Info info : mixer.getSourceLineInfo()) {
            if(SourceDataLine.class.isAssignableFrom(info.getLineClass())) {
                SourceDataLine.Info info2 = (SourceDataLine.Info) info;
                System.out.println(info2);
                System.out.printf("  max buffer size: \t%d\n", info2.getMaxBufferSize());
                System.out.printf("  min buffer size: \t%d\n", info2.getMinBufferSize());
                javax.sound.sampled.AudioFormat[] formats = info2.getFormats();
                System.out.println("  Supported Audio formats: ");
                for(javax.sound.sampled.AudioFormat format : formats) {
                    System.out.println("    "+format);
                }
                System.out.println();
            } else {
                System.out.println(info.toString());
            }
            System.out.println();
        }

    }
    public static void render(String filename,Muxer muxer) throws IOException, InterruptedException {
        AudioChannel.Layout inLayout = AudioChannel.Layout.CH_LAYOUT_STEREO;
        int inSampleRate = 44100;
        AudioFormat.Type inFormat = AudioFormat.Type.SAMPLE_FMT_S16;
        int bytesPerSample = 2;

        File inFile = new File("input.wav");
        //Starting everything up.

        //Muxer muxer = Muxer.make(new File(filename).getAbsolutePath(), null, null);
        Codec codec = Codec.guessEncodingCodec(muxer.getFormat(), null, null, null, MediaDescriptor.Type.MEDIA_AUDIO);

        AudioFormat.Type findType = null;
        for(AudioFormat.Type type : codec.getSupportedAudioFormats()) {
            if(findType == null) {
                findType = type;
            }
            if(type == inFormat) {
                findType = type;
                break;
            }
        }

        if(findType == null){
            throw new IllegalArgumentException("Couldn't find valid audio format for codec: " + codec.getName());
        }

        Encoder encoder = Encoder.make(codec);
        encoder.setSampleRate(44100);
        encoder.setTimeBase(Rational.make(1, 44100));
        encoder.setChannels(2);
        encoder.setChannelLayout(AudioChannel.Layout.CH_LAYOUT_STEREO);
        encoder.setSampleFormat(findType);
        encoder.setFlag(Coder.Flag.FLAG_GLOBAL_HEADER, true);

        encoder.open(null, null);
        muxer.addNewStream(encoder);
        muxer.open(null, null);

        MediaPacket audioPacket = MediaPacket.make();
        MediaAudioResampler audioResampler = MediaAudioResampler.make(encoder.getChannelLayout(), encoder.getSampleRate(), encoder.getSampleFormat(), inLayout, inSampleRate, inFormat);
        audioResampler.open();

        MediaAudio rawAudio = MediaAudio.make(1024/bytesPerSample, inSampleRate, 2, inLayout, inFormat);
        rawAudio.setTimeBase(Rational.make(1, inSampleRate));

        //Reading

        try(BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inFile))){
            reader.skip(44);

            int totalSamples = 0;

            byte[] buffer = new byte[1024];
            int readLength;
            while((readLength = reader.read(buffer, 0, 1024)) != -1){
                int sampleCount = readLength/bytesPerSample;

                rawAudio.getData(0).put(buffer, 0, 0, readLength);
                rawAudio.setNumSamples(sampleCount);
                rawAudio.setTimeStamp(totalSamples);

                totalSamples += sampleCount;

                rawAudio.setComplete(true);

                MediaAudio usedAudio = rawAudio;

                if(encoder.getChannelLayout() != inLayout ||
                        encoder.getSampleRate() != inSampleRate ||
                        encoder.getSampleFormat() != inFormat){
                    usedAudio = MediaAudio.make(
                            sampleCount,
                            encoder.getSampleRate(),
                            encoder.getChannels(),
                            encoder.getChannelLayout(),
                            encoder.getSampleFormat());
                    audioResampler.resample(usedAudio, rawAudio);
                }

                do{
                    encoder.encodeAudio(audioPacket, usedAudio);
                    if(audioPacket.isComplete()) {
                        muxer.write(audioPacket, false);
                    }
                } while (audioPacket.isComplete());
            }
        }
        catch (IOException e){
            e.printStackTrace();
            muxer.close();
            System.exit(-1);
        }}

}
