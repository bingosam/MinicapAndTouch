package com.github.bingosam.device;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.android.ddmlib.*;
import com.github.bingosam.constant.Constants;
import com.github.bingosam.entity.MinicapInfo;
import com.github.bingosam.entity.Size;
import com.github.bingosam.entity.SizeUnmodifiable;
import com.github.bingosam.util.LibUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@Getter
@Log4j2
public class Minicap extends BaseStfService
        implements DeviceSize {

    @Setter
    private int maxHeight;

    private Size outputSize;

    private double scale;

    private final List<Consumer<Size>> sizeConsumers;

    public Minicap(DeviceWrap device) {
        this(device, Collections.emptyList());
    }

    public Minicap(DeviceWrap device, List<Consumer<Size>> sizeConsumers) {
        super(device, Constants.BIN_MINICAP);
        maxHeight = -1;
        this.sizeConsumers = sizeConsumers;
    }

    @Override
    public void init() throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException,
            SyncException {
        if (isInitialized()) {
            return;
        }

        String abi = getDevice().getDevice().getProperty(IDevice.PROP_DEVICE_CPU_ABI);
        String sdk = getDevice().getDevice().getProperty(IDevice.PROP_BUILD_API_LEVEL);
        int apiLevel = Integer.parseInt(sdk);

        getDevice().getDevice().pushFile(LibUtils.getMinicapBin(abi, apiLevel), Constants.REMOTE_PATH_MINICAP);
        getDevice().getDevice().pushFile(
                LibUtils.getMinicapSo(abi, apiLevel),
                Constants.DIR_DEVICE_TEMP + '/' + Constants.SO_MINICAP
        );

        getDevice().getDevice().executeShellCommand("chmod 0755 " + Constants.REMOTE_PATH_MINICAP, NullOutputReceiver.getReceiver());
    }

    @Override
    public String start() throws InterruptedException {
        if (maxHeight > 0 && getDevice().getSize().getHeight() > maxHeight) {
            scale = maxHeight * 1.0 / getDevice().getSize().getHeight();
            outputSize = new SizeUnmodifiable((int) (scale * getDevice().getSize().getWidth()), maxHeight);
        } else {
            scale = 1;
            outputSize = new SizeUnmodifiable(getDevice().getSize());
        }
        getSizeConsumers().forEach(consumer -> consumer.accept(outputSize));

        String cmd = String.format(Constants.CMD_MINICAP + " -P %dx%d@%dx%d/0",
                getDevice().getSize().getWidth(),
                getDevice().getSize().getHeight(),
                outputSize.getWidth(),
                outputSize.getHeight());
        doStart(cmd, new MinicapCmdReceiver());
        return Constants.SOCKET_NAME_MINICAP;
    }

    @Override
    public Size getSize() {
        return getDevice().getSize();
    }

    /**
     * check if minicap is installed
     *
     * @return
     * @throws TimeoutException
     * @throws AdbCommandRejectedException
     * @throws ShellCommandUnresponsiveException
     * @throws IOException
     */
    public boolean isInitialized() throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        return null != getMinicapInfo();
    }

    /**
     * get info of minicap
     *
     * @return
     * @throws TimeoutException
     * @throws AdbCommandRejectedException
     * @throws ShellCommandUnresponsiveException
     * @throws IOException
     */
    public MinicapInfo getMinicapInfo() throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        final StringBuilder sb = new StringBuilder();
        getDevice().getDevice().executeShellCommand(Constants.CMD_MINICAP + " -i",
                new MultiLineReceiver() {
                    private boolean start;
                    private boolean end;

                    @Override
                    public void processNewLines(String[] lines) {
                        for (String line : lines) {
                            if (!start) {
                                start = "{".equals(line);
                            }
                            if (!start) {
                                continue;
                            }
                            if (end) {
                                continue;
                            }
                            sb.append(line);
                            end = "}".equals(line);
                        }
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }
                });
        try {
            return JSON.parseObject(sb.toString(), MinicapInfo.class);
        } catch (JSONException e) {
            log.warn(() -> "Unabled to get info of minicap: " + sb.toString());
            return null;
        }
    }

    private static class MinicapCmdReceiver extends BaseServiceStartReceiver {

        public MinicapCmdReceiver() {
            super(Constants.BIN_MINICAP);
        }

        @Override
        protected boolean isStarted(String line) {
            return line.endsWith("Publishing virtual display");
        }
    }
}
