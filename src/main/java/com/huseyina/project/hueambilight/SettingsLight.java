package com.huseyina.project.hueambilight;

import java.util.prefs.Preferences;

public class SettingsLight // light settings
{
  private Preferences prefs = Preferences.userRoot().node("/hueambilight/lights");


  public void check(PHLight light) throws Exception // setup default light settings if it doesn't
                                                   // have
  {
    Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
    if (lprefs.get("active", null) == null) {
      lprefs.putBoolean("active", true);
    }
    if (lprefs.get("bri", null) == null) {
      lprefs.putInt("bri", 100);
    }
  }

  public void setBrightness(PHLight light, int bri) {
    Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
    lprefs.putInt("bri", bri);
  }

  public void setActive(PHLight light, boolean active) {
    Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
    lprefs.putBoolean("active", active);
  }

  public void setAlgorithm(PHLight light, int alg) {
    Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
    lprefs.putInt("alg", alg);
  }

  public boolean getActive(PHLight light) {
    Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
    return lprefs.getBoolean("active", true);
  }

  public int getBrightness(PHLight light) {
    Preferences lprefs = Preferences.userRoot().node(prefs.absolutePath() + "/" + light.uniqueid);
    return lprefs.getInt("bri", -1);
  }
}
