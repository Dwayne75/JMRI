/**
 * SerialMonPane.java
 *
 * Description:	Swing action to create and register a MonFrame object
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2008 copied from Ecos and converted
 * to Powerline
 * @author	Ken Cameron Copyright (C) 2011
 * @version	$Revision$
 */
package jmri.jmrix.powerline.swing.serialmon;

import java.util.ResourceBundle;
import jmri.jmrix.powerline.SerialListener;
import jmri.jmrix.powerline.SerialMessage;
import jmri.jmrix.powerline.SerialReply;
import jmri.jmrix.powerline.SerialSystemConnectionMemo;
import jmri.jmrix.powerline.swing.PowerlinePanelInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialMonPane extends jmri.jmrix.AbstractMonPane implements SerialListener, PowerlinePanelInterface {

    /**
     *
     */
    private static final long serialVersionUID = 5452592994756329128L;
    ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrix.powerline.swing.serialmon.SerialMonBundle");

    public SerialMonPane() {
        super();
    }

    public String getHelpTarget() {
        return null;
    }

    public String getTitle() {
        StringBuilder x = new StringBuilder();
        if (memo != null) {
            x.append(memo.getUserName());
        } else {
            x.append(rb.getString("DefaultTag"));
        }
        x.append(": ");
        x.append(rb.getString("Title"));
        return x.toString();
    }

    public void dispose() {
        // disconnect from the SerialTrafficController
        if (memo != null) {
            memo.getTrafficController().removeSerialListener(this);
        }
        // and unwind swing
        super.dispose();
    }

    public void init() {
    }

    SerialSystemConnectionMemo memo;

    public void initContext(Object context) {
        if (context instanceof SerialSystemConnectionMemo) {
            initComponents((SerialSystemConnectionMemo) context);
        }
    }

    public void initComponents(SerialSystemConnectionMemo memo) {
        this.memo = memo;
        // connect to the SerialTrafficController
        memo.getTrafficController().addSerialListener(this);
    }

    public synchronized void message(SerialMessage l) {  // receive a message and log it
        nextLine(l.toMonitorString(), l.toString());
    }

    public synchronized void reply(SerialReply l) {  // receive a reply message and log it
        nextLine(l.toMonitorString(), l.toString());
    }

    /**
     * Nested class to create one of these using old-style defaults
     */
    static public class Default extends jmri.jmrix.powerline.swing.PowerlineNamedPaneAction {

        /**
         *
         */
        private static final long serialVersionUID = -2564227328398852669L;

        public Default() {
            super("Open Powerline Monitor",
                    new jmri.util.swing.sdi.JmriJFrameInterface(),
                    SerialMonPane.class.getName(),
                    jmri.InstanceManager.getDefault(SerialSystemConnectionMemo.class));
        }

    }

    static Logger log = LoggerFactory.getLogger(SerialMonPane.class.getName());

}

/* @(#)MonAction.java */
