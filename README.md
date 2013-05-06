TSAE2
=====
In terms of development, this application can be built using ant, but can also be loaded using the BlueJ IDE. 

The purpose of this application is to communicate with an Arduino Microcontroller which is using a series of differentiator circuits to determine when a sinusoidal input's derivatives are equal to zero. This data is recorded, processed and reconstructed using a Taylor Series Approximation.

The dependencies for this application are as follows:
 - guava
 - RXTX
 - symja
 
These must be installed and included in the java classpath for your system. The included guava and symja jar file should function properly regardless of platform, but RXTX may not since its implementation of serial communication has some c dependencies.
