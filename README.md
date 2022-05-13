# CS528CompanionApp

This is the demo application

It requires the "read all notifications" permission accessible from notifications settings
It requires Location and Nearby Devices permissions as well (might need to go to app settings and enable the permission manually)


Turn on bluetooth before starting the app.

The app allows you to connect to the wearable by pressing "scan" and selecting the wearable. Once it is connected, it will start collecting sensor data over bluetooth.
If the sensor data is properly formated (not corrupted), then the magnitude of the accelerometer is calculated. If it is greater than 1000mG (milli-gravity), then 
the state is set to "in motion", otherwise it is "at rest"

When wearable is "at rest", then the app will send a vibration to the wearable when a priority notification arrives.

Priority notifications can be selected in the app by using the "+" button and selecting an app along with a string qualifier

Script for the wearable can be found [here](https://github.com/Vrend/CS528DataCollection/blob/CS228DataCollect/Arduino/sketch_apr25b/sketch_apr25b.ino)
