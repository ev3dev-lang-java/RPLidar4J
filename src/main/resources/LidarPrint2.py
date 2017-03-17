import math
import numpy as np
import optparse
import serial
from time import sleep

###############################
#
#  Lidar Print
#
#  By: John McCormack www.jdmccormack.com
#  Modified by: Juan Antonio Brenha Moral
#  
#  A simple class for interfacing to the 
#  Robopeak RPLidar using Python.
#  
#  Built for Windows, one line needs to be
#  changed for mac or linux
#  
#
################################


###############################
#
#  Below fields are from the 
#  RPLidar Data Sheets
#
################################
Start_Scan = "\xA5\x20" #Begins scanning
Force_Scan = "\xA5\x21" #Overrides anything preventing a scan
Health = "\xA5\x52" #Returns the state of the Lidar
Stop_Scan = "\xA5\x25" #Stops the scan
RESET = "\xA5\x40" #Resets the device

###############################
#
#  Class Lidar
#
#  Inputs: Port to connect to
#
#  Init will call all necessary
#  functions to start printing.
#
################################
class Lidar():

    def __init__(self,port):
        #set the port as an instance variable

        self.port = port
        #lock checks if the connection is made
        lock = False 

        #Begin by starting the scan
        lock = self.startScan(self.port)

        #Once scan is started, beging printing data
        if lock == True:
            self.getPoints(self.port)
        else:
            print "Exiting"

###############################
#
#  Start Scan
# 
#  Method connects and starts
#  the Lidar
#
#  Inputs: Port to scan from
#
#  Outputs: true once lock is 
#  acquired. 
#
################################
    def startScan(self, port):
        print "Connecting"
        line = ""
        #Lock is true once connected
        lock = False
        #Continue looping until connected
        while lock == False:
            print "..."
            # First reset the port
            port.write(RESET)
            # Wait
            sleep(2)
            #Start reading
            #Look for the correct start
            #frame of A55A
            port.write(Start_Scan)
            try:
                #If after looping nothing found,
                #Reset and try again
                for a in range(0, 250):
                    character = port.read()
                    line += character
                    if (line[0:2] == "\xa5\x5a"):
                        if(len(line) == 7):
                            lock = True
                            break
                        
                    elif (line[0:2] != "\xa5\x5a" and len(line) == 2):
                        line = ""
            except KeyboardInterrupt:
                break
        return lock

###############################
#
#  Get Points
#
#  Inputs: Port to scan
#  Polar - if true, print polar
#          coordinates, otherwise
#          print rectangular
#
#  Ouput: Prints the recieved data
#
#  Generally this will be the method
#  You want to edit to pipe the data
#  someplace else. 
#
################################

    def getPoints(self,port,polar=True):

        line = ""
        while True:
            try:
                character = port.read()
                line += character
                #Data comes in 5 byte blocks
                if (len(line) == 5):
                    #Switches based on desired output
                    if polar == True:
                        point = str(self.point_Polar(line))
                    else:
                        point = str(self.point_XY(line))

                    # Remove () from previous output
                    newstr = point.replace("(", "")
                    newstr = newstr.replace(")", "")
                    print newstr
                    line = ""
                    
            except KeyboardInterrupt:
                break


###############################
#
# Left Shift Bits
#
# This method is used to properly
# organize the recorded data. 
# Based off of the Application
# notes. 
# 
# Input is the serial frame
# output is the data formated
# properly. 
#
################################
        
    def leftshiftbits(self,line):
        line = int(line, 16)
        line = bin(line)
        line = line[:2] + "0" + line[2:-1]
        line = int(line, 2) #convert to integer
        return line


###############################
#
# Point Polar
#
# This converts the serial frame
# into human readable format. 
#
# All information is based on the
# RPLidar data sheet.
#
# Serial Frame = the line of data
# If radians is false it will print
# in degrees. 
#
################################

    def point_Polar(self,serial_frame,radians=False):
        #Get Distance
        distance = serial_frame[4].encode("hex") + serial_frame[3].encode("hex")
        distance = int(distance, 16)
        distance = distance / 4 #instructions from data sheet
        #Get Angle
        angle = serial_frame[2].encode("hex") + serial_frame[1].encode("hex")
        angle = self.leftshiftbits(angle) #remove check bit, convert to integer
        angle = angle/64 #instruction from data sheet

        if radians == True:
            theta = (angle * np.pi) / 180 #uncomment to use radians
        
            return(distance,theta) #uncomment to return radians

        else:
            return(distance, angle)


###############################
#
# point XY
#
# Converts the polar value into X
# and Y values based on Trigonometry
#
# Inputs - one frame of serial data
# Outputs - The X, Y coordinate. 
#
################################
       
    def point_XY(self,serial_frame):
        circular_coordinates = point_Polar(serial_frame)
        distance = circular_coordinates[0]
        angle = circular_coordinates[1]
        
        #Get X
        x = distance * math.cos(angle)
        
        #Get Y
        y = distance * math.sin(angle)
        return (x,y)


if __name__ == "__main__":

    parser = optparse.OptionParser()
    (options, args) = parser.parse_args()

    if len(args) == 0:
        raise ValueError("No command line arguments")
    elif len(args) == 1:
        #if first_re.match(args[0]):
        #    print "Primary argument is : ", args[0]
        #else:
        #    raise ValueError("First argument should be ...")
        print "Primary argument is : ", args[0]
        port = args[0]
    elif len(args) > 1:
        raise ValueError("Too many command line arguments")

    #COM4 was used on my computer, this will change based on
    #your setup and whether you're on Windows/Mac/Linux
    #port = "/dev/ttyUSB0"
    ser = serial.Serial(port, 115200, timeout = 5)
    ser.setDTR(False)
    print ser.name

    #Create a Lidar instance, this will immidiately start printing.
    #To edit where the data is sent, edit the GetPoints Method
    lidar = Lidar(ser)
