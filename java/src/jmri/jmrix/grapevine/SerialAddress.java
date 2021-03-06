// SerialAddress.java
package jmri.jmrix.grapevine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility Class supporting parsing and testing of addresses
 * <P>
 * Multiple address formats are supported: Gtnnnxxx where: t is the type code,
 * 'T' for turnouts, 'S' for sensors, 'H' for signals and 'L' for lights nn is
 * the node address (0-127) xxx is a bit number of the input or output bit
 * (001-999) nnxxx = (node address x 1000) + bit number examples: GT2 (node
 * address 0, bit 2), GS1003 (node address 1, bit 3), GL11234 (node address 11,
 * bit234) Gtnnnaxxxx where: t is the type code, 'T' for turnouts, 'S' for
 * sensors, 'H' for signals and 'L' for lights nnn is the node address of the
 * input or output bit (0-127) xxxx is a bit number of the input or output bit
 * (1-2048) a is a subtype-specific letter: 'B' for a bit number (e.g. GT12B3 is
 * a shorter form of GT12003) 'a' is for advanced serial occupancy sensors 'm'
 * is for advanced serial motion sensors 'p' is for parallel sensors 's' is for
 * serial occupancy sensors
 *
 * examples: GT0B2 (node address 0, bit 2), GS1B3 (node address 1, bit 3),
 * GL11B234 (node address 11, bit234)
 * <P>
 * @author	Dave Duchamp, Copyright (C) 2004
 * @author Bob Jacobsen, Copyright (C) 2006, 2007, 2008
 * @version $Revision$
 */
public class SerialAddress {

    public SerialAddress() {
    }

    /**
     * Regular expression used to parse Turnout names.
     * <p>
     * Groups:
     * <ul>
     * <li>1 - System letter
     * <li>2 - Type letter
     * <li>3 - suffix, if of nnnAnnn form
     * <li>4 - node number in nnnAnnn form
     * <li>5 - address type in nnnAnnn form
     * <li>6 - bit number in nnnAnnn form
     * <li>7 - combined number in nnnnnn form
     * </ul>
     */
    static final String turnoutRegex = "^(G)(T)(?:((\\d++)(B)(\\d++))|(\\d++))$";
    static Pattern turnoutPattern = null;

    static Pattern getTurnoutPattern() {
        if (turnoutPattern == null) {
            turnoutPattern = Pattern.compile(turnoutRegex);
        }
        return turnoutPattern;
    }

    /**
     * Regular expression used to parse Light names.
     * <p>
     * Groups:
     * <ul>
     * <li>1 - System letter
     * <li>2 - Type letter
     * <li>3 - suffix, if of nnnAnnn form
     * <li>4 - node number in nnnAnnn form
     * <li>5 - address type in nnnAnnn form
     * <li>6 - bit number in nnnAnnn form
     * <li>7 - combined number in nnnnnn form
     * </ul>
     */
    static final String lightRegex = "^(G)(L)(?:((\\d++)(B)(\\d++))|(\\d++))$";
    static Pattern lightPattern = null;

    static Pattern getLightPattern() {
        if (lightPattern == null) {
            lightPattern = Pattern.compile(lightRegex);
        }
        return lightPattern;
    }

    /**
     * Regular expression used to parse signalHead names.
     * <p>
     * Groups:
     * <ul>
     * <li>1 - System letter
     * <li>2 - Type letter
     * <li>3 - suffix, if of nnnAnnn form
     * <li>4 - node number in nnnAnnn form
     * <li>5 - address type in nnnAnnn form
     * <li>6 - bit number in nnnAnnn form
     * <li>7 - combined number in nnnnnn form
     * </ul>
     */
    static final String headRegex = "^(G)(H)(?:((\\d++)(B)(\\d++))|(\\d++))$";
    static Pattern headPattern = null;

    static Pattern getHeadPattern() {
        if (headPattern == null) {
            headPattern = Pattern.compile(headRegex);
        }
        return headPattern;
    }

