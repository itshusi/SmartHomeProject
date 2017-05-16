package com.huseyina.project.hueambilight;

public class ImageCaptureObject {

  private static byte[] image;

  private static ImageCaptureObject instance = null;

  private ImageCaptureObject() {
    // do nothing
  }

  public ImageCaptureObject(byte[] image) {
    ImageCaptureObject.setImage(image);
  }

  public static ImageCaptureObject getInstance() {
    if (instance == null) {
      instance = new ImageCaptureObject();
    }
    return instance;
  }

  public static byte[] getImage() {
    return image;
  }

  public static void setImage(byte[] image) {
    ImageCaptureObject.image = image;
  }

}
