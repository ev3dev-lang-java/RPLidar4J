from random import randint

lock = False
#Continue looping until connected
while lock == False:

    try:

        for angle in xrange(0,359):
            distance = randint(0,5000)
            print distance, angle

    except KeyboardInterrupt:
        break
