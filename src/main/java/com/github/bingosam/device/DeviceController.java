package com.github.bingosam.device;

import com.github.bingosam.entity.Point;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/9          </p>
 *
 * @author zhang kunbin
 */
public interface DeviceController extends Closeable {

    void down(int x, int y) throws IOException;

    void up(int x, int y) throws IOException;

    void move(int x, int y) throws IOException;

    void swipe(int x1, int y1, int x2, int y2, int duration) throws IOException;

    DeviceSize getDeviceSize();

    default Point convertToRealPoint(int x, int y) {
        return new Point((int) (x / getDeviceSize().getScale()), (int) (y / getDeviceSize().getScale()));
    }
}
