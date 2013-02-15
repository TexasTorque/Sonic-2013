package org.TexasTorque.TorqueLib.motionProfile;

import edu.wpi.first.wpilibj.Watchdog;

public class ContinuousAccelFilter extends AccelFilterBase
{
    public ContinuousAccelFilter(double currPos, double currVel, double currAcc)
    {
        super(currPos, currVel, currAcc);
    }
    
    public void calcSystem(double distanceToTarget, double v, double goalV, double maxA, double maxV, double dt)
    {
        double dt2 = 0.0;
        double a = 0.0;
        double constTime = 0.0;
        double dtf = 0.0;
        double af = 0.0;
        // [0] - dt2    [1] - a     [2] - constTime     [3] - dtf       [4] - af
        double[] values = maxAccelTime(distanceToTarget, v, goalV, maxA, maxV, dt2, a, constTime, dtf, af);
        dt2 = values[0];
        a = values[1];
        constTime = values[2];
        dtf = values[3];
        af = values[4];
        double timeLeft = dt;
        if(dt2 > timeLeft)
        {
            updateVals(a, timeLeft);
        }
        else
        {
            updateVals(a, dt2);
            timeLeft -= dt2;
            if(constTime > timeLeft)
            {
                updateVals(0, timeLeft);
            }
            else
            {
                updateVals(0, constTime);
                timeLeft -= constTime;
                if(dtf > timeLeft)
                {
                    updateVals(af, timeLeft);
                }
                else
                {
                    updateVals(af, dtf);
                    timeLeft -= dtf;
                    updateVals(0, timeLeft);
                }
            }
        }
    }
    
    protected void updateVals(double acc, double dt)
    {
        currentPosition += (currentVelocity * dt) + (acc * 0.5 * dt * dt);
        currentVelocity += acc * dt;
        currentAcceleration = acc;
    }
    
    private double[] maxAccelTime(double distanceLeft, double currVel, double goalVel, double maxA, double maxV,
            double t1, double a1, double ct, double t2, double a2)
    {
        double constTime = 0.0;
        double startA = 0.0;
        if(distanceLeft > 0)
        {
            startA = maxA;
        }
        else if(distanceLeft == 0)
        {
            t1 = 0;
            a1 = 0;
            ct = 0;
            t2 = 0;
            a2 = 0;
            return new double[]{t1, a1, ct, t2, a2};
        }
        else
        {
            maxAccelTime(-distanceLeft, -currVel, -goalVel, maxA, maxV, t1, a1, ct, t2, a2);
            a1 *= -1;
            a2 *= -1;
            return new double[]{t1, a1, ct, t2, a2};
        }
        double maxAccelVelocity = distanceLeft * 2 * Math.abs(startA) + currVel * currVel;
        if(maxAccelVelocity > 0)
        {
            maxAccelVelocity = Math.sqrt(maxAccelVelocity);
        }
        else
        {
            maxAccelVelocity = -Math.sqrt(-maxAccelVelocity);
        }
        double finalA = 0.0;
        if(maxAccelVelocity > goalVel)
        {
            finalA = maxA;
        }
        else
        {
            finalA = maxA;
        }
        double topV = Math.sqrt((distanceLeft + (currVel * currVel) / (2.0 * startA) + (goalVel * goalVel) / (2.0 * finalA)) / (-1.0 / (2.0 * finalA) + 1.0 / (2.0 * startA)));
        double accelTime = 0.0;
        if(topV > maxV)
        {
            accelTime = (maxV - currVel) / maxA;
            constTime = (distanceLeft + (goalVel * goalVel - maxV * maxV) / (2.0 * maxA)) / maxV;
        }
        else
        {
            accelTime = (topV - currVel) / startA;
        }
        if(topV > -maxV)
        {
            System.err.println("Something broke in ContinuousAccelFilter!");
            Watchdog.getInstance().kill();
        }
        return new double[]{accelTime, startA, constTime, (goalVel - topV) / finalA, finalA};
    }
    
}