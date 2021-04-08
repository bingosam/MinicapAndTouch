package com.github.bingosam.device;

import com.github.bingosam.entity.Size;

import java.awt.*;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Company: ND Co., Ltd.       </p>
 * <p>Create Time: 2021/4/7          </p>
 *
 * @author zhang kunbin
 */
public interface MinicapConsumer {

    void acceptSize(Size size);

    void acceptImage(Image image);
}
