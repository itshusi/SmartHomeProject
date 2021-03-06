package com.huseyina.project.hueambilight;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class SyncProcess {

  public static Rectangle imageSize = null;
  public static int chunksNumX;
  public static int chunksNumY;

  static {
    setSettings();
  }

  public static Rectangle setImgBounds() throws Exception {
    // convert byte array back to BufferedImage
    byte[] imageInByte = ImageCaptureObject.getImage();
    InputStream in = new ByteArrayInputStream(imageInByte);
    BufferedImage image = ImageIO.read(in);
    Rectangle imgSize =
        new Rectangle(image.getMinX(), image.getMinY(), image.getWidth(), image.getHeight());
    return imgSize;
  }

  public static void setStandbyOutput() throws Exception {
    setSettings();
    Main.ui.cpi.setStandbyIcon(imageSize, chunksNumX, chunksNumY);
  }

  private static void setSettings() {
    // setup screen area
    try {
      imageSize = setImgBounds();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // calculate number of chunks
    double chunks = Settings.getInteger("chunks");
    chunks = 3 + 0.35 * Math.pow(chunks, 1.4);
    chunksNumX = (int) Math.round(chunks);
    chunksNumY =
        (int) Math.round(((double) imageSize.height / (double) imageSize.width) * chunksNumX);
    // round -> exact | ceil (round up) -> less options, always transverse | nothing (round down) ->
    // imprecise
  }

  public static void execute() throws Exception // execute the process to get ambilight colours
                                                // based on the captured area
  {
    applyChanges();
    capture();
  }

  private static int lFormat = Settings.getInteger("format");
  private static int lChunks = Settings.getInteger("chunks");
  private static boolean forceStandbyColourGrid = false;

  private static void applyChanges() throws Exception // apply changed settings
  {
    forceStandbyColourGrid = false;
    if (Settings.getInteger("format") != lFormat || Settings.getInteger("chunks") != lChunks) {
      lFormat = Settings.getInteger("format");
      lChunks = Settings.getInteger("chunks");
      setSettings();
      Main.ui.cpi.setStandbyIcon(imageSize, chunksNumX, chunksNumY);
      forceStandbyColourGrid = true;
    }
  }

  private static void capture() throws Exception // capture a selected image
  {
    // convert byte array back to BufferedImage
    byte[] imageInByte = ImageCaptureObject.getImage();
    InputStream in = new ByteArrayInputStream(imageInByte);
    BufferedImage img = ImageIO.read(in);
    chunking(img);
  }

  private static void chunking(BufferedImage img) throws Exception // split the image in several
                                                                   // chunks
  {
    int chunkResX = img.getWidth() / chunksNumX;
    int chunkResY = img.getHeight() / chunksNumY;

    BufferedImage[] chunks = new BufferedImage[chunksNumX * chunksNumY];

    int id = 0;
    for (int x = 0; x < chunksNumX; x++) {
      for (int y = 0; y < chunksNumY; y++) {
        chunks[id] = img.getSubimage(chunkResX * x, chunkResY * y, chunkResX, chunkResY);
        id++;
      }
    }

    avgChunks(chunks);
  }

  private static void avgChunks(BufferedImage[] chunks) throws Exception // get average colour of
                                                                         // each chunk
  {
    Color[] avgColours = new Color[chunks.length];

    for (int i = 0; i < chunks.length; i++) {
      int avgR = 0;
      int avgG = 0;
      int avgB = 0;

      for (int x = 0; x < chunks[i].getWidth(); x++) {
        for (int y = 0; y < chunks[i].getHeight(); y++) {
          Color pColour = new Color(chunks[i].getRGB(x, y));
          avgR += pColour.getRed();
          avgG += pColour.getGreen();
          avgB += pColour.getBlue();
        }
      }

      avgR = avgR / (chunks[i].getWidth() * chunks[i].getHeight());
      avgG = avgG / (chunks[i].getWidth() * chunks[i].getHeight());
      avgB = avgB / (chunks[i].getWidth() * chunks[i].getHeight());
      avgColours[i] = new Color(avgR, avgG, avgB);
    }

    if (Main.ui.cpi.frame.isVisible()) {
      drawColourGrid(avgColours);
    }

    analyse(avgColours);
  }

  private static void drawColourGrid(Color[] ColourContainer) // draw the chunks in the colour grid
  // interface
  {
    if (forceStandbyColourGrid == false) {
      int ChunkResX = (int) (imageSize.getWidth() / 3) / chunksNumX;
      int ChunkResY = (int) (imageSize.getHeight() / 3) / chunksNumY;

      BufferedImage b = new BufferedImage(ChunkResX * chunksNumX, ChunkResY * chunksNumY,
          BufferedImage.TYPE_INT_RGB);
      Graphics g = b.createGraphics();

      int i = 0;
      for (int x = 0; x < chunksNumX; x++) {
        for (int y = 0; y < chunksNumY; y++) {
          g.setColor(ColourContainer[i]);
          g.drawRect(x * ChunkResX, y * ChunkResY, ChunkResX, ChunkResY);
          g.fillRect(x * ChunkResX, y * ChunkResY, ChunkResX, ChunkResY);
          i++;
        }
      }

      Main.ui.cpi.label_Colours.setIcon(new ImageIcon(b));
      Main.ui.cpi.frame.pack();
    }
  }

  private static void analyse(Color[] colourcontainer) throws Exception // analyse all chunks
  {
    float[] temp_colour;

    float[] avg_colour = Color.RGBtoHSB(0, 0, 0, null);

    float minSat = 1;
    float maxSat = 0;
    float minBri = 1;
    float maxBri = 0;

    int[] avg_rgb = new int[3];
    for (Color colour : colourcontainer) // get average colour
    {
      temp_colour = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);

      if (temp_colour[1] <= minSat) {
        minSat = temp_colour[1];
      }
      if (temp_colour[1] >= maxSat) {
        maxSat = temp_colour[1];
      }
      if (temp_colour[2] <= minBri) {
        minBri = temp_colour[2];
      }
      if (temp_colour[2] >= maxBri) {
        maxBri = temp_colour[2];
      }

      avg_rgb[0] += colour.getRed();
      avg_rgb[1] += colour.getGreen();
      avg_rgb[2] += colour.getBlue();
    }
    avg_rgb[0] = avg_rgb[0] / colourcontainer.length;
    avg_rgb[1] = avg_rgb[1] / colourcontainer.length;
    avg_rgb[2] = avg_rgb[2] / colourcontainer.length;
    avg_colour = Color.RGBtoHSB(avg_rgb[0], avg_rgb[1], avg_rgb[2], null);


    Color[] extrColour = new Color[1];
    extrColour[0] = Color.getHSBColor(avg_colour[0], avg_colour[1], avg_colour[2]);

    setLightColour(extrColour);
  }

  private static void setLightColour(Color[] extrColour) throws Exception // distribute the colours
                                                                          // to
  // the lights
  {
    for (PHLight light : PHBridge.lights) {
      boolean active = Settings.Light.getActive(light);

      if (active) {
        Main.hueControl.setLight(light, extrColour[0]);
      }
    }
  }
}
