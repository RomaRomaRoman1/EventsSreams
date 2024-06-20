package org.example.util;

import org.springframework.stereotype.Component;

@Component
public class ArimaHelper {

    public int[] autoSelectARIMAParams(double[] data) {
        int d = autoSelectD(data);
        double[] differencedData = difference(data, d);
        int p = autoSelectP(differencedData);
        int q = autoSelectQ(differencedData);

        return new int[]{p, d, q};
    }

    private int autoSelectD(double[] data) {
        int d = 0;
        while (d < 2 && !isStationary(difference(data, d))) {
            d++;
        }
        return d;
    }

    private boolean isStationary(double[] data) {
        double threshold = 1.96 / Math.sqrt(data.length);
        double[] acf = calculateACF(data);
        for (int i = 1; i < acf.length; i++) {
            if (Math.abs(acf[i]) > threshold) {
                return false;
            }
        }
        return true;
    }

    private double[] difference(double[] data, int order) {
        if (order == 0) {
            return data;
        }
        double[] diff = new double[data.length - order];
        for (int i = order; i < data.length; i++) {
            diff[i - order] = data[i] - data[i - order];
        }
        return diff;
    }

    private double[] calculateACF(double[] data) {
        int n = data.length;
        double[] acf = new double[n];
        double mean = calculateMean(data);

        for (int lag = 0; lag < n; lag++) {
            double numerator = 0.0;
            double denominator = 0.0;

            for (int i = 0; i < n - lag; i++) {
                numerator += (data[i] - mean) * (data[i + lag] - mean);
                denominator += Math.pow(data[i] - mean, 2);
            }

            acf[lag] = numerator / denominator;
        }

        return acf;
    }

    private double[] calculatePACF(double[] data) {
        int n = data.length;
        double[] pacf = new double[n];
        double[] acf = calculateACF(data);

        pacf[0] = 1.0;

        for (int k = 1; k < n; k++) {
            double numerator = acf[k];
            for (int j = 1; j < k; j++) {
                numerator -= pacf[j] * acf[k - j];
            }
            pacf[k] = numerator / (1.0 - pacf[k - 1] * acf[k]);
        }

        return pacf;
    }

    private int autoSelectP(double[] data) {
        double[] pacf = calculatePACF(data);
        return findSignificantLag(pacf);
    }

    private int autoSelectQ(double[] data) {
        double[] acf = calculateACF(data);
        return findSignificantLag(acf);
    }

    private int findSignificantLag(double[] acfOrPacf) {
        for (int i = 1; i < acfOrPacf.length; i++) {
            if (Math.abs(acfOrPacf[i]) > 1.96 / Math.sqrt(acfOrPacf.length)) {
                return i;
            }
        }
        return 0;
    }

    private double calculateMean(double[] data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }

    public class ArimaModel {
        private final int p;
        private final int d;
        private final int q;
        private double[] coefficients;

        public ArimaModel(int p, int d, int q) {
            this.p = p;
            this.d = d;
            this.q = q;
        }

        public void fit(double[] data) {
            double[] diffData = difference(data, d);

            coefficients = new double[p + q + 1];
            for (int i = 0; i < p; i++) {
                coefficients[i] = calculateMean(diffData);
            }

            for (int i = 0; i < q; i++) {
                coefficients[p + i] = calculateMean(diffData);
            }
        }

        public double[] forecast(double[] data, int steps) {
            double[] forecast = new double[steps];
            for (int i = 0; i < steps; i++) {
                forecast[i] = coefficients[0];
            }
            return forecast;
        }

        private double[] difference(double[] data, int order) {
            double[] diff = new double[data.length - order];
            for (int i = order; i < data.length; i++) {
                diff[i - order] = data[i] - data[i - order];
            }
            return diff;
        }
    }
}