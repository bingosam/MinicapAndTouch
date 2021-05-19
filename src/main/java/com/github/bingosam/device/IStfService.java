package com.github.bingosam.device;

import java.io.Closeable;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
public interface IStfService extends Closeable {

    /**
     * init env
     *
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * start service
     *
     * @return socket name
     * @throws InterruptedException
     */
    String start() throws InterruptedException;
}
