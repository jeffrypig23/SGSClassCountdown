package com.spud.ClassCountdown.Regime;

/**
 * Created by Stephen Ogden on 2/4/19.
 */
public class Class {

    private String name, customName = "";

    private long startTime, endTime;

    /**
     * (Non native java) Class constructor.
     *
     * @param name       The official name of the class.
     * @param startTime  When the class starts (as a long).
     * @param endTime    When the class ends (as a long).
     * @param customName The customized name for the class (usually an empty string).
     */
    public Class(String name, long startTime, long endTime, String customName) {
        this.setName(name);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setCustomName(customName);
    }

    /**
     * Get the current class from the loaded regime.
     *
     * @param currentTime The current time (as a long)
     * @return The class. If there is no class, will return null.
     */
    public static Class getClass(Regime regime, long currentTime) {
        for (Class c : regime.getClasses()) {
            // Check if the start time for the class has already passed, and if the end time is yet to come
            if (c.getStartTime() < currentTime && c.getEndTime() > currentTime) {
                // Set the current class, and stop the loop
                return c;
            }
        }
        return null;
    }

    /**
     * Get the time (as a long) of when the class starts.
     *
     * @return When the class.
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Sets the time (as a long) of when the class starts.
     *
     * @param startTime The time when the class starts.
     */
    private void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the time (as a long) of when the class ends.
     *
     * @return The time when the class ends.
     */
    public long getEndTime() {
        return this.endTime;
    }

    /**
     * Set the time (as a long) of when the class ends.
     *
     * @param endTime The time when the class ends.
     */
    private void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Get the name name of the class. If the boolean provided is true, the  it will return the custom name for the class rather than the official one.
     *
     * @param bool Whether or not to return the custom name for the class.
     * @return The class name.
     */
    public String getName(boolean bool) {
        return bool ? this.getCustomName() : this.name;
    }

    /**
     * Returns the custom name for the class. This may be empty an string!
     *
     * @return The custom class name.
     */
    private String getCustomName() {
        return this.customName;
    }

    /**
     * Sets the custom name for the class.
     *
     * @param customName The custom name.
     */
    private void setCustomName(String customName) {
        this.customName = customName;
    }

    /**
     * Sets the official name for the class.
     *
     * @param name The official class name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks whether this class has a custom name.
     *
     * @return Whether this class has a custom name.
     */
    public boolean hasCustomName() {
        return !this.customName.equals("");
    }
}
