package com.github.bingosam.device;

import com.android.ddmlib.*;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@Log4j2
public class DeviceRmCliTest extends BaseDeviceTest {

    @Test
    public void testOpenMinicapAndSaveImages() throws IOException, AdbCommandRejectedException, InterruptedException, SyncException, ShellCommandUnresponsiveException, TimeoutException, InstallException {
        AtomicInteger index = new AtomicInteger(0);
        try (DeviceRmCli cli = new DeviceRmCli(device, image -> {
            try {
                ImageIO.write((BufferedImage) image, "jpg", new File(index.getAndIncrement() + ".jpg"));
            } catch (IOException e) {
                log.error("write image", e);
            }
        })) {
            cli.start(720);
            Thread.sleep(5000L);
        }
    }
}