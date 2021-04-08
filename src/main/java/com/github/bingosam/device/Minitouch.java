package com.github.bingosam.device;

import com.android.ddmlib.*;
import com.github.bingosam.constant.Constants;
import com.github.bingosam.util.LibUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Company: ND Co., Ltd.       </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@Getter
@Log4j2
public class Minitouch extends BaseStfService {

    private String stfServiceInstalledPath;

    public Minitouch(DeviceWrap device) {
        super(device, Constants.BIN_MINITOUCH);
    }

    @Override
    public void init() throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException,
            SyncException,
            InstallException {
        String sdk = device.getDevice().getProperty(IDevice.PROP_BUILD_API_LEVEL);
        int apiLevel = Integer.parseInt(sdk);
        if (isInitialized(apiLevel)) {
            return;
        }

        if (apiLevel < Constants.MIN_API_LEVEL_TOUCH) {
            String abi = device.getDevice().getProperty(IDevice.PROP_DEVICE_CPU_ABI);
            device.getDevice().pushFile(LibUtils.getMinitouchBin(abi, apiLevel), Constants.REMOTE_PATH_MINITOUCH);
            device.getDevice().executeShellCommand("chmod 0755 " + Constants.REMOTE_PATH_MINITOUCH, NullOutputReceiver.getReceiver());
            return;
        }

        if (null == stfServiceInstalledPath) {
            device.getDevice().installPackage(Constants.APK_STF_SERVICE, true);
            stfServiceInstalledPath = getStfServiceInstalledPath();
            if (null == stfServiceInstalledPath) {
                throw new IllegalStateException("Failed to install STFService.apk");
            }
        }
    }

    @Override
    public String start() throws InterruptedException {
        String socketName;
        String cmd;
        BaseServiceStartReceiver startReceiver;
        if (null == stfServiceInstalledPath) {
            startReceiver = new MinitouchReceiver();
            socketName = Constants.SOCKET_NAME_MINITOUCH;
            cmd = Constants.CMD_MINITOUCH;
        } else {
            startReceiver = new StfAgentReceiver();
            socketName = Constants.SOCKET_NAME_MINITOUCH_AGENT;
            cmd = "export CLASSPATH=\"" + stfServiceInstalledPath + "\"; exec app_process /system/bin " + Constants.CLS_STF_AGENT;
        }
        doStart(cmd, startReceiver);
        return socketName;
    }

    private boolean isInitialized(int apiLevel) throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        if (apiLevel < Constants.MIN_API_LEVEL_TOUCH) {
            //use minitouch
            CollectingOutputReceiver receiver = new CollectingOutputReceiver();
            device.getDevice().executeShellCommand(
                    Constants.REMOTE_PATH_MINITOUCH + " -h", receiver
            );
            String output = receiver.getOutput().trim();
            return output.startsWith("Usage");
        }

        //use minitouch agent instead
        stfServiceInstalledPath = getStfServiceInstalledPath();
        return null != stfServiceInstalledPath;
    }

    private String getStfServiceInstalledPath() throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        CollectingOutputReceiver receiver = new CollectingOutputReceiver();
        device.getDevice().executeShellCommand("pm path " + Constants.PKG_STF_SERVICE, receiver);
        String output = receiver.getOutput();
        if (output.startsWith("package:")) {
            return output.split(":")[1].trim();
        }
        return null;
    }

    public static class StfAgentReceiver extends BaseServiceStartReceiver {

        public StfAgentReceiver() {
            super("minitouch agent");
        }

        @Override
        protected boolean isStarted(String line) {
            return line.equals("Listening on @stfagent");
        }

        @Override
        public void cancel() {
            super.cancel();
            //TODO how to check the stf agent is terminated
            try {
                //waiting for shutdown
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                //do nothing
            }
        }
    }

    public static class MinitouchReceiver extends BaseServiceStartReceiver {

        public MinitouchReceiver() {
            super(Constants.BIN_MINITOUCH);
        }

        @Override
        protected boolean isStarted(String line) {
            return line.contains("detected on");
        }
    }
}
