// AppsLaunchPane.java
package apps;

import apps.gui3.TabbedPreferences;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.help.SwingHelpUtilities;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import jmri.IdTagManager;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.JmriPlugin;
import jmri.NamedBeanHandleManager;
import jmri.UserPreferencesManager;
import jmri.configurexml.ConfigXmlManager;
import jmri.configurexml.swing.DialogErrorHandler;
import jmri.implementation.AbstractShutDownTask;
import jmri.jmrit.DebugMenu;
import jmri.jmrit.ToolsMenu;
import jmri.jmrit.decoderdefn.DecoderIndexFile;
import jmri.jmrit.decoderdefn.PrintDecoderListAction;
import jmri.jmrit.display.PanelMenu;
import jmri.jmrit.display.layoutEditor.BlockValueFile;
import jmri.jmrit.jython.Jynstrument;
import jmri.jmrit.jython.JynstrumentFactory;
import jmri.jmrit.jython.RunJythonScript;
import jmri.jmrit.operations.OperationsMenu;
import jmri.jmrit.revhistory.FileHistory;
import jmri.jmrit.withrottle.WiThrottleCreationAction;
import jmri.jmrix.ActiveSystemsMenu;
import jmri.jmrix.ConnectionConfig;
import jmri.jmrix.ConnectionStatus;
import jmri.jmrix.JmrixConfigPane;
import jmri.managers.DefaultIdTagManager;
import jmri.managers.DefaultShutDownManager;
import jmri.managers.DefaultUserMessagePreferences;
import jmri.plaf.macosx.Application;
import jmri.plaf.macosx.PreferencesHandler;
import jmri.plaf.macosx.QuitHandler;
import jmri.profile.Profile;
import jmri.profile.ProfileManager;
import jmri.profile.ProfileManagerDialog;
import jmri.util.FileUtil;
import jmri.util.HelpUtil;
import jmri.util.Log4JUtil;
import jmri.util.SystemType;
import jmri.util.WindowMenu;
import jmri.util.iharder.dnd.FileDrop;
import jmri.util.iharder.dnd.FileDrop.Listener;
import jmri.util.swing.FontComboUtil;
import jmri.util.swing.JFrameInterface;
import jmri.util.swing.SliderSnap;
import jmri.util.swing.WindowInterface;
import jmri.web.server.WebServerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for pane filling main frame (window) of traditional-style JMRI applications
 * <P>
 * This is for launching after the system is initialized, so it does none of that.
 *
 * @author	Bob Jacobsen Copyright 2003, 2007, 2008, 2010, 2014
 * @author Dennis Miller Copyright 2005
 * @author Giorgio Terdina Copyright 2008
 * @author Matthew Harris Copyright (C) 2011
 * @version $Revision$
 */
public abstract class AppsLaunchPane extends JPanel implements PropertyChangeListener {

    static String profileFilename;

    public AppsLaunchPane() {

        super(true);

        setButtonSpace();
        setJynstrumentSpace();

        jmri.Application.setLogo(logo());
        jmri.Application.setURL(line2());



        // Add actions to abstractActionModel
        // Done here as initial non-GUI initialisation is completed
        // and UI L&F has been set
        addToActionModel();

        // populate GUI
        log.debug("Start UI");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(statusPanel());
        log.debug("Done with statusPanel, start buttonSpace");
        add(buttonSpace());
        add(_jynstrumentSpace);

    }

