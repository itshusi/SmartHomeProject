package com.huseyina.project.hueambilight;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.*;


public class HBridge {
  public static String internalipaddress = Settings.Bridge.getInternalipaddress();

  public static final String username = "Cl7Gy44bZqsqP49SeGPD6ZC26eYKavy5UktIBu2N";
  public static final String devicetype = "hueambilight";

  public static ArrayList<HLight> lights = new ArrayList<HLight>();

  public static void setup() throws Exception {
    if (internalipaddress != null) {
      fastConnect();
    } else {
      newConnect();
    }
  }

  public static HLight getLight(int lightID) {
    for (HLight light : lights) {
      if (light.id == lightID) {
        return light;
      }
    }
    return null;
  }

  private static void fastConnect() throws Exception // try to connect to the saved ip
  {
    System.out.println("trying fast connect");

    JsonObject response = HRequest.GET("http://" + internalipaddress + "/api/" + username);

    if (HRequest.responseCheck(response) == "data") {
      System.out.println("fast connect successful");

      getLights();
      Main.ui.loadMainInterface();
    } else {
      System.out.println("can't find bridge");

      newConnect();
    }
  }

  private static void newConnect() throws Exception // find a new bridge
  {
    System.out.println("setup new connection");

    Main.ui.loadConnectionInterface();
    Main.ui.setConnectState(1);

    final Timer timer = new Timer();
    TimerTask addUserLoop = new TimerTask() {
      int tries = 0;

      public void run() {
        try // to get the bridge ip
        {
          JsonObject response = HRequest.GET("https://www.meethue.com/api/nupnp");

          if (response != null) {
            timer.cancel();
            timer.purge();

            internalipaddress = response.get("internalipaddress").getAsString();

            Settings.Bridge.setInternalipaddress(internalipaddress);

            System.out.println("bridge found");

            login();
          }
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }

        if (tries > 6) // abort after serval tries
        {
          try {
            timer.cancel();
            timer.purge();
            Main.ui.setConnectState(4);
            System.out.println("connection to bridge timeout");
          } catch (Exception e) {
            System.out.println("ERROR: " + e);
          }
        }

        tries++;
      }
    };
    timer.scheduleAtFixedRate(addUserLoop, 0, 1500);
  }

  private static void login() throws Exception // try to login
  {
    JsonObject response = HRequest.GET("http://" + internalipaddress + "/api/" + username);
    if (HRequest.responseCheck(response) == "data") {
      System.out.println("login successful");

      getLights();

      Main.ui.setConnectState(2);
    } else if (HRequest.responseCheck(response) == "error") {
      createUser();
    }
  }

  private static void getLights() throws Exception {
    System.out.println("getting lights");
    JsonObject response =
        HRequest.GET("http://" + internalipaddress + "/api/" + username + "/lights/");

    for (int i = 1; i < 50; i++) {
      if (response.has(String.valueOf(i))) {
        JsonObject state = response.getAsJsonObject(String.valueOf(i)).getAsJsonObject("state");
        if (state.has("on") && state.has("hue") && state.has("sat") && state.has("bri")) {
          lights.add(new HLight(i));
        }
      }
    }

    System.out.println(countLights() + " lights found");
  }

  public static int countLights() {
    return lights.size();
  }

  private static void createUser() throws Exception // create a new bridge user
  {
    System.out.println("creating new user");
    Main.ui.setConnectState(3);

    final Timer timer = new Timer();
    TimerTask addUserLoop = new TimerTask() {
      String body = "{\"devicetype\": \"" + devicetype + "\", \"username\": \"" + username + "\"}";
      int tries = 0;

      public void run() {
        try // to register a new bridge user (user must press the link button)
        {
          tries++;
          JsonObject response = HRequest.POST("http://" + internalipaddress + "/api/", body);
          if (HRequest.responseCheck(response) == "success") {
            timer.cancel();
            timer.purge();
            System.out.println("new user created");
            login();
          } else if (tries > 20) // abort after serval tries
          {
            timer.cancel();
            timer.purge();
            Main.ui.setConnectState(4);
            System.out.println("link button not pressed");
          }
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    };
    timer.scheduleAtFixedRate(addUserLoop, 1500, 1500);
  }

}
