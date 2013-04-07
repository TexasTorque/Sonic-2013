package org.TexasTorque.TorqueLib.component;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Watchdog;
import java.util.Vector;

public class AdaFruitLights
{
    private Watchdog watchdog;
    
    private Vector outputVector;
    private int currentState;
    private int desiredState;
    
    public AdaFruitLights(Vector outputs)
    {
        watchdog = Watchdog.getInstance();
        outputVector = outputs;
    }
    
    public void setDesiredState(int state)
    {
        desiredState = state;
    }
    
    private void setState()
    {
        String byteString = Integer.toBinaryString(currentState);
        byteString = "0000" + byteString;
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
    
    public void run()
    {
        if(currentState != desiredState)
        {
            currentState = desiredState;
            setState();
        }
    }
}