    protected final void addToActionModel() {
        apps.CreateButtonModel bm = InstanceManager.getDefault(apps.CreateButtonModel.class);
        ResourceBundle actionList = ResourceBundle.getBundle("apps.ActionListBundle");
        Enumeration<String> e = actionList.getKeys();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            try {
                bm.addAction(key, actionList.getString(key));
            } catch (ClassNotFoundException ex) {
                log.error("Did not find class {}", key);
            }
        }
    }

    /**
     * Prepare the JPanel to contain buttons in the startup GUI. Since it's
     * possible to add buttons via the preferences, this space may have
     * additional buttons appended to it later. The default implementation here
     * just creates an empty space for these to be added to.
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "only one application at a time")
    protected void setButtonSpace() {
        _buttonSpace = new JPanel();
        _buttonSpace.setLayout(new FlowLayout());
    }
    static JComponent _jynstrumentSpace = null;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
            justification = "only one application at a time")
    protected void setJynstrumentSpace() {
        _jynstrumentSpace = new JPanel();
        _jynstrumentSpace.setLayout(new FlowLayout());
        new FileDrop(_jynstrumentSpace, new Listener() {
            @Override
            public void filesDropped(File[] files) {
                for (int i = 0; i < files.length; i++) {
                    ynstrument(files[i].getPath());
                }
            }
        });
    }

    public static void ynstrument(String path) {
        Jynstrument it = JynstrumentFactory.createInstrument(path, _jynstrumentSpace);
        if (it == null) {
            log.error("Error while creating Jynstrument {}", path);
            return;
        }
        jmri.jmrit.throttle.ThrottleFrame.setTransparent(it);
        it.setVisible(true);
        _jynstrumentSpace.setVisible(true);
        _jynstrumentSpace.add(it);
    }

    protected String line1() {
        return Bundle.getMessage("DefaultVersionCredit", jmri.Version.name());
    }

    protected String line2() {
        return "http://jmri.org/";
    }

    protected String line3() {
        return " ";
    }
    // line 4
    JLabel cs4 = new JLabel();

    protected void buildLine4(JPanel pane) {
        if (connection[0] != null) {
            buildLine(connection[0], cs4, pane);
        }
    }
    // line 5 optional
    JLabel cs5 = new JLabel();

    protected void buildLine5(JPanel pane) {
        if (connection[1] != null) {
            buildLine(connection[1], cs5, pane);
        }
    }
    // line 6 optional
    JLabel cs6 = new JLabel();

    protected void buildLine6(JPanel pane) {
        if (connection[2] != null) {
            buildLine(connection[2], cs6, pane);
        }
    }
    // line 7 optional
    JLabel cs7 = new JLabel();

    protected void buildLine7(JPanel pane) {
        if (connection[3] != null) {
            buildLine(connection[3], cs7, pane);
        }
    }

    protected void buildLine(ConnectionConfig conn, JLabel cs, JPanel pane) {
        if (conn.name().equals(JmrixConfigPane.NONE)) {
            cs.setText(" ");
            return;
        }
        ConnectionStatus.instance().addConnection(conn.name(), conn.getInfo());
        cs.setFont(pane.getFont());
        updateLine(conn, cs);
        pane.add(cs);
    }

    protected void updateLine(ConnectionConfig conn, JLabel cs) {
        if (conn.getDisabled()) {
            return;
        }
        String name = conn.getConnectionName();
        if (name == null) {
            name = conn.getManufacturer();
        }
        if (ConnectionStatus.instance().isConnectionOk(conn.getInfo())) {
            cs.setForeground(Color.black);
            String cf = Bundle.getMessage("ConnectionSucceeded", name, conn.name(), conn.getInfo());
            cs.setText(cf);
        } else {
            cs.setForeground(Color.red);
            String cf = Bundle.getMessage("ConnectionFailed", name, conn.name(), conn.getInfo());
            cf = cf.toUpperCase();
            cs.setText(cf);
        }

        this.revalidate();
    }

    protected String line8() {
        return " ";
    }

    protected String line9() {
        return Bundle.getMessage("JavaVersionCredit",
                System.getProperty("java.version", "<unknown>"),
                Locale.getDefault().toString());
    }

    protected String logo() {
        return "resources/logo.gif";
    }

    /**
     * Fill in the logo and status panel
     *
     * @return Properly-filled out JPanel
     */
    protected JPanel statusPanel() {
        JPanel pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.X_AXIS));
        log.debug("Fetch main logo: {}", logo());
        pane1.add(new JLabel(new ImageIcon(getToolkit().getImage(logo()), "JMRI logo"), JLabel.LEFT));
        pane1.add(Box.createRigidArea(new Dimension(15, 0))); // Some spacing between logo and status panel

        log.debug("start labels");
        JPanel pane2 = new JPanel();

        pane2.setLayout(new BoxLayout(pane2, BoxLayout.Y_AXIS));
        pane2.add(new JLabel(line1()));
        pane2.add(new JLabel(line2()));
        pane2.add(new JLabel(line3()));

        // add listerner for Com port updates
        ConnectionStatus.instance().addPropertyChangeListener(this);
        ArrayList<Object> connList = InstanceManager.configureManagerInstance().getInstanceList(ConnectionConfig.class);
        int i = 0;
        if (connList != null) {
            for (int x = 0; x < connList.size(); x++) {
                ConnectionConfig conn = (ConnectionConfig) connList.get(x);
                if (!conn.getDisabled()) {
                    connection[i] = conn;
                    i++;
                }
                if (i > 3) {
                    break;
                }
            }
        }
        buildLine4(pane2);
        buildLine5(pane2);
        buildLine6(pane2);
        buildLine7(pane2);

        pane2.add(new JLabel(line8()));
        pane2.add(new JLabel(line9()));
        pane1.add(pane2);
        return pane1;
    }
    //int[] connection = {-1,-1,-1,-1};
    ConnectionConfig[] connection = {null, null, null, null};

    static protected void setJmriSystemProperty(String key, String value) {
        try {
            String current = System.getProperty("org.jmri.Apps." + key);
            if (current == null) {
                System.setProperty("org.jmri.Apps." + key, value);
            } else if (!current.equals(value)) {
                log.warn("JMRI property {} already set to {}, skipping reset to {}", key, current, value);
            }
        } catch (Exception e) {
            log.error("Unable to set JMRI property {} to {} due to execption {}", key, value, e);
        }
    }

    /**
     * Provide access to a place where applications can expect the configuration
     * code to build run-time buttons.
     *
     * @see apps.CreateButtonPanel
     * @return null if no such space exists
     */
    static public JComponent buttonSpace() {
        return _buttonSpace;
    }
    static JComponent _buttonSpace = null;
    static AppConfigBase prefs;

    static public AppConfigBase getPrefs() {
        return prefs;
    }

    /**
     * @deprecated as of 2.13.3, directly access the connection configuration
     * from the instance list
     * jmri.InstanceManager.configureManagerInstance().getInstanceList(jmri.jmrix.ConnectionConfig.class)
     */
    @Deprecated
    static public String getConnection1() {
        return MessageFormat.format(Bundle.getMessage("ConnectionCredit"),
                new Object[]{AppConfigBase.getConnection(0), AppConfigBase.getPort(0), AppConfigBase.getManufacturerName(0)});
    }

    /**
     * @deprecated as of 2.13.3, directly access the connection configuration
     * from the instance list
     * jmri.InstanceManager.configureManagerInstance().getInstanceList(jmri.jmrix.ConnectionConfig.class)
     */
    @Deprecated
    static public String getConnection2() {
        return MessageFormat.format(Bundle.getMessage("ConnectionCredit"),
                new Object[]{AppConfigBase.getConnection(1), AppConfigBase.getPort(1), AppConfigBase.getManufacturerName(1)});
    }

    /**
     * @deprecated as of 2.13.3, directly access the connection configuration
     * from the instance list
     * jmri.InstanceManager.configureManagerInstance().getInstanceList(jmri.jmrix.ConnectionConfig.class)
     */
    @Deprecated
    static public String getConnection3() {
        return MessageFormat.format(Bundle.getMessage("ConnectionCredit"),
                new Object[]{AppConfigBase.getConnection(2), AppConfigBase.getPort(2), AppConfigBase.getManufacturerName(2)});
    }

    /**
     * @deprecated as of 2.13.3, directly access the connection configuration
     * from the instance list
     * jmri.InstanceManager.configureManagerInstance().getInstanceList(jmri.jmrix.ConnectionConfig.class)
     */
    @Deprecated
    static public String getConnection4() {
        return MessageFormat.format(Bundle.getMessage("ConnectionCredit"),
                new Object[]{AppConfigBase.getConnection(3), AppConfigBase.getPort(3), AppConfigBase.getManufacturerName(3)});
    }
    

    /**
     * Set up the configuration file name at startup.
     * <P>
     * The Configuration File name variable holds the name used to load the
     * configuration file during later startup processing. Applications invoke
     * this method to handle the usual startup hierarchy:
     * <UL>
     * <LI>If an absolute filename was provided on the command line, use it
     * <LI>If a filename was provided that's not absolute, consider it to be in
     * the preferences directory
     * <LI>If no filename provided, use a default name (that's application
     * specific)
     * </UL>
     * This name will be used for reading and writing the preferences. It need
     * not exist when the program first starts up. This name may be proceeded
     * with <em>config=</em> and may not contain the equals sign (=).
     *
     * @param def Default value if no other is provided
     * @param args Argument array from the main routine
     */
    static protected void setConfigFilename(String def, String[] args) {
        // save the configuration filename if present on the command line
        if (args.length >= 1 && args[0] != null && !args[0].contains("=")) {
            def = args[0];
            log.debug("Config file was specified as: {}", args[0]);
        }
        for (String arg : args) {
            String[] split = arg.split("=", 2);
            if (split[0].equalsIgnoreCase("config")) {
                def = split[1];
                log.debug("Config file was specified as: {}", arg);
            }
        }
        Apps.configFilename = def;
        setJmriSystemProperty("configFilename", def);
    }

    static public String getConfigFileName() {
        log.debug("getConfigFileName() called, shouldn't have been", new Exception("bad call traceback"));
        return null;
        // was hopefully set by setJmriSystemProperty("configFilename", def) earlier, recover
    }

    @Override
    public void propertyChange(PropertyChangeEvent ev) {
        if (log.isDebugEnabled()) {
            log.debug("property change: comm port status update");
        }
        if (connection[0] != null) {
            updateLine(connection[0], cs4);
        }

        if (connection[1] != null) {
            updateLine(connection[1], cs5);
        }

        if (connection[2] != null) {
            updateLine(connection[2], cs6);
        }

        if (connection[3] != null) {
            updateLine(connection[3], cs7);
        }

    }

    /**
     * Returns the ID for the window's help, which is application specific
     */
    protected abstract String windowHelpID();

    static Logger log = LoggerFactory.getLogger(AppsLaunchPane.class.getName());
}