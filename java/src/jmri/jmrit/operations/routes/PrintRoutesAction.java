// PrintRouteAction.java
package jmri.jmrit.operations.routes;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import jmri.jmrit.operations.setup.Control;
import jmri.util.davidflanagan.HardcopyWriter;

/**
 * Action to print all of the routes used in operations.
 *
 * @author Daniel Boudreau Copyright (C) 2012
 * @version $Revision: 17977 $
 */
public class PrintRoutesAction extends PrintRouteAction {

    /**
     *
     */
    private static final long serialVersionUID = 6083754676592916495L;
    private static final char FORM_FEED = '\f';

    public PrintRoutesAction(String actionName, boolean preview) {
        super(actionName, preview, null);
    }

    public void actionPerformed(ActionEvent e) {
        log.debug("Print all routes");
        // obtain a HardcopyWriter to do this
        HardcopyWriter writer = null;
        try {
            writer = new HardcopyWriter(mFrame, MessageFormat.format(
                    Bundle.getMessage("TitleRoutesTable"), new Object[]{}), Control.reportFontSize, .5, .5, .5, .5,
                    isPreview);
        } catch (HardcopyWriter.PrintCanceledException ex) {
            log.debug("Print cancelled");
            return;
        }
        List<Route> routes = RouteManager.instance().getRoutesByNameList();
        for (int i = 0; i < routes.size(); i++) {
            Route route = routes.get(i);
            try {
                writer.write(route.getName() + NEW_LINE);
                printRoute(writer, route);
                if (i != routes.size() - 1) {
                    writer.write(FORM_FEED);
                }
            } catch (IOException e1) {
                log.error("Exception in print routes");
            }
        }
        // and force completion of the printing
        writer.close();
    }
}
