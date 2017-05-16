package com.huseyina.project.hueambilight;

import com.google.gson.JsonObject;


public class PHLight {
  public final int id;
  public final String name;
  public final String uniqueid;
  private int[] storedLightColour = new int[3];

  public PHLight(int LightID) throws Exception {
    id = LightID;

    JsonObject response = Request.GET(
        "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username + "/lights/" + id);
    name = response.get("name").getAsString();
    uniqueid = response.get("uniqueid").getAsString();

    Settings.Light.check(this);
  }

  public boolean isOn() throws Exception {
    JsonObject response = Request.GET(
        "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username + "/lights/" + id);

    return response.get("state").getAsJsonObject().get("on").getAsBoolean();
  }

  public void turnOn() throws Exception {
    String APIurl = "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username
        + "/lights/" + id + "/state/";
    String data = "{\"on\": true, \"transitiontime\":4, \"bri\":255}";

    Request.PUT(APIurl, data);
  }

  public void turnOff() throws Exception {
    String APIurl = "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username
        + "/lights/" + id + "/state/";
    String data = "{\"on\": false, \"transitiontime\":4}";

    Request.PUT(APIurl, data);
  }

  public void storeLightColour() throws Exception {
    JsonObject response = Request.GET(
        "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username + "/lights/" + id);

    storedLightColour[0] = response.get("state").getAsJsonObject().get("hue").getAsInt();
    storedLightColour[1] = response.get("state").getAsJsonObject().get("sat").getAsInt();
    storedLightColour[2] = response.get("state").getAsJsonObject().get("bri").getAsInt();
  }

  public void restoreLightColour() throws Exception {
    String APIurl = "http://" + PHBridge.internalipaddress + "/api/" + PHBridge.username
        + "/lights/" + id + "/state/";
    String data = "{\"hue\":" + storedLightColour[0] + ", \"sat\":" + storedLightColour[1]
        + ", \"bri\":" + storedLightColour[2] + ", \"transitiontime\":1}";

    Request.PUT(APIurl, data);
  }
}
