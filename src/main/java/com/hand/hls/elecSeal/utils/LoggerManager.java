//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoggerManager {
    public static final Logger debugLogger = LoggerFactory.getLogger("sadk.debugLogger");
    public static final Logger systemLogger = LoggerFactory.getLogger("sadk.systemLogger");
    public static final Logger environmentLogger = LoggerFactory.getLogger("sadk.environmentLogger");
    public static final Logger exceptionLogger = LoggerFactory.getLogger("sadk.exceptionLogger");
    public static final Logger timeoutLogger = LoggerFactory.getLogger("sadk.timeoutLogger");

    public LoggerManager() {
    }
}
