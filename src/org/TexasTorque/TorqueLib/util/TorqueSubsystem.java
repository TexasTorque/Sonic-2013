package org.TexasTorque.TorqueLib.util;

public interface TorqueSubsystem
{
    // They should also have a getInstance() method.
    public void run();
    public void loadParameters();
    public void logData();
    public String getKeyNames();
}
