package com.huseyina.project.hueambilight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;


public class Settings {
  private static Preferences prefs = Preferences.userRoot().node("/hueambilight");

  public static SettingsLight Light = new SettingsLight();
  public static SettingsBridge Bridge = new SettingsBridge();

  public static void check() throws Exception {
    if (prefs.node("/hueambilight").keys().length != 0) {
      ArrayList<String> keys = new ArrayList<String>(Arrays.asList(prefs.keys()));
      String[] settingList =
          {"ui_x", "ui_y", "cpi_x", "cpi_y", "oi_x", "oi_y", "chunks", "brightness", "saturation",
              "format", "colorgrid", "restorelight", "autoswitch", "gammacorrection"};

      ArrayList<String> settings = new ArrayList<String>(Arrays.asList(settingList));

      if (keys.containsAll(settings) == false) {
        System.out.println("INFO: " + "some settings are missing");
        setDefaultSettings();
      }
    } else {
      setDefaultSettings();
    }
  }

  public static void setDefaultSettings() {
    System.out.println("Default settings set");
    prefs.putInt("ui_x", 250);
    prefs.putInt("ui_y", 200);
    prefs.putInt("cpi_x", 600);
    prefs.putInt("cpi_y", 200);
    prefs.putInt("oi_x", 250);
    prefs.putInt("oi_y", 450);
    prefs.putInt("chunks", 850);
    prefs.putInt("brightness", 100);
    prefs.putInt("saturation", 110);
    prefs.putInt("format", 0);
    prefs.putBoolean("colorgrid", false);
    prefs.putBoolean("restorelight", true);
    prefs.putBoolean("autoswitch", false);
    prefs.putBoolean("gammacorrection", true);
  }

  public static void reset(boolean exit) throws Exception // delete all settings and exit the
                                                          // program
  {
    System.out.println("reset all settings");
    prefs.node("/hueambilight").removeNode();
    if (exit == true) {
      System.exit(0);
    }
  }

  public static ArrayList<String> getArguments() {
    String args = prefs.get("arguments", null);

    ArrayList<String> arrArgs = new ArrayList<String>();
    if (args != null) {
      arrArgs.addAll(Arrays.asList(args.split(",")));
    }

    return arrArgs;
  }

  public static void setArguments(ArrayList<String> args) {
    if (args.size() != 0) {
      String arguments = "";
      for (String arg : args) {
        arguments += "," + arg;
      }
      arguments = arguments.replaceFirst(",", "");
      prefs.put("arguments", arguments);
    } else {
      prefs.remove("arguments");
    }
  }

  public static int getInteger(String key) {
    return prefs.getInt(key, 0);
  }

  public static boolean getBoolean(String key) {
    return prefs.getBoolean(key, false);
  }

  public static void set(String key, int value) {
    prefs.putInt(key, value);
  }

  public static void set(String key, boolean value) {
    prefs.putBoolean(key, value);
  }
}


class SettingsBridge // bridge settings
{
  private Preferences prefs = Preferences.userRoot().node("/hueambilight/bridge");


  public void setInternalipaddress(String internalipaddress) {
    prefs.put("internalipaddress", internalipaddress);
  }

  public String getInternalipaddress() {
    return prefs.get("internalipaddress", null);
  }
  public void setUsername(String username) {
    prefs.put("username", username);
  }

  public String getUsername() {
    return prefs.get("username", null);
  }
}
