package com.github.bingosam.device;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.TimeoutException;
import com.github.bingosam.constant.Constants;
import com.github.bingosam.entity.Point;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/7          </p>
 *
 * @author zhang kunbin
 */
public class MinitouchCli implements DeviceController {

    private static final byte[] COMMAND_COMMIT = "c\n".getBytes();

    private static final byte[] COMMAND_TOUCH_UP = "u 0\n".getBytes();

    private final OutputStream outputStream;

    private final DeviceSize deviceSize;

    public MinitouchCli(DeviceWrap device, DeviceSize deviceSize, String socketName)
            throws AdbCommandRejectedException, IOException, TimeoutException {
        this.deviceSize = deviceSize;
        int port = PortManager.createForward(device.getDevice(), Constants.BIN_MINITOUCH, socketName, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
        Socket socket = new Socket("localhost", port);
        outputStream = socket.getOutputStream();
    }

    @Override
    public void down(int x, int y) throws IOException {
        Point real = convertToRealPoint(x, y);
        executeCommand(String.format("d 0 %d %d 50\n", real.getX(), real.getY()));
    }

    @Override
    public void up(int x, int y) throws IOException {
        executeCommand(COMMAND_TOUCH_UP);
    }

    @Override
    public void move(int x, int y) throws IOException {
        Point real = convertToRealPoint(x, y);
        executeCommand(String.format("m 0 %d %d 50\n", real.getX(), real.getY()));
    }

    @Override
    public void swipe(int x1, int y1, int x2, int y2, int duration) throws IOException {
        down(x1, y1);
        move(x2, y2);
        up(x2, y2);
    }

    @Override
    public DeviceSize getDeviceSize() {
        return deviceSize;
    }

    private void executeCommand(String command) throws IOException {
        executeCommand(command.getBytes());
    }

    private void executeCommand(byte[] command) throws IOException {
        outputStream.write(command);
        outputStream.write(COMMAND_COMMIT);
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        if (null != outputStream) {
            outputStream.close();
        }
    }
}
