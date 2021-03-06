// CarsSetFrameAction.java
package jmri.jmrit.operations.rollingstock.cars;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 * Swing action to create and register a CarsSetFrame object.
 *
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2010
 * @version $Revision: 22219 $
 */
public class EnableDestinationAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = -6780168973942677595L;
    CarSetFrame _csFrame;

    public EnableDestinationAction(String s, CarSetFrame frame) {
        super(s);
        _csFrame = frame;
    }

    public void actionPerformed(ActionEvent e) {
        _csFrame.setDestinationEnabled(true);
    }
}

/* @(#)CarsSetFrameAction.java */
