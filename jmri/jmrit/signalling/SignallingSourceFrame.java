// StatusFrame.java

package jmri.jmrit.signalling;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.util.ResourceBundle;

/**
 * Frame for Signal Logic Source Mast status
 * @author	Kevin Dickerson   Copyright (C) 2011
 * @version $Revision: 1.2 $
*/
public class SignallingSourceFrame extends jmri.util.JmriJFrame {

    static final ResourceBundle rb = ResourceBundle.getBundle("jmri.jmrit.signalling.SignallingBundle");

    public SignallingSourceFrame() {
        super(false, true);
    }
    
    JButton sendButton;
    SignallingSourcePanel sigPanel;
    protected boolean reuseFrameSavedSized = false;

    public void initComponents(jmri.SignalMast source) throws Exception {
        // the following code sets the frame's initial state
        if (source==null)
            sigPanel = new SignallingSourcePanel();
        else
            sigPanel = new SignallingSourcePanel(source);
        
        setTitle(rb.getString("SignallingPairs"));
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        getContentPane().add(sigPanel);

		addHelpMenu("package.jmri.jmrit.signalling.SignallingSourceFrame", true);

        // pack for display
        pack();
    }
}

