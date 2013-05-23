package edu.drexel.TSAE;

import javax.sound.sampled.*;
/**
 * Write a description of class Player here.
 * 
 * @author tags
 * @version 0.1
 */
public class Player
{
    /*
     * Data should be formatted as follows:
     * [[time1,     time2,     time3]     ]
     * [[amplitude1,amplitude2,amplitude3]]
     * and must have a start value
     * 
     */
    double[][] data;
    double startVal;
    public Player(double[][] input)
    {
        data = input;
    }
    public void play()
    {
        int seconds = 120;
        int sampleRate = 44100;
        try
        {
            AudioFormat af = new AudioFormat((float)sampleRate, 8, 1, true, false );
            DataLine.Info info = new DataLine.Info ( SourceDataLine.class, af );
            SourceDataLine source =(SourceDataLine) AudioSystem.getLine( info );
            source.open( af );
            source.start();
            byte[] buf = new byte[(int)sampleRate * seconds];
            //this is just a rough way of doing things, I'd eventually like the sound produced to be entirely analog.
            int slope;
            double t;
            for(int i=0; i<buf.length; i++)
            {
                t=((double)i/(double)sampleRate);
                //find the appropriate time values represented by indices a and b that t lies between
                int a = 0;
                int b = data[0].length;
                while(true)
                {
                    if(Math.abs(a-b) == 1)
                    {
                        break;
                    }
                    if(data[0][(a+b)/2] > t)
                    {
                        b = (a+b)/2;
                    }
                    else if(data[0][(a+b)/2] < t)
                    {
                        a = (a+b)/2;
                    }
                    else
                    {
                        a = (a+b)/2;
                        b = a;
                        break;
                    }
                }
                if(a >= data[0].length || b >= data[0].length)
                {
                    break;
                }
                //these are valid for expressions that need to be approximated by higher order polynomials
                //double displacement = ((data[2][b]-data[2][a])*Math.pow((t-data[0][a]),2))/(2.0*(data[0][b]-data[0][a]));
                //double scalingFactor = ((data[0][b]-data[0][a])*(data[2][b]-data[2][a]))/(2.0*(data[1][b]-data[1][a]));
                double displacement = ((data[2][b]-data[2][a])*Math.pow((t-data[0][a]),2))/(2.0*(data[0][b]-data[0][a]));
                double scalingFactor = ((data[0][b]-data[0][a])*(data[2][b]-data[2][a]))/(2.0*(data[1][b]-data[1][a]));
                buf[i] =(byte)(((displacement*scalingFactor)+data[1][a]));
            }
            source.write(buf, 0, buf.length);
            source.drain();
            source.stop();
            source.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        System.exit(0);
    }
}