    /**
     * Regular expression used to parse Sensor names.
     * <p>
     * Groups:
     * <ul>
     * <li>1 - System letter
     * <li>2 - Type letter
     * <li>3 - suffix, if of nnnAnnn form
     * <li>4 - node number in nnnAnnn form
     * <li>5 - address type in nnnAnnn form
     * <li>6 - bit number in nnnAnnn form
     * <li>7 - combined number in nnnnnn form
     * </ul>
     */
    static final String sensorRegex = "^(G)(S)(?:((\\d++)([BbAaMmPpSs])(\\d++))|(\\d++))$";
    static Pattern sensorPattern = null;

    static Pattern getSensorPattern() {
        if (sensorPattern == null) {
            sensorPattern = Pattern.compile(sensorRegex);
        }
        return sensorPattern;
    }

    /**
     * Regular expression used to parse from any type of name.
     * <p>
     * Groups:
     * <ul>
     * <li>1 - System letter
     * <li>2 - Type letter
     * <li>3 - suffix, if of nnnAnnn form
     * <li>4 - node number in nnnAnnn form
     * <li>5 - address type in nnnAnnn form
     * <li>6 - bit number in nnnAnnn form
     * <li>7 - combined number in nnnnnn form
     * </ul>
     */
    static final String allRegex = "^(G)([SHLT])(?:((\\d++)([BbAaMmPpSs])(\\d++))|(\\d++))$";
    static Pattern allPattern = null;

    static Pattern getAllPattern() {
        if (allPattern == null) {
            allPattern = Pattern.compile(allRegex);
        }
        return allPattern;
    }

    /**
     * Parse for secondary letters.
     *
     * @return offset for type letter, or -1 if none
     */
    static int typeOffset(String type) {
        switch (type.toUpperCase().charAt(0)) {
            case 'B':
                return 0;
            case 'A':
                return SerialNode.offsetA;
            case 'M':
                return SerialNode.offsetM;
            case 'P':
                return SerialNode.offsetP;
            case 'S':
                return SerialNode.offsetS;
            default:
                return -1;
        }
    }

    /**
     * Public static method to parse a system name and return the Serial Node
     * Note: Returns 'NULL' if illegal systemName format or if the node is not
     * found
     */
    public static SerialNode getNodeFromSystemName(String systemName) {
        // validate the System Name leader characters
        Matcher matcher = getAllPattern().matcher(systemName);
        if (!matcher.matches()) {
            // here if an illegal format 
            log.error("illegal system name format: " + systemName);
            return (null);
        }

        // start decode
        int ua;
        if (matcher.group(7) != null) {
            // This is a Gtnnxxx address
            int num = Integer.valueOf(matcher.group(7)).intValue();
            if (num > 0) {
                ua = num / 1000;
            } else {
                log.error("invalid value in system name: " + systemName);
                return (null);
            }
        } else {
            ua = Integer.valueOf(matcher.group(4)).intValue();
        }
        return (SerialNode) SerialTrafficController.instance().getNodeFromAddress(ua);
    }

    /**
     * Public static method to parse a system name and return the bit number
     * Notes: Bits are numbered from 1. If an error is found, 0 is returned.
     */
    public static int getBitFromSystemName(String systemName) {
        // validate the System Name leader characters
        Matcher matcher = getAllPattern().matcher(systemName);
        if (!matcher.matches()) {
            // here if an illegal format 
            log.error("illegal system name format: " + systemName);
            return 0;
        }

        // start decode
        int n = 0;
        if (matcher.group(7) != null) {
            // name in be Gtnnxxx format
            int num = Integer.valueOf(matcher.group(7)).intValue();
            if (num > 0) {
                n = num % 1000;
            } else {
                log.error("invalid value in system name: " + systemName);
                return (0);
            }
        } else {
            // This is a Gtnnaxxxx address
            n = Integer.valueOf(matcher.group(6)).intValue();
        }
        return (n);
    }

