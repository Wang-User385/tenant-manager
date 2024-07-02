//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.sadk.algorithm.common.CBCParam;
import cfca.sadk.algorithm.common.MechanismKit;

public final class Mechanism extends MechanismKit {
    public Mechanism(String mechanismType, byte[] ivBytes) {
        super(mechanismType, ivBytes == null ? null : new CBCParam(ivBytes));
    }

    public Mechanism(String mechanismType, Object param, String curveName) {
        super(mechanismType, param, curveName);
    }

    public Mechanism(String mechanismType, Object param) {
        super(mechanismType, param);
    }

    public Mechanism(String mechanismType) {
        super(mechanismType);
    }
}
