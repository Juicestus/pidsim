#!/usr/bin/env python3

from simple_pid import PID
from time import sleep
from matplotlib import pyplot as plt

pid = PID(1.5, 0.05, 0.01)

target = 100
current = 0
pid.setpoint = target

currents = []
controls = []
xs = list(range(250))
for i in xs:
    control = pid(current)
    controls.append(control)
    currents.append(current)
    print(f'{i} : {current} -> {target} : {control}')
    current += control
    sleep(.1)

#plt.plot(xs[5:], controls[5:], color="RED")
plt.plot(xs[5:], currents[5:], color="BLUE")
plt.show()