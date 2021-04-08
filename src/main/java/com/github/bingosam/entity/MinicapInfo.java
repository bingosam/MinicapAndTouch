package com.github.bingosam.entity;

import lombok.Data;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Company: ND Co., Ltd.       </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@Data
public class MinicapInfo {

    private int id;

    private int width;

    private int height;

    private float xdpi;

    private float ydpi;

    private float size;

    private float density;

    private float fps;

    private boolean secure;

    private int rotation;
}
