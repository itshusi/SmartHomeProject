package com.huseyina.project.hueambilight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class SettingsLight // light settings
{
	private Preferences prefs = Preferences.userRoot().node("/hueambilight/lights");
	
	private int nexAlg = 0;
	private int maxAlg = SyncProcess.algorithms;
	
	public void check(HLight light) throws Exception // setup default light settings if it doesn't have
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		if (lprefs.get("active", null) == null)
		{
			lprefs.putBoolean("active", true);
		}
		if (lprefs.get("bri", null) == null)
		{
			lprefs.putInt("bri", 100);
		}
		if (lprefs.get("alg", null) == null)
		{
			lprefs.putInt("alg", nexAlg);
			nexAlg++;
			if (nexAlg > maxAlg)
			{
				nexAlg = 0;
			}
		}
	}
	
	public void debug() throws Exception
	{
		ArrayList<String> settings = new ArrayList<String>();
		for (String node : prefs.childrenNames())
		{	
			settings.add(node + "");
			String[] keys = prefs.node(node).keys();
			Arrays.sort(keys);
			for (String s : keys)
			{
				settings.add("  " + s + " = " + prefs.node(node).get(s, null));
			}
		}
		Debug.info("settings lights", settings);
	}
	
	public void setBrightness(HLight light, int bri)
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		lprefs.putInt("bri", bri);
	}
	public void setActive(HLight light, boolean active)
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		lprefs.putBoolean("active", active);
	}
	public void setAlgorithm(HLight light, int alg)
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		lprefs.putInt("alg", alg);
	}
	
	public boolean getActive(HLight light)
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		return lprefs.getBoolean("active", true);
	}
	public int getAlgorithm(HLight light)
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		return lprefs.getInt("alg", -1);
	}
	public int getBrightness(HLight light)
	{
		Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
		return lprefs.getInt("bri", -1);
	}
}
