package com.github.bingosam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class Size {
    private int width;

    private int height;

    public boolean isValid() {
        return width > 0 && height > 0;
    }
}
