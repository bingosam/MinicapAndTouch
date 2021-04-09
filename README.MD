# MinicapAndTouch

1. Connect Android devices through adb.
2. Capture the realtime screen data from minicap socket.
3. Auto select minitouch or minitouch agent to control the Android devices. 

# Requirements

- [minicap](https://github.com/DeviceFarmer/minicap/releases) 

download devicefarmer-minicap-prebuilt-{version}.tgz and extract  prebuilt/* into libs/stf
- [minitouch](https://github.com/DeviceFarmer/minitouch) for Android 9 and down

[build](https://github.com/DeviceFarmer/minitouch#Building) and then copy the minitouch/libs/* into ./libs/stf/
- [STFService.apk](https://github.com/DeviceFarmer/STFService.apk/releases)  for Android 10 and up

download and save into libs/stf/

# Usage
- install STFService.apk if device is Android 10 and up. (Because pop ups are not supported, STFService.apk needs to be installed manually)


# Thanks

- [DeviceFarmer](https://github.com/DeviceFarmer)

# LICENSE

See [LICENSE](LICENSE)

Copyright © The bingosam Project. All Rights Reserved.