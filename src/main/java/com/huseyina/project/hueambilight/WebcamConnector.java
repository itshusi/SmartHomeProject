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

import org.apache.commons.lang3.SystemUtils;

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
 * Handle webcam video stream and image capture from Java
 * 
 * @author Huseyin Arpalikli
 */

public class WebcamConnector extends JFrame implements Runnable, WebcamListener, WindowListener,
    UncaughtExceptionHandler, ItemListener, WebcamDiscoveryListener {

  private static final long serialVersionUID = 1L;

  public WebcamConnector() {
    run();
  }

  private Webcam webcam = null;
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

    webcam.setViewSize(WebcamResolution.VGA.getSize());
    webcam.addWebcamListener(WebcamConnector.this);

    webcam.open();
    streamer = new WebcamStreamer(8080, webcam, 30, true);
    add(picker, BorderLayout.NORTH);

    pack();
    setResizable(false);
    setVisible(true);

    imageCapture();

  }

  public void imageCapture() {
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
            ImageCaptureObject.getInstance();
            ImageCaptureObject.setImage(imageInByte);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }, PERIOD, PERIOD, TimeUnit.MILLISECONDS);
    }
  }

  // For linux rPi
  static {
    if (SystemUtils.IS_OS_LINUX) {
      Webcam.setDriver(new V4l4jDriver());
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new WebcamConnector());
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
    imageCapture();
  }

  @Override
  public void windowIconified(WindowEvent e) {
    System.out.println("webcam viewer paused");
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
        streamer.stop();
        webcam.removeWebcamListener(this);
        webcam.close();

        webcam = (Webcam) e.getItem();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.addWebcamListener(this);
        webcam.open();
        System.out.println("selected " + webcam.getName());

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