    /**
     * Public static method to parse a system name and return the node number
     * Notes: Nodes are numbered from 1. If an error is found, -1 is returned.
     */
    public static int getNodeAddressFromSystemName(String systemName) {
        // validate the System Name leader characters
        Matcher matcher = getAllPattern().matcher(systemName);
        if (!matcher.matches()) {
            // here if an illegal format 
            log.error("illegal system name format: " + systemName);
            return (-1);
        }

        // start decode
        int ua;
        if (matcher.group(7) != null) {
            // This is a Gtnnxxx address
            int num = Integer.valueOf(matcher.group(7)).intValue();
            if (num > 0) {
                ua = num / 1000;
            } else {
                log.error("invalid value in system name: " + systemName);
                return (-1);
            }
        } else {
            ua = Integer.valueOf(matcher.group(4)).intValue();
        }
        return ua;
    }

    /**
     * Public static method to validate system name format
     *
     * @return 'true' if system name has a valid format, else returns 'false'
     * @param systemName name to check
     * @param type       expected device type letter
     */
    public static boolean validSystemNameFormat(String systemName, char type) {
        // validate the System Name leader characters
        Matcher matcher = getAllPattern().matcher(systemName);
        if (!matcher.matches()) {
            // here if an illegal format, e.g. another system letter
            // which happens all the time due to how proxy managers work
            return false;
        }
        if (matcher.group(2).charAt(0) != type) {
            log.error("type in " + systemName + " does not match " + type);
            return false;
        }
        Pattern p;
        if (type == 'L') {
            p = getLightPattern();
        } else if (type == 'T') {
            p = getTurnoutPattern();
        } else if (type == 'H') {
            p = getHeadPattern();
        } else if (type == 'S') {
            p = getSensorPattern();
        } else {
            log.error("cannot match type in " + systemName + ", which is unexpected");
            return false;
        }

        // check format
        Matcher m2 = p.matcher(systemName);
        if (!m2.matches()) {
            // here if cannot parse specifically
            log.error("illegal system name format: " + systemName + " for type " + type);
            return (false);
        }

        // check for the two different formats
        int node = -1;
        int bit = -1;
        if (matcher.group(7) != null) {
            // name in be Gtnnxxx format
            int num = Integer.valueOf(matcher.group(7)).intValue();
            if (num > 0) {
                node = num / 1000;
                bit = num % 1000;
            } else {
                log.error("invalid value in system name: " + systemName);
                return false;
            }
        } else {
            // This is a Gtnnaxxxx address, get values
            node = Integer.valueOf(matcher.group(4)).intValue();
            bit = Integer.valueOf(matcher.group(6)).intValue();

        }

        // check values
        if ((node < 1) || (node > 127)) {
            log.error("invalid node number " + node + " in " + systemName);
            return false;
        }

        // check bit numbers
        if ((type == 'T') || (type == 'H') || (type == 'L')) {
            if (!((bit >= 101 && bit <= 124)
                    || (bit >= 201 && bit <= 224)
                    || (bit >= 301 && bit <= 324)
                    || (bit >= 401 && bit <= 424))) {
                log.error("invalid bit number " + bit + " in " + systemName);
                return false;
            }
        } else if (type == 'S') {
            // sort on subtype
            String subtype = matcher.group(5);
            if (subtype == null) { // no subtype, just look at total
                if ((bit < 1) || (bit > 224)) {
                    log.error("invalid bit number " + bit + " in " + systemName);
                    return false;
                } else {
                    return true;
                }
            }
            subtype = subtype.toUpperCase();
            if (subtype.equals("A")) // advanced serial occ
            {
                if ((bit < 1) || (bit > 24)) {
                    log.error("invalid bit number " + bit + " in " + systemName);
                    return false;
                } else if (subtype.equals("M")) // advanced serial motion
                {
                    if ((bit < 1) || (bit > 24)) {
                        log.error("invalid bit number " + bit + " in " + systemName);
                        return false;
                    } else if (subtype.equals("S")) // old serial
                    {
                        if ((bit < 1) || (bit > 24)) {
                            log.error("invalid bit number " + bit + " in " + systemName);
                            return false;
                        }
                    }
                }
            }
            if (subtype.equals("P")) // parallel
            {
                if ((bit < 1) || (bit > 96)) {
                    log.error("invalid bit number " + bit + " in " + systemName);
                    return false;
                }
            }
        }

        // finally, return true
        return true;
    }

