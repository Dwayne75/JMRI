package jmri.jmrix.can.cbus.configurexml;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides load and store functionality for configuring CbusTurnoutManagers.
 * <P>
 * Uses the store method from the abstract base class, but provides a load
 * method here.
 *
 * @author Bob Jacobsen Copyright: Copyright (c) 2008
 * @version $Revision$
 * @since 2.3.1
 */
public class CbusTurnoutManagerXml extends jmri.managers.configurexml.AbstractTurnoutManagerConfigXML {

    public CbusTurnoutManagerXml() {
        super();
    }

    public void setStoreElementClass(Element turnouts) {
        turnouts.setAttribute("class", this.getClass().getName());
    }

    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    @Override
    public boolean load(Element shared, Element perNode) {
        // load individual turnouts
        return loadTurnouts(shared, perNode);
    }

    static Logger log = LoggerFactory.getLogger(CbusTurnoutManagerXml.class.getName());

}
