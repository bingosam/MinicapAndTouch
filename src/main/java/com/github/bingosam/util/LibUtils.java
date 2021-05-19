package com.github.bingosam.util;

import com.github.bingosam.constant.Constants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>Title: Module Information  </p>
 * <p>Description: Function Description  </p>
 * <p>Copyright: Copyright (c) 2021     </p>
 * <p>Create Time: 2021/4/1          </p>
 *
 * @author zhang kunbin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LibUtils {

    public static String getMinitouchBin(String abi, int sdk) {
        if (sdk < Constants.MAX_API_LEVEL_WITHOUT_PIE) {
            return Constants.DIR_STF_ROOT + '/' + abi + '/' + Constants.BIN_MINITOUCH_NOPIE;
        }
        return Constants.DIR_STF_ROOT + '/' + abi + '/' + Constants.BIN_MINITOUCH;
    }

    public static String getMinicapBin(String abi, int sdk) {
        if (sdk < Constants.MAX_API_LEVEL_WITHOUT_PIE) {
            return Constants.DIR_STF_ROOT + '/' + abi + "/bin/" + Constants.BIN_MINICAP_NOPIE;
        }
        return Constants.DIR_STF_ROOT + '/' + abi + "/bin/" + Constants.BIN_MINICAP;
    }

    public static String getMinicapSo(String abi, int sdk) {
        return Constants.DIR_STF_ROOT + '/' + abi + "/lib/android-" + sdk + '/' + Constants.SO_MINICAP;
    }
}
