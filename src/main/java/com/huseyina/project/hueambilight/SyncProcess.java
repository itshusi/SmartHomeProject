package com.huseyina.project.hueambilight;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class SyncProcess
{
    
    private static Rectangle imgSize = null; 
    public static Rectangle captureSize = null; 
    public static int chunksNumX;
    public static int chunksNumY;
    
    static
    {
        setSettings();
    }
    
    public static Rectangle setImgBounds() throws Exception {
      // convert byte array back to BufferedImage
      byte[] imageInByte = ImageCaptureObject.getImage();
      InputStream in = new ByteArrayInputStream(imageInByte);
      BufferedImage image = ImageIO.read(in);
      Rectangle imgSize = new Rectangle(image.getMinX(),image.getMinY(),image.getWidth(), image.getHeight());
      return imgSize;
    }
    public static void setStandbyOutput() throws Exception
    {
        setSettings();
        Main.ui.cpi.setStandbyIcon(captureSize, chunksNumX, chunksNumY);
    }
    
    private static void setSettings()
    {       
        // setup screen area
        double ratio;
        int x, y, w, h;
        try {
          imgSize = setImgBounds();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

                ratio = 1.0;
                w = (int)imgSize.getWidth();
                h = (int)imgSize.getHeight();
                x = 0;
                y = 0;
               
        captureSize = new Rectangle((int)imgSize.getX() + x, (int)imgSize.getY() + y, w, h);
        
        // calculate number of chunks
        double chunks = Settings.getInteger("chunks");
        chunks = 3 + 0.35 * Math.pow(chunks, 1.4);
        chunksNumX = (int) Math.round(chunks);
        chunksNumY = (int) Math.round(((double)captureSize.height / (double)captureSize.width) * chunksNumX); 
        //round -> exact | ceil (round up) -> less options, always transverse | nothing (round down) -> imprecise
    }
    
    public static void execute() throws Exception // execute the process to get ambilight colours based on the captured area
    {
        applyChanges();
        capture();
    }
    
    private static int lFormat = Settings.getInteger("format");
    private static int lChunks = Settings.getInteger("chunks");
    private static int lScreen = Settings.getInteger("screen");
    private static boolean forceStandbyColorGrid = false;
    private static void applyChanges() throws Exception // apply changed settings
    {
        forceStandbyColorGrid = false;
        if (Settings.getInteger("format") != lFormat || Settings.getInteger("chunks") != lChunks || Settings.getInteger("screen") != lScreen)
        {
            lFormat = Settings.getInteger("format");
            lChunks = Settings.getInteger("chunks");
            lScreen = Settings.getInteger("screen");
            setSettings();
            Main.ui.cpi.setStandbyIcon(captureSize, chunksNumX, chunksNumY);
            forceStandbyColorGrid = true;
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
    
    private static void chunking(BufferedImage img) throws Exception // split the image in several chunks
    {       
        int chunkResX = img.getWidth() / chunksNumX;
        int chunkResY = img.getHeight() / chunksNumY;
        
        BufferedImage[] chunks = new BufferedImage[chunksNumX * chunksNumY];
        
        int id = 0;
        for (int x = 0; x < chunksNumX; x++)
        {
            for (int y = 0; y < chunksNumY; y++)
            {
                chunks[id] = img.getSubimage(chunkResX * x, chunkResY * y, chunkResX, chunkResY);
                id++;
            }
        }
        
        avgChunks(chunks);
    }
    
    private static void avgChunks(BufferedImage[] chunks) throws Exception // get average color of each chunk
    {
        Color[] avgColors = new Color[chunks.length];
        
        for (int i = 0; i < chunks.length; i++)
        {
            int avgR = 0;
            int avgG = 0;
            int avgB = 0;
            
            for (int x = 0; x < chunks[i].getWidth(); x++)
            {
                for (int y = 0; y < chunks[i].getHeight(); y++)
                {
                    Color pColor = new Color(chunks[i].getRGB(x, y));
                    avgR += pColor.getRed();
                    avgG += pColor.getGreen();
                    avgB += pColor.getBlue();
                }
            }
            
            avgR = avgR / (chunks[i].getWidth() * chunks[i].getHeight());
            avgG = avgG / (chunks[i].getWidth() * chunks[i].getHeight());
            avgB = avgB / (chunks[i].getWidth() * chunks[i].getHeight());
            avgColors[i] = new Color(avgR, avgG, avgB);
        }
        
        if (Main.ui.cpi.frame.isVisible())
        {
            drawColorGrid(avgColors);
        }
        
        analyse(avgColors);
    }
    
    private static void drawColorGrid(Color[] ColorContainer) // draw the chunks in the color grid interface
    {
        if (forceStandbyColorGrid == false)
        {
            int ChunkResX = (int)(captureSize.getWidth() / 3) / chunksNumX;
            int ChunkResY = (int)(captureSize.getHeight() / 3) / chunksNumY;
            
            BufferedImage b = new BufferedImage(ChunkResX * chunksNumX, ChunkResY * chunksNumY, BufferedImage.TYPE_INT_RGB);
            Graphics g = b.createGraphics();
            
            int i = 0;
            for (int x = 0; x < chunksNumX; x++)
            {
                for (int y = 0; y < chunksNumY; y++)
                {
                    g.setColor(ColorContainer[i]);
                    g.drawRect(x * ChunkResX, y * ChunkResY, ChunkResX, ChunkResY);
                    g.fillRect(x * ChunkResX, y * ChunkResY, ChunkResX, ChunkResY);
                    i++;
                }
            }

            Main.ui.cpi.label_Colors.setIcon(new ImageIcon(b));
            Main.ui.cpi.frame.pack();
        }
    }

    private static void analyse(Color[] colorcontainer) throws Exception // analyse all chunks
    {
        float[] temp_color;
        
        float[] avg_color = Color.RGBtoHSB(0, 0, 0, null);
        
        float minSat = 1;
        float maxSat = 0;
        float minBri = 1;
        float maxBri = 0;
        
        int[] avg_rgb = new int[3];
        for (Color color : colorcontainer) // get average color
        {
            temp_color = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            
            if (temp_color[1] <= minSat)
            {
                minSat = temp_color[1];
            }
            if (temp_color[1] >= maxSat)
            {
                maxSat = temp_color[1];
            }
            if (temp_color[2] <= minBri)
            {
                minBri = temp_color[2];
            }
            if (temp_color[2] >= maxBri)
            {
                maxBri = temp_color[2];
            }
            
            avg_rgb[0] += color.getRed();
            avg_rgb[1] += color.getGreen();
            avg_rgb[2] += color.getBlue();
        }       
        avg_rgb[0] = avg_rgb[0] / colorcontainer.length;
        avg_rgb[1] = avg_rgb[1] / colorcontainer.length;
        avg_rgb[2] = avg_rgb[2] / colorcontainer.length;
        avg_color = Color.RGBtoHSB(avg_rgb[0], avg_rgb[1], avg_rgb[2], null);


        Color[] extrColor = new Color[1];
        extrColor[0] = Color.getHSBColor(avg_color[0], avg_color[1], avg_color[2]);
        
        setLightColor(extrColor);
    }
    
    private static void setLightColor(Color[] extrColor) throws Exception // distribute the colors to the lights
    {
        for (HLight light : HBridge.lights)
        {
            boolean active = Settings.Light.getActive(light);
            
            if(active)
            {
                Main.hueControl.setLight(light, extrColor[0]);
            }
        }
    }
}
