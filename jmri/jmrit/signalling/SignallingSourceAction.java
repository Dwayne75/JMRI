// SignallingSourceAction.java

package jmri.jmrit.signalling;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Swing action to create and register a 
 *       			SignallingFrame object
 *
 * @author	    Kevin Dickerson    Copyright (C) 2011
 * @version		$Revision: 1.2 $	
 */

public class SignallingSourceAction extends AbstractAction {

    static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.signalling.SignallingBundle");

	public SignallingSourceAction(String s) {
        super(s);
    }
    
    public SignallingSourceAction(String s, jmri.SignalMast source) {
        super(s);
        this.source=source;
    }
    
    public SignallingSourceAction() {
        super(rb.getString("SignallingPairs"));
    }

    public void setMast(jmri.SignalMast source){
        this.source = source;
    }

    jmri.SignalMast source = null;

    public void actionPerformed(ActionEvent e) {
		SignallingSourceFrame f = new SignallingSourceFrame();
		try {
			f.initComponents(source);
			}
		catch (Exception ex) {
			log.error("Exception: "+ex.toString());
			ex.printStackTrace();
			}
		f.setVisible(true);	
	}
   static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SignallingSourceAction.class.getName());
}


/* @(#)SignallingSourceAction.java */
