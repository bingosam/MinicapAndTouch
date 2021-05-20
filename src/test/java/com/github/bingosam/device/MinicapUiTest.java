package com.github.bingosam.device;

import com.android.ddmlib.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/7          </p>
 *
 * @author zhang kunbin
 */
@Log4j2
public class MinicapUiTest extends BaseDeviceTest {

    @Test
    public void test() throws AdbCommandRejectedException,
            InterruptedException,
            SyncException,
            ShellCommandUnresponsiveException,
            IOException,
            TimeoutException,
            InstallException {
        connectDevice(device);
    }

    private static void connectDevice(DeviceWrap device) throws IOException, AdbCommandRejectedException, InterruptedException, SyncException, ShellCommandUnresponsiveException, TimeoutException, InstallException {
        try (DeviceRmCli cli = new DeviceRmCli(device)) {
            JPanel panel = new JPanel();

            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @SneakyThrows
                @Override
                public void mousePressed(MouseEvent e) {
                    cli.touchDown(e.getX(), e.getY());
                }

                @SneakyThrows
                @Override
                public void mouseReleased(MouseEvent e) {
                    cli.touchUp();
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            panel.addMouseMotionListener(new MouseMotionListener() {
                @SneakyThrows
                @Override
                public void mouseDragged(MouseEvent e) {
                    cli.touchMove(e.getX(), e.getY());
                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            });


            JFrame ui = new JFrame();
            ui.setResizable(false);
            ui.setLayout(new BorderLayout());
            ui.add(panel, BorderLayout.CENTER);
            ui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JPanel btnPanel = new JPanel();
            ui.add(btnPanel, BorderLayout.SOUTH);
            addKeyEventButton(btnPanel, "Back", device, 4);
            addKeyEventButton(btnPanel, "Home", device, 3);
            addKeyEventButton(btnPanel, "Power", device, 26);

            cli.registerImageConsumer(image -> {
                int w = image.getWidth(null);
                int h = image.getHeight(null);
                ui.setSize(w, h + 70);
                panel.setSize(w, h);
                panel.getGraphics().drawImage(
                        image, 0, 0, null
                );
            });
            cli.start(720);
            ui.setVisible(true);
            ui.pack();
            while (true) {
                Thread.sleep(500);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException,
            AdbCommandRejectedException,
            SyncException,
            ShellCommandUnresponsiveException,
            IOException,
            TimeoutException,
            InstallException {
        String deviceId = "";
        if (args.length > 0) {
            deviceId = args[0];
        }

        Adb adb = new Adb();
        IDevice device = adb.getDevice(deviceId);
        DeviceWrap deviceWrap = new DeviceWrap(device);
        connectDevice(deviceWrap);
    }

    private static void addKeyEventButton(JPanel panel, String text, DeviceWrap device, int keyEvent) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            try {
                device.sendKeyEvent(keyEvent);
            } catch (Exception ex) {
                log.error("Failed to send key event: " + keyEvent, e);
            }
        });
        panel.add(button);
    }

    public static class Adb {

        private AndroidDebugBridge androidDebugBridge;

        public Adb() throws InterruptedException {
            AndroidDebugBridge.init(false);
            androidDebugBridge = AndroidDebugBridge.createBridge("adb", true);
            while (!androidDebugBridge.hasInitialDeviceList()) {
                Thread.sleep(500L);
            }
        }

        public IDevice getDevice(String deviceId) {
            IDevice[] devices = androidDebugBridge.getDevices();
            if (null == deviceId || deviceId.trim().isEmpty()) {
                return devices[0];
            }
            for (IDevice dev : devices) {
                if (dev.getSerialNumber().equals(deviceId)) {
                    return dev;
                }
            }
            return null;
        }
    }

}
