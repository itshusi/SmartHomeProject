package com.huseyina.project.hueambilight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class UserInterface extends JFrame implements MqttCallback {
  private static final long serialVersionUID = -3678846489480645029L;
  private JFrame frame;
  private JLabel labelConnect;
  public JButton button_Stop;
  public JButton button_Start;
  public JCheckBox checkbox_ShowColourGrid;
  public JSlider slider_Brightness;
  private JPanel panel_Brightness;
  private JLabel label_BrightnessPercentage;
  public JCheckBox checkbox_RestoreLight;
  public JButton button_On;
  public JButton button_Off;
  public JSlider sSetChunks;
  private JPanel panel_Chunks;
  private JLabel label_ChunksNumber;
  private JMenuBar menubar;
  private JPanel panel;
  private JMenu menu_Settings;
  private JMenuItem menuitem_Options;
  private JMenuItem menuitem_Reset;
  private Component rigidarea;
  public ColourGridInterface cpi = new ColourGridInterface();
  private JLabel label_Saturation;
  private JPanel panel_Saturation;
  public JSlider slider_Saturation;
  private JLabel label_SaturationPercentage;

  public UserInterface() throws Exception {
    setLookAndFeel();
    initialize();
  }

  public void setEnabled(Boolean b) {
    setEnabled(b);
  }

  private void setLookAndFeel() // set style - Required to look identical on each platform
  {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      UIManager.put("Label.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("Button.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("MenuBar.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("MenuItem.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("Panel.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("ToggleButton.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("RadioButton.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("CheckBox.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("ColourChooser.font", new FontUIResource("Dialog.plain", Font.PLAIN, 12));
      UIManager.put("ComboBox.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("List.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("RadioButtonMenuItem.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("CheckBoxMenuItem.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("Menu.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("PopupMenu.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("OptionPane.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("ProgressBar.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("ScrollPane.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("Viewport.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("TabbedPane.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("Table.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("TableHeader.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("TextField.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("PasswordField.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("TextArea.font", new FontUIResource("Monospaced.plain", Font.PLAIN, 13));
      UIManager.put("TextPane.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("EditorPane.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("TitledBorder.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
      UIManager.put("ToolBar.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("ToolTip.font", new FontUIResource("Segoe UI", Font.PLAIN, 12));
      UIManager.put("Tree.font", new FontUIResource("Tahoma", Font.PLAIN, 11));
    } catch (Exception e) {
      System.out.println("ERROR: " + e);
    }
  }

  private void initialize() throws Exception // pre init user interface
  {
    // frame = new JFrame();
    getContentPane().setBackground(Color.WHITE);
    setResizable(false);
    setTitle("Hue Light Sync");
    setBounds(100, 100, 243, 260);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocation(Settings.getInteger("ui_x"), Settings.getInteger("ui_y"));
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent arg0) {
        Settings.set("ui_x", getX());
        Settings.set("ui_y", getY());
        cpi.hide();
      }
    });

    System.out.println("interface initialized");

    // loadMainInterface(); // uncomment to edit MainInterface in Window Builder
    // loadConnectionInterface(); // uncomment to edit ConnectionInterface in Window Builder
  }

  public void loadConnectionInterface() throws Exception // load the connection interface
  {
    getContentPane().setLayout(new BorderLayout(0, 0));
    labelConnect = new JLabel("");
    labelConnect.setHorizontalAlignment(SwingConstants.CENTER);
    getContentPane().add(labelConnect);
    setVisible(true);
    setConnectState(0);

    System.out.println("connection-interface loaded");
  }

  public void setConnectState(int state) throws Exception // set different visual output
  {
    switch (state) {
      case 0: // blank
        labelConnect.setIcon(null);
        break;
      case 1: // search and connect
        labelConnect// .setText("Connect Hue!");
            .setIcon(new ImageIcon(this.getClass().getResource("/hue_connect.gif")));
        Thread.sleep(1500);
        break;
      case 2: // successfully connected
        labelConnect// .setText("Hue Connected!");
            .setIcon(new ImageIcon(this.getClass().getResource("/hue_connected.png")));
        Thread.sleep(500);
        loadMainInterface();
        break;
      case 3: // press link button
        labelConnect// .setText("Press Link Button!");
            .setIcon(new ImageIcon(this.getClass().getResource("/hue_presslinkbutton.gif")));
        break;
      case 4: // timeout
        labelConnect// .setText("Hue Timeout!");
            .setIcon(new ImageIcon(this.getClass().getResource("/hue_timeout.png")));
        break;
    }
  }

  public void loadMainInterface() throws Exception // load the main user interface
  {
    // setup window
    getContentPane().removeAll();
    getContentPane().setBackground(new Color(240, 240, 240));

    getContentPane().setLayout(new FormLayout(
        new ColumnSpec[] {ColumnSpec.decode("10dlu:grow(2)"), ColumnSpec.decode("5dlu:grow"),
            ColumnSpec.decode("5dlu:grow"), ColumnSpec.decode("10dlu:grow(2)"),},
        new RowSpec[] {RowSpec.decode("24px:grow"), RowSpec.decode("24px:grow"),
            RowSpec.decode("24px:grow"), RowSpec.decode("24px:grow"), RowSpec.decode("24px:grow"),
            RowSpec.decode("24px:grow"), RowSpec.decode("24px:grow"),
            RowSpec.decode("24px:grow"),}));

    // Button ON
    button_On = new JButton("ON");
    button_On.setToolTipText("turn all lights on");
    button_On.setEnabled(false);
    button_On.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          Main.hueControl.turnAllLightsOn();
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    });
    getContentPane().add(button_On, "1, 1, 2, 1, fill, center");

    // Button OFF
    button_Off = new JButton("OFF");
    button_Off.setToolTipText("turn all lights off");
    button_Off.setEnabled(false);
    button_Off.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          Main.hueControl.turnAllLightsOff();
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    });
    getContentPane().add(button_Off, "3, 1, 2, 1, fill, center");

    // Setup On/Off Buttons
    setupOnOffButton();

    // CheckBox to show the colour grid (debug feature)
    checkbox_ShowColourGrid = new JCheckBox("   show colour grid");
    checkbox_ShowColourGrid.setToolTipText("show the colour/chunks grid");
    checkbox_ShowColourGrid.setSelected(Settings.getBoolean("colourgrid"));
    if (checkbox_ShowColourGrid.isSelected() == true) {
      cpi.show();
    } else if (checkbox_ShowColourGrid.isSelected() == false) {
      cpi.hide();
    }
    checkbox_ShowColourGrid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          Settings.set("colourgrid", checkbox_ShowColourGrid.isSelected());
          if (checkbox_ShowColourGrid.isSelected() == true) {
            cpi.show();
          } else if (checkbox_ShowColourGrid.isSelected() == false) {
            cpi.hide();
          }
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    });

    // Label "chunks"
    JLabel label_Chunks = new JLabel("   chunks");
    getContentPane().add(label_Chunks, "1, 2, left, center");

    // Panel to hold the chunks slider and chunks amount Label
    panel_Chunks = new JPanel();
    panel_Chunks.setLayout(new FormLayout(
        new ColumnSpec[] {ColumnSpec.decode("115px"), ColumnSpec.decode("right:32px:grow"),},
        new RowSpec[] {RowSpec.decode("23px:grow"),}));
    getContentPane().add(panel_Chunks, "2, 2, 3, 1, fill, fill");

    // Label chunks amount
    label_ChunksNumber =
        new JLabel(String.valueOf(SyncProcess.chunksNumX * SyncProcess.chunksNumY));
    panel_Chunks.add(label_ChunksNumber, "2, 1, center, center");

    // Slider to set the numbers of chunks
    sSetChunks = new JSlider();
    sSetChunks.setSnapToTicks(true);
    sSetChunks.setToolTipText("set how detailed the grid should be");
    sSetChunks.setMinorTickSpacing(1);
    sSetChunks.setMajorTickSpacing(1);
    sSetChunks.setMaximum(30);
    sSetChunks.setValue(Settings.getInteger("chunks"));
    panel_Chunks.add(sSetChunks, "1, 1, center, center");

    sSetChunks.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent event) {
        Settings.set("chunks", sSetChunks.getValue());
        try {
          SyncProcess.setStandbyOutput();
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
        label_ChunksNumber.setText(String.valueOf(SyncProcess.chunksNumX * SyncProcess.chunksNumY));
      }
    });

    // Label "brightness"
    JLabel label_Brightness = new JLabel("   brightness");
    label_Brightness.setToolTipText("");
    label_Brightness.setHorizontalAlignment(SwingConstants.LEFT);
    getContentPane().add(label_Brightness, "1, 3, left, center");

    // Panel to hold brightness Slider and brightness percentage Label
    panel_Brightness = new JPanel();
    panel_Brightness.setLayout(new FormLayout(
        new ColumnSpec[] {ColumnSpec.decode("115px"), ColumnSpec.decode("right:38px:grow"),},
        new RowSpec[] {RowSpec.decode("26px"),}));
    getContentPane().add(panel_Brightness, "2, 3, 3, 1, fill, fill");

    // Label brightness percentage
    label_BrightnessPercentage = new JLabel(Settings.getInteger("brightness") + " %");
    label_BrightnessPercentage.setIconTextGap(3);
    label_BrightnessPercentage.setAlignmentX(Component.CENTER_ALIGNMENT);
    label_BrightnessPercentage.setHorizontalAlignment(SwingConstants.RIGHT);
    panel_Brightness.add(label_BrightnessPercentage, "2, 1, center, center");

    // Slider brightness
    slider_Brightness = new JSlider();
    slider_Brightness.setToolTipText("set how bright your lights should be");
    slider_Brightness.setMinorTickSpacing(5);
    slider_Brightness.setSnapToTicks(true);
    slider_Brightness.setMinimum(5);
    slider_Brightness.setValue(Settings.getInteger("brightness"));
    slider_Brightness.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        label_BrightnessPercentage.setText(String.valueOf(slider_Brightness.getValue()) + " %");
        Settings.set("brightness", slider_Brightness.getValue());
      }
    });
    panel_Brightness.add(slider_Brightness, "1, 1, center, center");

    // Label "saturation"
    label_Saturation = new JLabel("   saturation");
    label_Saturation.setHorizontalAlignment(SwingConstants.LEFT);
    getContentPane().add(label_Saturation, "1, 4, left, center");

    // Panel to hold saturation Slider and saturation percentage Label
    panel_Saturation = new JPanel();
    getContentPane().add(panel_Saturation, "2, 4, 3, 1, fill, fill");
    panel_Saturation.setLayout(new FormLayout(
        new ColumnSpec[] {ColumnSpec.decode("115px"), ColumnSpec.decode("right:38px:grow"),},
        new RowSpec[] {RowSpec.decode("26px"),}));

    // Slider saturation
    slider_Saturation = new JSlider();
    slider_Saturation.setToolTipText("set how saturated your lights should be");
    slider_Saturation.setMaximum(150);
    slider_Saturation.setMinorTickSpacing(5);
    slider_Saturation.setMinimum(50);
    slider_Saturation.setValue(Settings.getInteger("saturation"));
    slider_Saturation.setSnapToTicks(true);
    slider_Saturation.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        label_SaturationPercentage.setText(String.valueOf(slider_Saturation.getValue()) + " %");
        Settings.set("saturation", slider_Saturation.getValue());
      }
    });
    panel_Saturation.add(slider_Saturation, "1, 1, center, center");

    // Label saturation percentage
    label_SaturationPercentage = new JLabel(Settings.getInteger("saturation") + " %");
    panel_Saturation.add(label_SaturationPercentage, "2, 1, center, center");
    getContentPane().add(checkbox_ShowColourGrid, "1, 5, 4, 1");

    // CheckBox restore light
    checkbox_RestoreLight = new JCheckBox("   restore light");
    checkbox_RestoreLight
        .setToolTipText("restore the colour/brightness from your lights when the program stopped");
    checkbox_RestoreLight.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Settings.set("restorelight", checkbox_RestoreLight.isSelected());
      }
    });
    checkbox_RestoreLight.setSelected(Settings.getBoolean("restorelight"));
    getContentPane().add(checkbox_RestoreLight, "1, 6, 4, 1");

    // Button stop
    button_Stop = new JButton("STOP");
    button_Stop.setEnabled(false);
    button_Stop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        try {
          Main.hueControl.stopAmbilightProcess();
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    });
    getContentPane().add(button_Stop, "1, 8, fill, center");

    // Button start
    button_Start = new JButton("START");
    button_Start.setToolTipText("start to ");
    button_Start.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        try {
          Main.hueControl.startAmbilightProcess();
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    });
    getContentPane().add(button_Start, "2, 8, 3, 1, default, center");

    // MenuBar
    menubar = new JMenuBar();
    setJMenuBar(menubar);

    // Menu settings
    menu_Settings = new JMenu(" settings ");
    menubar.add(menu_Settings);

    // MenuItem options
    menuitem_Options = new JMenuItem("options");
    menuitem_Options.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        new OptionInterface();
      }
    });
    menu_Settings.add(menuitem_Options);

    // MenuItem reset
    menuitem_Reset = new JMenuItem("reset");
    menuitem_Reset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        try {
          // create a dialog window (ok or cancel)
          Object[] options = {"OK", "Cancel"};
          int dialogResult = JOptionPane.showOptionDialog(frame,
              "After a reset, all previous settings are lost and can't be recovered.\nThe program will be closed after reset.",
              "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
              options[1]);
          if (dialogResult == JOptionPane.YES_OPTION) {
            Settings.reset(true);
          }
        } catch (Exception e) {
          System.out.println("ERROR: " + e);
        }
      }
    });
    menu_Settings.add(menuitem_Reset);

    // Panel to hold MenuBar
    panel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel.getLayout();
    flowLayout.setHgap(4);
    flowLayout.setAlignment(FlowLayout.RIGHT);
    menubar.add(panel);


    // RigidArea to keep distance between objects
    rigidarea = Box.createRigidArea(new Dimension(9, 5));
    panel.add(rigidarea);

    // Label author
    JLabel label_Author = new JLabel("Huseyin Arpalikli");
    label_Author.setEnabled(false);
    panel.add(label_Author);

    // complete main user interface loading and show the window
    setVisible(true);
    System.out.println("main-interface loaded");
  }


  public void setupOnOffButton() throws Exception // enable/disable on/off Buttons
  {
    if (!Main.hueControl.ambilightProcessIsActive) {
      boolean lightOn = false;
      boolean lightOff = false;

      for (PHLight light : PHBridge.lights) {
        if (light.isOn() && Settings.Light.getActive(light)) {
          lightOn = true;
        } else if (!light.isOn() && Settings.Light.getActive(light)) {
          lightOff = true;
        }
      }

      if (lightOn && lightOff) {
        button_On.setEnabled(true);
        button_Off.setEnabled(true);
      } else if (lightOn) {
        button_On.setEnabled(false);
        button_Off.setEnabled(true);
      } else if (lightOff) {
        button_On.setEnabled(true);
        button_Off.setEnabled(false);
      }
    }
  }

  String clientId = "Ambilight";
  String topicName = "startambilight";
  int qos = 1;
  final String brokerUrl = "tcp://m21.cloudmqtt.com:14403";
  MqttClient client;
  MqttConnectOptions conOpt;

  public void mqttTask() throws MqttSecurityException, MqttException {
    String tmpDir = System.getProperty("java.io.tmpdir");
    MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

    // Construct the connection options object that contains connection parameters
    // such as cleanSession and LWT
    conOpt = new MqttConnectOptions();
    conOpt.setCleanSession(true);
    conOpt.setUserName("huseyin");
    conOpt.setPassword(new char[] {'h', 'u', 's', 'e', 'y', 'i', 'n'});

    // Construct an MQTT blocking mode client
    client = new MqttClient(brokerUrl, clientId, dataStore);

    // Set this wrapper as the callback handler
    client.setCallback(this);

    // Connect to the MQTT server
    client.connect(conOpt);

    // Create and configure a message
    String msg = "";
    MqttMessage message = new MqttMessage();
    message.setPayload(msg.getBytes());
    message.setQos(qos);

    client.publish(topicName, message); // Blocking publish

    // Subscribe to the requested topic
    // The QoS specified is the maximum level that messages will be sent to the client at.
    // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
    // be downgraded to 1 when delivering to the client but messages published at 1 and 0
    // will be received at the same level they were published at.
    System.out.print("Subscribing to topic \"" + topicName + "\" qos " + qos);
    client.subscribe(topicName, qos);

  }

  /****************************************************************/
  /* Methods to implement the MqttCallback interface */
  /****************************************************************/

  /**
   * @see MqttCallback#connectionLost(Throwable)
   */
  public void connectionLost(Throwable cause) {
    System.out.print("Connection to " + brokerUrl + " lost!" + cause);
    try {
      client.connect(conOpt);
    } catch (MqttException e) {
      e.printStackTrace();
    }
    
  }

  /**
   * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
   */
  public void deliveryComplete(IMqttDeliveryToken token) {
    // Called when a message has been delivered to the
    // server. The token passed in here is the same one
    // that was passed to or returned from the original call to publish.
    // This allows applications to perform asynchronous
    // delivery without blocking until delivery completes.
    //
    // This sample demonstrates asynchronous deliver and
    // uses the token.waitForCompletion() call in the main thread which
    // blocks until the delivery has completed.
    // Additionally the deliveryComplete method will be called if
    // the callback is set on the client
    //
    // If the connection to the server breaks before delivery has completed
    // delivery of a message will complete after the client has re-connected.
    // The getPendingTokens method will provide tokens for any messages
    // that are still to be delivered.
  }

  /**
   * @see MqttCallback#messageArrived(String, MqttMessage)
   */
  public void messageArrived(String topic, MqttMessage message) throws MqttException {
    // Called when a message arrives from the server that matches any
    // subscription made by the client
    String time = new Timestamp(System.currentTimeMillis()).toString();
    String msg = new String(message.getPayload());
    System.out.println("Time:\t" + time + "  Topic:\t" + topic + "  Message:\t" + msg + "  QoS:\t"
        + message.getQos());
    if (topic.equals("startambilight") && msg.equals("START")) {
      button_Start.doClick();
    } else if (topic.equals("startambilight") && msg.equals("STOP")) {
      button_Stop.doClick();
    } else if (topic.equals("startambilight") && msg.equals("ON")) {
      button_On.doClick();
    } else if (topic.equals("startambilight") && msg.equals("OFF")) {
      button_Off.doClick();
    }
  }

}

