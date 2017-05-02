package com.huseyina.project.hueambilight;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDiscoveryEvent;
import com.github.sarxos.webcam.WebcamDiscoveryListener;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.WebcamStreamer;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;


/**
 * Proof of concept of how to handle webcam video stream from Java
 * 
 * @author Huseyin Arpalikli
 */

public class WebcamViewer extends JFrame implements Runnable, WebcamListener, WindowListener,
    UncaughtExceptionHandler, ItemListener, WebcamDiscoveryListener {

  private static final long serialVersionUID = 1L;

  public WebcamViewer() {
    run();
  }

  private Webcam webcam = null;
  private WebcamPanel panel = null;
  private WebcamPicker picker = null;
  private WebcamStreamer streamer = null;
  private static final long PERIOD = (long) 7.315200000000001; // EQUIVALENT TO 24FPS

  @Override
  public void run() {
    Webcam.addDiscoveryListener(this);
    
    setTitle("Webcam Capture");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    addWindowListener(this);

    picker = new WebcamPicker();
    picker.addItemListener(this);

    webcam = picker.getSelectedWebcam();
    webcam.close();
    if (webcam == null) {
      System.out.println("No webcams found...");
      System.exit(1);
    }
    
    webcam.setViewSize(/*WebcamResolution.VGA.getSize()*/new Dimension(640,480));
    webcam.addWebcamListener(WebcamViewer.this);
//    panel = new WebcamPanel(webcam, false);
//    panel.setFPSDisplayed(true);
//    panel.setFPSLimit(30);
//    panel.setFPSLimited(true);
//    panel.setMirrored(false);
//    panel.setDisplayDebugInfo(true);
//    panel.setImageSizeDisplayed(true);
    webcam.open();
    streamer = new WebcamStreamer(8080, webcam, 30, true);
    add(picker, BorderLayout.NORTH);
//    add(panel, BorderLayout.CENTER);

    pack();
    setResizable(false);
    setVisible(true);


//    Thread t = new Thread() {
//      @Override
//      public void run() {
//        panel.start();
//      }
//    };
//    t.setName("starter");
//    t.setDaemon(true);
//    t.setUncaughtExceptionHandler(this);
//    t.start();

    imageCapture();
    
  }
  
  public void imageCapture(){
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    if (webcam.open(true)) {
      scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {

          try {
            // get image
            byte[] imageInByte;
            BufferedImage capturedImg = webcam.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(capturedImg, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
            ImageCaptureObject.setImage(imageInByte);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

          // WebcamUtils.capture(webcam, "images/" + dateFormat.format(date), ImageUtils.FORMAT_JPG);
          // System.gc ();
          // System.runFinalization ();
        }
      }, PERIOD, PERIOD, TimeUnit.MILLISECONDS);
    }
  }
  static {
    Webcam.setDriver(new V4l4jDriver());
}
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new WebcamViewer());
  }

  @Override
  public void webcamOpen(WebcamEvent we) {
    System.out.println("webcam open");
    imageCapture();
  }

  @Override
  public void webcamClosed(WebcamEvent we) {
    System.out.println("webcam closed");
  }

  @Override
  public void webcamDisposed(WebcamEvent we) {
    System.out.println("webcam disposed");
  }

  @Override
  public void webcamImageObtained(WebcamEvent we) {}

  @Override
  public void windowActivated(WindowEvent e) {}

  @Override
  public void windowClosed(WindowEvent e) {
    webcam.close();
  }

  @Override
  public void windowClosing(WindowEvent e) {}

  @Override
  public void windowOpened(WindowEvent e) {
    imageCapture();
  }

  @Override
  public void windowDeactivated(WindowEvent e) {}

  @Override
  public void windowDeiconified(WindowEvent e) {
    System.out.println("webcam viewer resumed");
//    panel.resume();
    imageCapture();
  }

  @Override
  public void windowIconified(WindowEvent e) {
    System.out.println("webcam viewer paused");
//    panel.pause();
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    System.err.println(String.format("Exception in thread %s", t.getName()));
    e.printStackTrace();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getItem() != webcam) {
      if (webcam != null) {

//        panel.stop();

//        remove(panel);

        streamer.stop();
        webcam.removeWebcamListener(this);
        webcam.close();
        
        webcam = (Webcam) e.getItem();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.addWebcamListener(this);
        webcam.open();
        System.out.println("selected " + webcam.getName());

//        panel = new WebcamPanel(webcam, false);
//        panel.setFPSDisplayed(true);
//        panel.setFPSLimit(30);
//        panel.setFPSLimited(true);
//        panel.setMirrored(false);
//        panel.setDisplayDebugInfo(true);
//        panel.setImageSizeDisplayed(true);
//        add(panel, BorderLayout.CENTER);
//        pack();
//
//        Thread t = new Thread() {
//
//          @Override
//          public void run() {
//            panel.start();
//          }
//        };
//        t.setName("restart");
//        t.setDaemon(true);
//        t.setUncaughtExceptionHandler(this);
//        t.start();
       
        imageCapture();
        streamer = new WebcamStreamer(8080, webcam, 30, true);
      }
    }
  }

  @Override
  public void webcamFound(WebcamDiscoveryEvent event) {
    if (picker != null) {
      picker.addItem(event.getWebcam());
    }
  }

  @Override
  public void webcamGone(WebcamDiscoveryEvent event) {
    if (picker != null) {
      picker.removeItem(event.getWebcam());
    }
  }
}
