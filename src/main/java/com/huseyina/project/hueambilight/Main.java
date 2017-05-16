package com.huseyina.project.hueambilight;

import java.util.ArrayList;
import java.util.Arrays;


public class Main {
  public static UserInterface ui;
  public static Control hueControl;
  public static WebcamConnector webcamConnect;
  public static ArrayList<String> arguments = new ArrayList<String>();

  public static void main(String[] args) throws Exception {
    arguments.addAll(Arrays.asList(args));
    arguments.addAll(Settings.getArguments());

    // check program arguments
    if (arguments.contains("reset")) {
      Settings.reset(true);
    }

    Settings.check();

    webcamConnect = new WebcamConnector();
    ui = new UserInterface();
    ui.mqttTask();
    hueControl = new Control();

  }

}
