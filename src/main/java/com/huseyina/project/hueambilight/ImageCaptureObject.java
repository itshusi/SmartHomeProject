package com.huseyina.project.hueambilight;

public class ImageCaptureObject {

  private static byte[] image;

  private static final ImageCaptureObject instance = new ImageCaptureObject();

  private ImageCaptureObject() {
    // do nothing
  }

  public ImageCaptureObject(byte[] image) {
    ImageCaptureObject.setImage(image);
  }

  public static ImageCaptureObject getInstance() {
    return instance;
  }

  public static byte[] getImage() {
    return image;
  }

  public static void setImage(byte[] image) {
    ImageCaptureObject.image = image;
  }

}