    /**
     * Public static method to validate system name for configuration returns
     * 'true' if system name has a valid meaning in current configuration, else
     * returns 'false'
     */
    public static boolean validSystemNameConfig(String systemName, char type) {
        if (!validSystemNameFormat(systemName, type)) {
            // No point in trying if a valid system name format is not present
            log.warn(systemName + " invalid");
            return false;
        }
        SerialNode node = getNodeFromSystemName(systemName);
        if (node == null) {
            log.warn(systemName + " invalid; no such node");
            // The node indicated by this system address is not present
            return false;
        }
        int bit = getBitFromSystemName(systemName);
        if ((type == 'T') || (type == 'L')) {
            if ((bit <= 0) || (bit > SerialNode.outputBits[node.nodeType])) {
                // The bit is not valid for this defined Serial node
                log.warn(systemName + " invalid; bad output bit number " + bit + " > " + SerialNode.outputBits[node.nodeType]);
                return false;
            }
        } else if (type == 'S') {
            if ((bit <= 0) || (bit > SerialNode.inputBits[node.nodeType])) {
                // The bit is not valid for this defined Serial node
                log.warn(systemName + " invalid; bad input bit number " + bit + " > " + SerialNode.inputBits[node.nodeType]);
                return false;
            }
        } else {
            log.error("Invalid type specification in validSystemNameConfig call");
            return false;
        }
        // System name has passed all tests
        return true;
    }

    /**
     * Public static method to convert any format system name for the alternate
     * format (nnBnn) If the supplied system name does not have a valid format,
     * or if there is no representation in the alternate naming scheme, an empty
     * string is returned.
     */
    public static String convertSystemNameToAlternate(String systemName) {
        // ensure that input system name has a valid format
        if (!validSystemNameFormat(systemName, systemName.charAt(1))) {
            // No point in normalizing if a valid system name format is not present
            return "";
        }

        Matcher matcher = getAllPattern().matcher(systemName);
        matcher.matches(); // known to work, just need values
        // check format
        if (matcher.group(7) != null) {
            int num = Integer.valueOf(matcher.group(7)).intValue();
            return matcher.group(1) + matcher.group(2) + (num / 1000) + "B" + (num % 1000);
        } else {
            int node = Integer.valueOf(matcher.group(4)).intValue();
            int bit = Integer.valueOf(matcher.group(6)).intValue();
            return matcher.group(1) + matcher.group(2) + node + "B" + bit;
        }
    }

    /**
     * Public static method to normalize a system name
     * <P>
     * This routine is used to ensure that each system name is uniquely linked
     * to one bit, by removing extra zeros inserted by the user.
     * <P>
     * If the supplied system name does not have a valid format, an empty string
     * is returned. Otherwise a normalized name is returned in the same format
     * as the input name.
     */
    public static String normalizeSystemName(String systemName) {
        // ensure that input system name has a valid format
        if (!validSystemNameFormat(systemName, systemName.charAt(1))) {
            // No point in normalizing if a valid system name format is not present
            return "";
        }

        Matcher matcher = getAllPattern().matcher(systemName);
        matcher.matches(); // known to work, just need values

        // check format
        if (matcher.group(7) != null) {
            int num = Integer.valueOf(matcher.group(7)).intValue();
            return matcher.group(1) + matcher.group(2) + num;
        } else {
            // there are alternate forms...
            int offset = typeOffset(matcher.group(5));
            int node = Integer.valueOf(matcher.group(4)).intValue();
            int bit = Integer.valueOf(matcher.group(6)).intValue();
            return matcher.group(1) + matcher.group(2) + (node * 1000 + bit + offset);
        }
    }

    static Logger log = LoggerFactory.getLogger(SerialAddress.class.getName());
}

/* @(#)SerialAddress.java */
