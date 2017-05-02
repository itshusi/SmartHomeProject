package com.huseyina.project.hueambilight;

import java.util.ArrayList;
import java.util.Arrays;


public class Main
{	
	public static UserInterface ui;
	public static Control hueControl;
	public static WebcamViewer webcamViewer;
	public static ArrayList<String> arguments = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception
	{
		arguments.addAll(Arrays.asList(args));
		arguments.addAll(Settings.getArguments());
		
		// check program arguments
		if(arguments.contains("debug"))
		{
			Debug.activateDebugging();
		}
		if(arguments.contains("log"))
		{
			Debug.activateLogging();
		}
		if (arguments.contains("reset"))
		{
			Settings.reset(true);
		}

		Debug.info("program arguments", (Object[])arguments.toArray());
		
		Debug.info("program parameters",
				"os: " + System.getProperty("os.name"),
				"java version: " + System.getProperty("java.version"));
		
		Settings.check();
		Settings.debug();
		Settings.Bridge.debug();
		Settings.Light.debug();
		
		Debug.info(null, "hue ambilight started");
		
		webcamViewer = new WebcamViewer();
        ui = new UserInterface();
        ui.mqttTask();
        hueControl = new Control();

	}
	
}
