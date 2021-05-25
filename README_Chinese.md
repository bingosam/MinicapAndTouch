# [English](README.md)

# MinicapAndTouch

1. 通过adb连接安卓设备.
2. 将minicap部署到设备上，开启minicap服务并实时捕获设备的界面流数据.
3. 根据安卓系统版本自动选择minitouch或minitouch agent控制设备. 

# 环境要求

- [minicap](https://github.com/DeviceFarmer/minicap/releases) 

下载官方编译好的文件压缩包devicefarmer-minicap-prebuilt-{version}.tgz，将压缩包中package/prebuilt/目录下的所有文件解压到本项目的libs/stf/目录下

- [minitouch](https://github.com/DeviceFarmer/minitouch) 支持Android9及以下版本

[编译](https://github.com/DeviceFarmer/minitouch#Building) ，将编译输出目录minitouch/libs/下的编译文件拷贝至本项目的libs/stf/目录下

- [STFService.apk](https://github.com/DeviceFarmer/STFService.apk/releases)  支持Android10及以上版本

下载APK安装包保存到本项目的libs/stf/目录下

# 用法
- 若设备是Android10及以上系统版本，请先自主安装STFService.apk至Android设备. (本项目并未适配安装过程中出现的各种弹窗)


# 鸣谢

- [DeviceFarmer](https://github.com/DeviceFarmer)

# 开源许可

使用 [Apache License 2.0](LICENSE)

Copyright © The bingosam Project. All Rights Reserved.