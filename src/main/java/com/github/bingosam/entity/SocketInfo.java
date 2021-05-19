package com.github.bingosam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/7          </p>
 *
 * @author zhang kunbin
 */
@Data
@AllArgsConstructor
public class SocketInfo {

    private int port;

    private String name;
}
