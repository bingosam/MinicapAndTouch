package com.github.bingosam.device;

import com.android.ddmlib.*;
import com.github.bingosam.concurrent.NamedThreadFactory;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
public class DeviceRmCli implements Closeable {

    private final DeviceWrap device;

    private final Minicap minicap;

    private final Minitouch minitouch;

    private final List<Consumer<Image>> imageConsumers;

    private final ExecutorService threadPool;

    private MinitouchCli minitouchCli;

    private MinicapCli minicapCli;

    @SafeVarargs
    public DeviceRmCli(DeviceWrap device, Consumer<Image>... imageConsumers) {
        this.device = device;
        minicap = new Minicap(this.device);
        minitouch = new Minitouch(this.device);
        this.imageConsumers = Collections.synchronizedList(new ArrayList<>());
        this.imageConsumers.addAll(Arrays.asList(imageConsumers));
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
        minitouch.init();

        String socket = minicap.start();
        minicapCli = new MinicapCli(device, socket, imageConsumers);
        socket = minitouch.start();
        minitouchCli = new MinitouchCli(device, minicap, socket);
        threadPool.submit(minicapCli);
    }

    public void touchDown(int x, int y) throws IOException {
        minitouchCli.down(x, y);
    }

    public void touchUp() throws IOException {
        minitouchCli.up();
    }

    public void touchMove(int x, int y) throws IOException {
        minitouchCli.move(x, y);
    }

    public void registerImageConsumer(Consumer<Image> consumer) {
        imageConsumers.add(consumer);
    }

    @Override
    public void close() throws IOException {
        threadPool.shutdown();
        if (null != minitouchCli) {
            minitouchCli.close();
        }
        if (null != minicapCli) {
            minicapCli.close();
        }
        minitouch.close();
        minicap.close();
    }

}
