package com.github.bingosam.device;

import com.android.ddmlib.MultiLineReceiver;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Company: ND Co., Ltd.       </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@Log4j2
public abstract class BaseServiceStartReceiver extends MultiLineReceiver {

    /**
     * Judge whether the service is started successfully
     *
     * @param line output
     * @return
     */
    protected abstract boolean isStarted(String line);

    protected void peekLine(String line) {
        //do nothing;
    }

    private final Semaphore semaphore;

    @Getter
    private final String serviceName;

    private boolean started = false;

    private AtomicBoolean cancelled = new AtomicBoolean(false);

    public BaseServiceStartReceiver(String serviceName) {
        semaphore = new Semaphore(0);
        this.serviceName = serviceName;
    }

    @Override
    public void processNewLines(String[] lines) {
        for (String line : lines) {
            log.info(() -> serviceName + " :" + line);
            peekLine(line);
            if (!started && isStarted(line)) {
                log.info(() -> serviceName + " started .");
                started = true;
                semaphore.release();
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    public void cancel() {
        log.info("Close " + serviceName);
        cancelled.set(true);
    }

    /**
     * waiting for the service to start successfully
     *
     * @param timeout
     * @param unit
     * @return
     */
    public boolean waitForStart(long timeout, TimeUnit unit) throws InterruptedException {
        return semaphore.tryAcquire(timeout, unit);
    }
}
