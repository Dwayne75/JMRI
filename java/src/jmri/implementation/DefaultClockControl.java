// DefaultClockControl.java
package jmri.implementation;

import java.util.Date;
import jmri.ClockControl;
import jmri.InstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing default logic of the ClockControl interface.
 *
 * Hardware systems that have fast clocks should "extend DefaultClockControl"
 * and override the appropriate methods.
 *
 * This class provides default implementations of ClockControl methods that are
 * not needed in the hardware implementation if one exists, or for those systems
 * with no hardware fast clock.
 *
 * @author	Dave Duchamp Copyright (C) 2007
 * @version $Revision$
 */
public class DefaultClockControl implements ClockControl {

    public DefaultClockControl() {

    }

    /**
     * Operational instance variables (not saved between runs)
     */
    /**
     * Get Status of the Fast Clock
     */
    public int getStatus() {
        return 0;
    }

    /**
     * Get name of hardware clock Note: If there is no hardware clock, this
     * method returns null.
     */
    public String getHardwareClockName() {
        return null;
    }

    /**
     * Returns true if hardware clock accuracy can be corrected using the
     * computer clock. Hardware implementations should override this and return
     * true if they can correct their hardware clock.
     */
    public boolean canCorrectHardwareClock() {
        return false;
    }

    /**
     * Returns true if hardware clock can be set to 12 or 24 hour display from
     * JMRI software. Note: Default implementation is to return false.
     */
    public boolean canSet12Or24HourClock() {
        return false;
    }

    /**
     * Returns true if hardware clock requires an integer rate Note: Default
     * implementation returns false. If an integer rate is required by the
     * hardware, this method should be overridden.
     */
    public boolean requiresIntegerRate() {
        return false;
    }

    /**
     * Get and set the rate of the fast clock Note: The rate is an integer that
     * multiplies the wall clock For example, a rate of 4 specifies that the
     * fast clock runs 4 times faster than the wall clock. For the default
     * implementation, setRate is ignored, and getRate returns the rate of the
     * internal clock;
     */
    public void setRate(double newRate) {
        return;
    }

    public double getRate() {
        return InstanceManager.timebaseInstance().getRate();
    }

    /**
     * Set and get the fast clock time For the default implementation,set time
     * is ignored and getTime returns the time of the internal clock;
     */
    public void setTime(Date now) {
        return;
    }

    public Date getTime() {
        return InstanceManager.timebaseInstance().getTime();
    }

    /**
     * Start and stop hardware fast clock Many hardware fast clocks continue to
     * run indefinitely. This is provided for the case where the hardware clock
     * can be stopped and started.
     */
    public void startHardwareClock(Date now) {
        setTime(now);
        return;
    }

    public void stopHardwareClock() {
        return;
    }

    /**
     * Initialize the hardware fast clock Note: When the hardware clock control
     * receives this, it should initialize those clock settings that are
     * available on the hardware clock. Default implementation is to ignore this
     * request.
     */
    public void initializeHardwareClock(double rate, Date now, boolean getTime) {
        return;
    }

    static final Logger log = LoggerFactory.getLogger(DefaultClockControl.class.getName());
}

/* @(#)DefaultClockControl.java */
