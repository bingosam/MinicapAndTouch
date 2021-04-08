package com.github.bingosam.device;

import com.android.ddmlib.*;
import com.github.bingosam.entity.Size;
import com.github.bingosam.entity.SizeUnmodifiable;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DeviceWrap {

    private final IDevice device;

    private final Size size;

    public DeviceWrap(IDevice device) throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        this.device = device;

        size = new SizeUnmodifiable(loadSize());
    }

    public void sendKeyEvent(int value) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        String cmd = String.format("input keyevent %d", value);
        device.executeShellCommand(cmd, NullOutputReceiver.getReceiver());
    }

    private Size loadSize() throws TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            IOException {
        BaseDisplayInfoReceiver receiver = new DumpsysDisplayReceiver();
        device.executeShellCommand("dumpsys display", receiver);
        if (receiver.getSize().isValid()) {
            return receiver.getSize();
        }

        receiver = new DumpsysWindowReceiver();
        device.executeShellCommand("dumpsys window", receiver);
        if (receiver.getSize().isValid()) {
            return receiver.getSize();
        }

        receiver = new WmSizeReceiver();
        device.executeShellCommand("wm size", receiver);
        if (receiver.getSize().isValid()) {
            return receiver.getSize();
        }
        throw new IllegalStateException("Unable to get display info");
    }

    @Getter
    public abstract static class BaseDisplayInfoReceiver extends MultiLineReceiver {

        private final Size size = new Size();

        @Override
        public boolean isCancelled() {
            return size.isValid();
        }
    }

    public static class DumpsysDisplayReceiver extends BaseDisplayInfoReceiver {

        private static final Pattern DISPLAY_DEVICE = Pattern.compile("^mPhys=PhysicalDisplayInfo\\{(\\d+) x (\\d+).*\\}");

        @Override
        public void processNewLines(String[] lines) {
            for (String line : lines) {
                Matcher m = DISPLAY_DEVICE.matcher(line);
                if (m.matches()) {
                    try {
                        getSize().setWidth(Integer.parseInt(m.group(1)));
                    } catch (NumberFormatException e) {
                        log.warn("DisplayDevice width: Failed to parse " + m.group(1) + " as an integer");
                    }

                    try {
                        getSize().setHeight(Integer.parseInt(m.group(2)));
                    } catch (NumberFormatException e) {
                        log.warn("DisplayDevice height: Failed to parse " + m.group(2) + " as an integer");
                    }
                    if (isCancelled()) {
                        break;
                    }
                }
            }
        }

    }

    public static class DumpsysWindowReceiver extends BaseDisplayInfoReceiver {
        private static final Pattern INIT = Pattern.compile(".*init=(\\d+)x(\\d+)\\s+.*");

        @Override
        public void processNewLines(String[] lines) {
            for (String line : lines) {
                Matcher m = INIT.matcher(line);
                if (m.matches()) {
                    try {
                        getSize().setWidth(Integer.parseInt(m.group(1)));
                    } catch (NumberFormatException e) {
                        log.warn("Display Width: Failed to parse " + m.group(1) + " as an integer");
                    }

                    try {
                        getSize().setHeight(Integer.parseInt(m.group(2)));
                    } catch (NumberFormatException e) {
                        log.warn("Display Height: Failed to parse " + m.group(2) + " as an integer");
                    }
                    if (isCancelled()) {
                        break;
                    }
                }
            }
        }
    }

    public static class WmSizeReceiver extends BaseDisplayInfoReceiver {

        private static final Pattern PHY_SIZE = Pattern.compile("Physical size: (\\d+)x(\\d+)");

        @Override
        public void processNewLines(String[] lines) {
            for (String line : lines) {
                Matcher m = PHY_SIZE.matcher(line);
                if (m.matches()) {
                    try {
                        getSize().setWidth(Integer.parseInt(m.group(1)));
                    } catch (NumberFormatException e) {
                        log.warn("wm size: Failed to parse " + m.group(1) + " as an integer");
                    }

                    try {
                        getSize().setHeight(Integer.parseInt(m.group(2)));
                    } catch (NumberFormatException e) {
                        log.warn("wm size: Failed to parse " + m.group(2) + " as an integer");
                    }
                    if (isCancelled()) {
                        break;
                    }
                }
            }
        }
    }
}
