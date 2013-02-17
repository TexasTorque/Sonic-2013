package org.TexasTorque.TorqueLib.util;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Watchdog;
import java.util.Vector;

public class AdaFruitLights
{
    private Watchdog watchdog;
    
    private Vector outputVector;
    
    private int currentState;
    
    public AdaFruitLights(Vector outputs)
    {
        watchdog = Watchdog.getInstance();
        outputVector = outputs;
    }
    
    public void setDesiredState (int state)
    {
        currentState = state;
    }
    
    public void setState()
    {
        String byteString = Integer.toBinaryString(currentState);
        for (int index = 0; index < outputVector.size(); index++)
        {
            watchdog.feed();
            int tempIndex = byteString.length() - 1 - index;
            char value = byteString.charAt(tempIndex);
            if (value == 48)
            {
                ((DigitalOutput)(outputVector.elementAt(index))).set(false);
            }
            else if(value == 49)
            {
                ((DigitalOutput)(outputVector.elementAt(index))).set(true);
            }
        }
    }
}
