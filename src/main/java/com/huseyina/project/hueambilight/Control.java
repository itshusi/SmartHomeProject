package com.huseyina.project.hueambilight;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;


public class Control {
  public static boolean ambilightProcessIsActive = false;
  private Timer captureLoop;
  private int transitionTime = 5;

  private float lastAutoOffBri = 0f;

  public Control() throws Exception {
    PHBridge.setup();
  }

  public void setLight(PHLight light, Color colour) throws Exception // calculate colour and send it
                                                                     // to
  // light
  {
    float[] colourHSB = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null); // unmodified
    // HSB
    // colour

    colour = Color.getHSBColor(colourHSB[0],
        Math.max(0f, Math.min(1f, colourHSB[1] * (Main.ui.slider_Saturation.getValue() / 100f))),
        (float) (colourHSB[2] * (Main.ui.slider_Brightness.getValue() / 100f)
            * (Settings.Light.getBrightness(light) / 100f))); // modified colour

    double[] xy = ColourCalc.translate(colour, Settings.getBoolean("gammacorrection")); // xy colour
    int bri = Math
        .round(Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null)[2] * 255); // brightness

    String APIurl = "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username
        + "/lights/" + light.id + "/state";
    String data = "{\"xy\":[" + xy[0] + ", " + xy[1] + "], \"bri\":" + bri + ", \"transitiontime\":"
        + transitionTime + "}";

    // turn light off automatically if the brightness is very low
    if (Settings.getBoolean("autoswitch")) {
      if (colourHSB[2] > lastAutoOffBri + 0.1f && light.isOn() == false) {
        data = "{\"on\":true, \"xy\":[" + xy[0] + ", " + xy[1] + "], \"bri\":" + bri
            + ", \"transitiontime\":" + transitionTime + "}";
      } else if (colourHSB[2] <= 0.0627451f && light.isOn() == true) {
        data = "{\"on\":false, \"transitiontime\":3}";
        lastAutoOffBri = colourHSB[2];
      }
    } else if (Settings.getBoolean("autoswitch") == false && light.isOn() == false) {
      data = "{\"on\":true, \"xy\":[" + xy[0] + ", " + xy[1] + "], \"bri\":" + bri
          + ", \"transitiontime\":" + transitionTime + "}";
    }

    Request.PUT(APIurl, data);
  }

  public void startAmbilightProcess() throws Exception {
    Main.ui.button_Off.setEnabled(false);
    Main.ui.button_On.setEnabled(false);

    Main.ui.button_Stop.setEnabled(true);
    Main.ui.button_Start.setEnabled(false);

    ambilightProcessIsActive = true;

    for (PHLight light : PHBridge.lights) {
      light.storeLightColour();
    }

    // create a loop to execute the ambilight process
    captureLoop = new Timer();
    TimerTask task = new TimerTask() {
      public void run() {
        try {
          SyncProcess.execute();
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    };
    captureLoop.scheduleAtFixedRate(task, 0, Math.round(transitionTime * 100 * 0.68));
  }

  public void stopAmbilightProcess() throws Exception {
    captureLoop.cancel();
    captureLoop.purge();

    ambilightProcessIsActive = false;

    Main.ui.setupOnOffButton();

    Main.ui.button_Stop.setEnabled(false);
    Main.ui.button_Start.setEnabled(true);

    Thread.sleep(250);
    SyncProcess.setStandbyOutput();

    if (Settings.getBoolean("restorelight")) {
      Thread.sleep(750);
      for (PHLight light : PHBridge.lights) {
        light.restoreLightColour();
      }
    }
  }


  public void turnAllLightsOn() throws Exception {
    for (PHLight light : PHBridge.lights) {
      if (Settings.Light.getActive(light)) {
        light.turnOn();
      }
    }
    Main.ui.setupOnOffButton();
  }

  public void turnAllLightsOff() throws Exception {
    for (PHLight light : PHBridge.lights) {
      if (Settings.Light.getActive(light)) {
        light.turnOff();
      }
    }
    Main.ui.setupOnOffButton();
  }

}
