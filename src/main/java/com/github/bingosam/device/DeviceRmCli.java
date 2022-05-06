package com.github.bingosam.device;

import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.*;
import com.github.bingosam.concurrent.NamedThreadFactory;
import com.github.bingosam.entity.Size;
import lombok.extern.log4j.Log4j2;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@Log4j2
public class DeviceRmCli implements Closeable {

    private final DeviceWrap device;

    private final Minicap minicap;

    private final Minitouch minitouch;

    private final List<Consumer<Image>> imageConsumers;

    private final List<Consumer<Size>> sizeConsumers;

    private final ExecutorService threadPool;

    private DeviceController deviceController;

    private MinicapCli minicapCli;

    @SafeVarargs
    public DeviceRmCli(DeviceWrap device, Consumer<Image>... imageConsumers) {
        this(device, imageConsumers, new Consumer[0]);
    }

    public DeviceRmCli(DeviceWrap device, Consumer<Image>[] imageConsumers, Consumer<Size>[] sizeConsumers) {
        this.device = device;
        this.imageConsumers = Collections.synchronizedList(new ArrayList<>());
        this.imageConsumers.addAll(Arrays.asList(imageConsumers));
        this.sizeConsumers = Collections.synchronizedList(new ArrayList<>());
        this.sizeConsumers.addAll(Arrays.asList(sizeConsumers));
        minicap = new Minicap(this.device, this.sizeConsumers);
        minitouch = new Minitouch(this.device);
        this.threadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new NamedThreadFactory("dev-" + device.getDevice().getSerialNumber() + "-minicap-reader"));
    }

    public void start(int maxHeight) throws
            IOException,
            TimeoutException,
            AdbCommandRejectedException,
            ShellCommandUnresponsiveException,
            InstallException,
            InterruptedException,
            SyncException {
        minicap.setMaxHeight(maxHeight);
        minicap.init();

        boolean initMiniTouchAgentFailed = false;
        try {
            minitouch.init();
        } catch (IllegalStateException e) {
            if (minitouch.isUseTouch()) {
                throw e;
            }
            initMiniTouchAgentFailed = true;
            log.error("Failed to init minitouch.", e);
        }

        String socket = minicap.start();
        minicapCli = new MinicapCli(device, socket, imageConsumers);
        if (initMiniTouchAgentFailed) {
            deviceController = new AdbCli(device, minicap);
        } else {
            socket = minitouch.start();
            deviceController = new MinitouchCli(device, minicap, socket);
        }
        Future<?> task = threadPool.submit(minicapCli);
        try {
            task.get(300, TimeUnit.MILLISECONDS);
            throw new IOException("The thread of minicap receiver shutdown abnormal.");
        } catch (ExecutionException | java.util.concurrent.TimeoutException e) {
            //
        }
    }

    public void touchDown(int x, int y) throws IOException {
        deviceController.down(x, y);
    }

    public void touchUp(int x, int y) throws IOException {
        deviceController.up(x, y);
    }

    public void touchMove(int x, int y) throws IOException {
        deviceController.move(x, y);
    }

    public void swipe(int x1, int y1, int x2, int y2, int duration) throws IOException {
        deviceController.swipe(x1, y1, x2, y2, duration);
    }

    public void registerImageConsumer(Consumer<Image> consumer) {
        imageConsumers.add(consumer);
    }

    public void registerSizeConsumer(Consumer<Size> consumer) {
        sizeConsumers.add(consumer);
    }

    public boolean isUseMinitouch() {
        return deviceController instanceof MinitouchCli;
    }

    @Override
    public void close() throws IOException {
        threadPool.shutdown();
        if (null != deviceController) {
            deviceController.close();
        }
        if (null != minicapCli) {
            minicapCli.close();
        }
        minitouch.close();
        minicap.close();
    }

}
