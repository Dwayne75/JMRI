// CMRIMenu.java
package jmri.jmrix.cmri;

import java.util.ResourceBundle;
import javax.swing.JMenu;

/**
 * Create a "Systems" menu containing the Jmri CMRI-specific tools
 *
 * @author	Bob Jacobsen Copyright 2003
 * @version $Revision$
 */
public class CMRIMenu extends JMenu {

    /**
     *
     */
    private static final long serialVersionUID = 4367233276261757531L;

    public CMRIMenu(String name) {
        this();
        setText(name);
    }

    public CMRIMenu() {

        super();

        ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.cmri.CMRIBundle");

        setText(rb.getString("MenuCMRI"));

        add(new jmri.jmrix.cmri.serial.serialmon.SerialMonAction(rb.getString("MenuItemCommandMonitor")));
        add(new jmri.jmrix.cmri.serial.packetgen.SerialPacketGenAction(rb.getString("MenuItemSendCommand")));
        add(new javax.swing.JSeparator());
        add(new jmri.jmrix.cmri.serial.diagnostic.DiagnosticAction(rb.getString("MenuItemDiagnostics")));
        add(new javax.swing.JSeparator());
        add(new jmri.jmrix.cmri.serial.nodeconfig.NodeConfigAction());
        add(new javax.swing.JSeparator());
        add(new jmri.jmrix.cmri.serial.assignment.ListAction(rb.getString("MenuItemAssignments")));
    }

}
