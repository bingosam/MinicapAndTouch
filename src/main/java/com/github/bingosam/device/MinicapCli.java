package com.github.bingosam.device;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.TimeoutException;
import com.github.bingosam.concurrent.NamedThreadFactory;
import com.github.bingosam.constant.Constants;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Company: ND Co., Ltd.       </p>
 * <p>Create Time: 2021/4/7          </p>
 *
 * @author zhang kunbin
 */
@Log4j2
public class MinicapCli implements Closeable, Runnable {

    private final InputStream stream;

    private final List<Consumer<Image>> imageConsumers;

    private final ExecutorService threadPool;

    private final Socket socket;

    public MinicapCli(DeviceWrap device, String socketName, List<Consumer<Image>> imageConsumers)
            throws AdbCommandRejectedException, IOException, TimeoutException {
        this.imageConsumers = imageConsumers;
        this.threadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new NamedThreadFactory("dev-" + device.getDevice().getSerialNumber() + "-minicap-image"));
        int port = PortManager.createForward(device.getDevice(), Constants.BIN_MINICAP, socketName, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
        socket = new Socket("localhost", port);
        stream = socket.getInputStream();
    }

    @Override
    public void close() throws IOException {
        threadPool.shutdown();
        if (null != stream) {
            stream.close();
        }
    }

    @Override
    public void run() {
        MinicapStreamReader reader = new MinicapStreamReader();
        try {
            reader.tryRead();
        } catch (IOException e) {
            if(socket.isClosed()) {
                return;
            }
            log.error("Read stream from minicap", e);
        }
    }

    private class MinicapStreamReader {

        private Banner banner = new Banner();

        private int readBannerBytes;

        private int bannerLength = 2;

        private int readFrameBytes;

        private int frameBodyLength;

        private byte[] frameBody = new byte[0];

        private void tryRead() throws IOException {
            byte[] buffer = new byte[4096];
            while (!Thread.currentThread().isInterrupted()) {
                int len = stream.read(buffer);
                if (len > 0) {
                    for (int cursor = 0; cursor < len; ) {
                        int val = buffer[cursor] & 0xFF;
                        if (readBannerBytes < bannerLength) {
                            readBanner(val);
                            readBannerBytes += 1;
                            cursor += 1;
                        } else if (readFrameBytes < 4) {
                            frameBodyLength += (val << (readFrameBytes * 8)) >>> 0;
                            readFrameBytes += 1;
                            cursor += 1;
                        } else {
                            if (len - cursor >= frameBodyLength) {
                                byte[] sub = ArrayUtils.subarray(buffer, cursor, cursor + frameBodyLength);
                                final byte[] imageBytes = ArrayUtils.addAll(frameBody, sub);
                                consumeImageBytes(imageBytes);

                                cursor += frameBodyLength;
                                frameBodyLength = 0;
                                readFrameBytes = 0;
                                frameBody = new byte[0];
                            } else {
                                byte[] sub = ArrayUtils.subarray(buffer, cursor, len);
                                frameBody = ArrayUtils.addAll(frameBody, sub);
                                frameBodyLength -= len - cursor;
                                readFrameBytes += len - cursor;
                                cursor = len;
                            }
                        }
                    }
                }
            }
        }

        private void readBanner(int value) {
            switch (readBannerBytes) {
                case 0:
                    banner.version = value;
                    break;
                case 1:
                    banner.length = value;
                    bannerLength = value;
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    banner.pid += (value << ((readBannerBytes - 2) * 8)) >>> 0;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                    banner.realWidth += (value << ((readBannerBytes - 6) * 8)) >>> 0;
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                    banner.realHeight += (value << ((readBannerBytes - 10) * 8)) >>> 0;
                    break;
                case 14:
                case 15:
                case 16:
                case 17:
                    banner.virtualWidth += (value << ((readBannerBytes - 14) * 8)) >>> 0;
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                    banner.virtualHeight += (value << ((readBannerBytes - 18) * 8)) >>> 0;
                    break;
                case 22:
                    banner.orientation += value * 90;
                    break;
                case 23:
                    banner.quirks = value;
                    break;
            }
        }
    }

    private void consumeImageBytes(final byte[] imageBytes) throws IOException {
        if (imageBytes[0] != (byte) 0xFF || imageBytes[1] != (byte) 0xD8) {
            throw new IOException("Frame body does not start with JPG header");
        }

        threadPool.submit(() -> {
            try {
                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                if (null == image) {
                    return;
                }
                imageConsumers.forEach(consumer -> consumer.accept(image));
            } catch (IOException e) {
                log.error("Failed to read image bytes", e);
            }
        });
    }

    @Data
    private static class Banner {
        private int version;

        private int length;

        private int pid;

        private int realWidth;

        private int realHeight;

        private int virtualWidth;

        private int virtualHeight;

        private int orientation;

        private int quirks;
    }
}
