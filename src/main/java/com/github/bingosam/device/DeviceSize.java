package com.github.bingosam.device;

import com.github.bingosam.entity.Size;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
public interface DeviceSize {

    Size getSize();

    Size getOutputSize();

    /**
     * output / real
     *
     * @return
     */
    double getScale();
}
