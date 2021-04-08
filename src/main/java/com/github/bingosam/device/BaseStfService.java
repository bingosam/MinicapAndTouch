package com.github.bingosam.device;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import com.github.bingosam.concurrent.NamedThreadFactory;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Company: ND Co., Ltd.       </p>
 * <p>Create Time: 2021/4/2          </p>
 *
 * @author zhang kunbin
 */
@Log4j2
public abstract class BaseStfService implements IStfService {

    @Getter
    protected final DeviceWrap device;

    private ExecutorService threadPool;

    private final Object closeLock = new Object();

    private boolean closed;

    private BaseServiceStartReceiver serviceStartReceiver;

    public BaseStfService(DeviceWrap device, String serviceName) {
        this.device = device;
        this.threadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(),
                new NamedThreadFactory("dev-" + device.getDevice().getSerialNumber() + '-' + serviceName));
    }

    protected void closeOthers() throws IOException {
        //for sub-class
    }

    /**
     * @param cmd
     * @param receiver
     * @return local socket port
     * @throws InterruptedException
     */
    protected void doStart(String cmd, BaseServiceStartReceiver receiver)
            throws InterruptedException {
        serviceStartReceiver = receiver;
        threadPool.submit(() -> {
            try {
                log.info("start " + receiver.getServiceName());
                device.getDevice().executeShellCommand(cmd, receiver, 0, TimeUnit.MILLISECONDS);
                log.info(receiver.getServiceName() + " stopped .");
            } catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
                log.error("Failed to start " + receiver.getServiceName() + " service .", e);
            }
        });
        if (!receiver.waitForStart(3, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Failed to start " + receiver.getServiceName());
        }
        log.info(receiver.getServiceName() + " start success .");
    }

    public boolean isClosed() {
        synchronized (closeLock) {
            return closed;
        }
    }

    @Override
    public synchronized void close() throws IOException {
        synchronized (closeLock) {
            if (isClosed()) {
                return;
            }
            if (serviceStartReceiver != null) {
                serviceStartReceiver.cancel();
            }
            threadPool.shutdown();
            closeOthers();
            closed = true;
        }
    }
}
