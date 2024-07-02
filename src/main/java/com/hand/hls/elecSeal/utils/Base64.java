//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

import cfca.sadk.org.bouncycastle.util.FastBase64;
import cfca.sadk.org.bouncycastle.util.encoders.Base64Kit;
import cfca.sadk.system.Environments;
import java.io.IOException;
import java.io.OutputStream;

public class Base64 extends Base64Kit {
    public Base64() {
    }

    public static byte[] encode(byte[] data) {
        return FastBase64.encode(data);
    }

    public static int encode(byte[] data, OutputStream out) throws IOException {
        return Base64Kit.encode(data, 0, data.length, out);
    }

    public static int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        return Base64Kit.encode(data, off, length, out);
    }

    public static byte[] decode(byte[] data) {
        try {
            return FastBase64.decode(data);
        } catch (IOException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    public static byte[] decode(String base64String) {
        return Base64Kit.decode(base64String);
    }

    public static int decode(String base64String, OutputStream out) throws IOException {
        return Base64Kit.decode(base64String, out);
    }

}
