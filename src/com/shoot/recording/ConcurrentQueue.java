package com.shoot.recording;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Beloved on 01-Feb-18.
 */
//Class responsible for the actual capturing of images sequentially
//Using three capture threads
public class ConcurrentQueue {
    //Using synchronized queue for storage
    static  LinkedBlockingQueue<BufferedImage>  imageList1= new LinkedBlockingQueue<>();
    private  static Rectangle rec;
    final static int CAPACITY=30;
    static  int  count =0;
    public ConcurrentQueue(Rectangle rect){
        rec=rect;
        ExecutorService exec= Executors.newCachedThreadPool();
        exec.execute(new list1());
        exec.execute(new list2());
        exec.execute(new list3());
    }

    private static synchronized LinkedBlockingQueue<BufferedImage> list(){
        return  imageList1;
    }
    public int getSize(){
        return list().size();

    }
    public void clearQueue(){
        list().clear();
    }
    public static BufferedImage getImage(){
        return    list().poll();
    }

    private  class list1 implements  Runnable{

        @Override
        public void run() {
            try {
                Robot  bot=new Robot();
                while(true) {
                    while(list().size()>=CAPACITY){
                        Thread.sleep(50);
                    }
                    list().put(bot.createScreenCapture(rec));
                    count++;
                    System.out.println("count 1 "+count);
                }
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
    private  class list2 implements  Runnable{

        @Override
        public void run() {
            try {
                Robot  bot=new Robot();
                while(true) {
                    while(list().size()>=CAPACITY){
                        Thread.sleep(50);
                    }
                    list().put(bot.createScreenCapture(rec));
                    count++;
                    System.out.println("count 2 "+count);
                }
            } catch (AWTException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private  class list3 implements  Runnable{

        @Override
        public void run() {
            try {
                Robot  bot=new Robot();
                while(true) {
                    while(list().size()>=CAPACITY){
                        Thread.sleep(50);
                    }
                    list().put(bot.createScreenCapture(rec));
                    count++;
                    System.out.println("count 3 "+count);
                }
            } catch (AWTException | InterruptedException e) {
                e.printStackTrace();
            }


        }
    }


}
