package com.github.bingosam.entity;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
public class SizeUnmodifiable extends Size {

    public SizeUnmodifiable(int width, int height) {
        super(width, height);
    }

    public SizeUnmodifiable(Size size) {
        this(size.getWidth(), size.getHeight());
    }

    @Override
    public void setWidth(int width) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHeight(int height) {
        throw new UnsupportedOperationException();
    }
}
