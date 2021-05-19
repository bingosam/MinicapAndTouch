package com.github.bingosam.device;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
public abstract class BaseDeviceTest {

    protected DeviceWrap device;

    @Before
    public void init() throws InterruptedException,
            TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        AndroidDebugBridge.init(false);
        AndroidDebugBridge adb = AndroidDebugBridge.createBridge("adb", true);
        while (!adb.hasInitialDeviceList()) {
            Thread.sleep(500L);
        }
        device = new DeviceWrap(adb.getDevices()[0]);
    }

    @After
    public void destroy() {
        AndroidDebugBridge.terminate();
    }
}
