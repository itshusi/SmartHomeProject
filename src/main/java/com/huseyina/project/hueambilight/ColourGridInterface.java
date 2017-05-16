package com.huseyina.project.hueambilight;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class ColourGridInterface {
  public JFrame frame;
  private JPanel contentpane;
  public JLabel label_Colours;

  public ColourGridInterface() {
    initialize();
  }

  private void initialize() {
    frame = new JFrame();
    frame.setTitle("Colour Grid");
    frame.setResizable(false);
    frame.setBounds(100, 100, 640, 360);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.setLocation(Settings.getInteger("cpi_x"), Settings.getInteger("cpi_y"));

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        Main.ui.checkbox_ShowColourGrid.setSelected(false);
        Settings.set("colourgrid", false);
        hide();
      }
    });
    frame.setResizable(false);
    contentpane = new JPanel();
    contentpane.setBackground(Color.black);
    contentpane.setBorder(null);
    contentpane.setLayout(new GridLayout(0, 1, 0, 0));
    frame.setContentPane(contentpane);

    label_Colours = new JLabel();
    contentpane.add(label_Colours);

    frame.setVisible(false);
  }

  public void show() throws Exception {
    frame.setVisible(true);
    frame.setLocation(Settings.getInteger("cpi_x"), Settings.getInteger("cpi_y"));
    SyncProcess.setStandbyOutput();
  }

  public void hide() {
    frame.setVisible(false);
    Settings.set("cpi_x", frame.getX());
    Settings.set("cpi_y", frame.getY());
  }

  public void setStandbyIcon(Rectangle CaptureSize, int ChunksNumX, int ChunksNumY) throws Exception // set
                                                                                                     // a
                                                                                                     // default
                                                                                                     // image
                                                                                                     // to
                                                                                                     // illustrate
                                                                                                     // the
                                                                                                     // the
                                                                                                     // chunks
                                                                                                     // amount
  {
    int ChunkResX = (int) (SyncProcess.imageSize.getWidth() / 3) / SyncProcess.chunksNumX;
    int ChunkResY = (int) (SyncProcess.imageSize.getHeight() / 3) / SyncProcess.chunksNumY;

    BufferedImage b = new BufferedImage(ChunkResX * SyncProcess.chunksNumX,
        ChunkResY * SyncProcess.chunksNumY, BufferedImage.TYPE_INT_RGB);
    Graphics g = b.createGraphics();

    // create a raster based on number of chunks
    Color colour = Color.gray;
    for (int x = 0; x < SyncProcess.chunksNumX; x++) {
      for (int y = 0; y < SyncProcess.chunksNumY; y++) {
        if (x % 2 == y % 2)
          colour = Color.gray;
        else
          colour = Color.darkGray;

        g.setColor(colour);
        g.drawRect(x * ChunkResX, y * ChunkResY, ChunkResX, ChunkResY);
        g.fillRect(x * ChunkResX, y * ChunkResY, ChunkResX, ChunkResY);
      }
    }

    label_Colours.setIcon(new ImageIcon(b));

    frame.pack();
    frame.pack(); // i dont know why, but it is needed to function properly
  }
}
