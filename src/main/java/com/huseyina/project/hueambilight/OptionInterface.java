package com.huseyina.project.hueambilight;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;


public class OptionInterface {
  private JFrame frame;
  private JCheckBox checkbox_AutoTurnOff;
  private JCheckBox checkbox_UseGammaCorrection;
  private JPanel panel_Lights;

  public OptionInterface() {
    Main.ui.setEnabled(false);
    initialize();
    getOptions();
  }

  private void initialize() {
    frame = new JFrame();
    frame.setMinimumSize(new Dimension(460, 500));
    frame.setResizable(false);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        Settings.set("oi_x", frame.getX());
        Settings.set("oi_y", frame.getY());
        Main.ui.setEnabled(true);
      }
    });
    frame.setTitle("options");
    frame.setBounds(100, 100, 460, 500);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    frame.setLocation(Settings.getInteger("oi_x"), Settings.getInteger("oi_y"));
    frame.getContentPane()
        .setLayout(new FormLayout(
            new ColumnSpec[] {FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("left:20dlu:grow"),
                ColumnSpec.decode("left:20dlu:grow"), ColumnSpec.decode("left:20dlu:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,},
            new RowSpec[] {FormSpecs.RELATED_GAP_ROWSPEC, RowSpec.decode("16dlu"),
                FormSpecs.LINE_GAP_ROWSPEC, RowSpec.decode("16dlu"), RowSpec.decode("10dlu"),
                RowSpec.decode("16dlu"), RowSpec.decode("10dlu"), RowSpec.decode("162dlu"),
                RowSpec.decode("10dlu"), RowSpec.decode("16dlu"), FormSpecs.LINE_GAP_ROWSPEC,
                RowSpec.decode("16dlu"), RowSpec.decode("10dlu"), RowSpec.decode("bottom:16dlu"),
                FormSpecs.RELATED_GAP_ROWSPEC,}));

    JLabel label_LightOptions = new JLabel("light options:");
    label_LightOptions.setEnabled(false);
    label_LightOptions.setFont(new Font("Tahoma", Font.PLAIN, 14));
    frame.getContentPane().add(label_LightOptions, "2, 2, 1, 3, center, default");

    checkbox_AutoTurnOff = new JCheckBox("   auto. turn off lights");
    checkbox_AutoTurnOff
        .setToolTipText("turns the lights automatically off when the screen is near black");
    frame.getContentPane().add(checkbox_AutoTurnOff, "3, 2, 2, 1, left, center");

    checkbox_UseGammaCorrection = new JCheckBox("   use gamma correction");
    checkbox_UseGammaCorrection
        .setToolTipText("makes the color more like the color on your screen");
    frame.getContentPane().add(checkbox_UseGammaCorrection, "3, 4, 2, 1, left, center");

    JSeparator separator_1 = new JSeparator();
    frame.getContentPane().add(separator_1, "2, 5, 3, 1, fill, center");

    JButton button_Ok = new JButton("ok");
    button_Ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveOptions();
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
      }
    });


    JButton button_Cancel = new JButton("cancel");
    button_Cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        frame.dispose();
      }
    });
    frame.getContentPane().add(button_Cancel, "3, 14, fill, fill");

    JButton button_Apply = new JButton("apply");
    button_Apply.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        saveOptions();
      }
    });
    frame.getContentPane().add(button_Apply, "4, 14, fill, fill");

    JSeparator separator_2 = new JSeparator();
    frame.getContentPane().add(separator_2, "2, 7, 3, 1, fill, center");

    JScrollPane scrollpane = new JScrollPane();
    scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    frame.getContentPane().add(scrollpane, "2, 8, 3, 1, fill, fill");


    // setup list with options for all lights

    panel_Lights = new JPanel();
    scrollpane.setViewportView(panel_Lights);
    int rows = HBridge.countLights();
    if (rows < 6) {
      rows = 6;
    }
    panel_Lights.setLayout(new GridLayout(rows, 1, 5, 7));

    JLabel lblActiveNameColor = new JLabel(
        "   active         name                                                  brightness\r\n");
    scrollpane.setColumnHeaderView(lblActiveNameColor);

    // create the list
    for (final HLight light : HBridge.lights) {
      final JPanel panel_options = new JPanel();
      panel_Lights.add(panel_options, HBridge.lights.indexOf(light));

      JLabel label_Name = new JLabel(light.name);
      label_Name.setPreferredSize(new Dimension(110, 15));

      JPanel panel_Brightness = new JPanel();
      panel_Brightness.setLayout(new FormLayout(
          new ColumnSpec[] {ColumnSpec.decode("fill:93px"), ColumnSpec.decode("right:29px:grow"),},
          new RowSpec[] {RowSpec.decode("24px"),}));

      final JSlider slider_Brightness = new JSlider();
      slider_Brightness.setSnapToTicks(true);
      slider_Brightness.setMinorTickSpacing(5);
      slider_Brightness.setMinimum(10);
      slider_Brightness.setMaximum(100);
      slider_Brightness.setValue(Settings.Light.getBrightness(light));
      panel_Brightness.add(slider_Brightness, "1, 1, center, center");

      final JLabel label_Brightness = new JLabel("100%");
      label_Brightness.setText(slider_Brightness.getValue() + "%");
      label_Brightness.setFont(new Font("Tahoma", Font.PLAIN, 10));
      panel_Brightness.add(label_Brightness, "2, 1, right, center");
      slider_Brightness.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent arg0) {
          label_Brightness.setText(slider_Brightness.getValue() + "%");
        }
      });

      final JCheckBox checkbox_Active = new JCheckBox();
      checkbox_Active.setSelected(Settings.Light.getActive(light));
      checkbox_Active
          .setToolTipText("allow the program to change this lights color and brightness");
      if (checkbox_Active.isSelected() == false) {
        label_Name.setEnabled(false);
        slider_Brightness.setEnabled(false);
        label_Brightness.setEnabled(false);
      }
      checkbox_Active.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          try {
            if (checkbox_Active.isSelected()) {
              panel_options.getComponent(1).setEnabled(true);
              panel_options.getComponent(2).setEnabled(true);
              JPanel panel = (JPanel) panel_options.getComponent(3);
              panel.getComponent(0).setEnabled(true);
              panel.getComponent(1).setEnabled(true);
            } else {
              panel_options.getComponent(1).setEnabled(false);
              panel_options.getComponent(2).setEnabled(false);
              panel_options.getComponent(3).setEnabled(false);
              JPanel panel = (JPanel) panel_options.getComponent(3);
              panel.getComponent(0).setEnabled(false);
              panel.getComponent(1).setEnabled(false);
            }
          } catch (Exception e) {
            System.out.println("ERROR: " + e);
          }
        }
      });

      FlowLayout flowlayout_options = new FlowLayout(FlowLayout.LEFT, 12, 4);
      panel_options.setLayout(flowlayout_options);
      panel_options.add(checkbox_Active, 0);
      panel_options.add(label_Name, 1);
      panel_options.add(panel_Brightness, 2);
    }

    frame.pack();
    frame.setVisible(true);
  }

  private void getOptions() // get saved options and setup window elements
  {
    checkbox_AutoTurnOff.setSelected(Settings.getBoolean("autoswitch"));
    checkbox_UseGammaCorrection.setSelected(Settings.getBoolean("gammacorrection"));

  }

  private void saveOptions() // save all settings
  {
    Settings.set("autoswitch", checkbox_AutoTurnOff.isSelected());
    Settings.set("gammacorrection", checkbox_UseGammaCorrection.isSelected());

    for (HLight light : HBridge.lights) {
      JPanel panel_Light = (JPanel) panel_Lights.getComponent(HBridge.lights.indexOf(light));

      JCheckBox checkbox_Active = (JCheckBox) panel_Light.getComponent(0);
      Settings.Light.setActive(light, checkbox_Active.isSelected());

      Settings.Light.setAlgorithm(light, 0);

      JPanel panel_Brightness = (JPanel) panel_Light.getComponent(2);
      JSlider slider_Brightness = (JSlider) panel_Brightness.getComponent(0);
      Settings.Light.setBrightness(light, slider_Brightness.getValue());
    }

    try {
      Main.ui.setupOnOffButton();
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
  }
}
