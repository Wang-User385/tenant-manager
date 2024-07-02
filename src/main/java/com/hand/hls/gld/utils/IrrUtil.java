//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hls.gld.utils;

import java.util.List;

public class IrrUtil {
    public IrrUtil() {
    }

    public static double irr(List<Double> income) {
        return irr(income, 0.1D);
    }

    public static double irr(List<Double> values, double guess) {
        int maxIterationCount = 100;
        double absoluteAccuracy = 1.0E-7D;
        double x0 = guess;

        for(int i = 0; i < maxIterationCount; ++i) {
            double fValue = 0.0D;
            double fDerivative = 0.0D;

            for(int k = 0; k < values.size(); ++k) {
                fValue += (Double)values.get(k) / Math.pow(1.0D + x0, (double)k);
                fDerivative += (double)(-k) * (Double)values.get(k) / Math.pow(1.0D + x0, (double)(k + 1));
            }

            double x1 = x0 - fValue / fDerivative;
            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                return x1;
            }

            x0 = x1;
        }

        return 0.0D / 0.0;
    }
}
