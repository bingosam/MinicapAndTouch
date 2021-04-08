package com.github.bingosam.constant;

import lombok.AccessLevel;
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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String DIR_LIBS_ROOT = "libs";

    public static final String DIR_STF_ROOT = DIR_LIBS_ROOT + "/stf";

    public static final String DIR_DEVICE_TEMP = "/data/local/tmp";

    /**
     * not support pie
     */
    public static final int MAX_API_LEVEL_WITHOUT_PIE = 16;

    /**
     * minicap constants
     */
    public static final String BIN_MINICAP = "minicap";

    public static final String BIN_MINICAP_NOPIE = "minicap-nopie";

    public static final String SO_MINICAP = "minicap.so";

    public static final String REMOTE_PATH_MINICAP = Constants.DIR_DEVICE_TEMP + '/' + Constants.BIN_MINICAP;

    public static final String CMD_MINICAP = "LD_LIBRARY_PATH=" + Constants.DIR_DEVICE_TEMP + " " + REMOTE_PATH_MINICAP;

    public static final String SOCKET_NAME_MINICAP = "minicap";


    /**
     * minitouch constants
     */
    public static final String BIN_MINITOUCH = "minitouch";

    public static final String BIN_MINITOUCH_NOPIE = "minitouch-nopie";

    public static final String REMOTE_PATH_MINITOUCH = Constants.DIR_DEVICE_TEMP + '/' + Constants.BIN_MINITOUCH;

    public static final String CMD_MINITOUCH = REMOTE_PATH_MINITOUCH;

    public static final String SOCKET_NAME_MINITOUCH = "minitouch";

    /**
     * minitouch no support Android 10 and up, use minitouchagent instead
     */
    public static final int MIN_API_LEVEL_TOUCH_AGENT = 29;

    /**
     * STFService constants
     */
    public static final String APK_STF_SERVICE = DIR_STF_ROOT + "/STFService.apk";

    public static final String PKG_STF_SERVICE = "jp.co.cyberagent.stf";

    public static final String CLS_STF_AGENT = PKG_STF_SERVICE + ".Agent";

    public static final String SOCKET_NAME_MINITOUCH_AGENT = "minitouchagent";

}
