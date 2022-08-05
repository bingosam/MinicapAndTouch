package com.github.bingosam.device;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.github.bingosam.entity.Point;

import java.io.IOException;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>

 * <p>Create Time: 2021/4/9          </p>
 *
 * @author zhang kunbin
 */
public class AdbCli implements DeviceController {

    private final DeviceWrap device;

    private final DeviceSize deviceSize;

    private Point point;

    public AdbCli(DeviceWrap device, DeviceSize deviceSize) {
        this.device = device;
        this.deviceSize = deviceSize;
    }


    @Override
    public void down(int x, int y) throws IOException {
        //do nothing
        point = convertToRealPoint(x, y);
    }

    @Override
    public void up(int x, int y) throws IOException {
        Point real = convertToRealPoint(x, y);

        try {
            if(point.equals(real)) {
                device.tap(real.getX(), real.getY());
            } else {
                doSwipe(point.getX(), point.getY(), real.getX(), real.getY(), 100);
            }
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void move(int x, int y) throws IOException {

    }

    @Override
    public void swipe(int x1, int y1, int x2, int y2, int duration) throws IOException {
        Point real1 = convertToRealPoint(x1, y1);
        Point real2 = convertToRealPoint(x2, y2);
        doSwipe(real1.getX(), real1.getY(), real2.getX(), real2.getY(), duration);
    }

    @Override
    public DeviceSize getDeviceSize() {
        return deviceSize;
    }

    @Override
    public void close() {

    }

    private void doSwipe(int x1, int y1, int x2, int y2, int duration) throws IOException {
        try {
            device.swipe(x1, y1, x2, y2, duration);
        } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException e) {
            throw new IOException(e);
        }
    }
}
