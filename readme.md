# PIDSim
I originally wrote this as a quick program to teach myself about PIDs.\
Since we are working on SwerveDrive at [@TexasTorque](https://github.com/TexasTorque),\
I added some the algorithm we are working on to drive better movement so we can do more iterative
testing than running changes on the robot directly.\
Also used just to practice Java but we can ignore that, seen as this is seriously the worst GUI i've ever written.
## Warning
Since this was made just for testing, as i'm pushing, the test movement DOES NOT WORK LIKE ITS DESIGNED TO.\
This is for YOUR TESTING PURPOSES ONLY, the system works, but not what we are testing.
## Run
Use ```java Main.java```\
If you want to use a different PID settings file than the default ```pid.text``` in the parent directly,\
pass the first argument as the path to the new PID settings file.\
Example: ```java Main.java /path/to/pid.txt```