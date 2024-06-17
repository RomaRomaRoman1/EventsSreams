package org.example.util;

import org.springframework.stereotype.Component;

@Component
public class ArimaHelper {

    // Метод для автоматического выбора параметров p, d, q
    public int[] autoSelectARIMAParams(double[] data) {
        int d = autoSelectD(data);
        double[] differencedData = difference(data, d);
        int p = autoSelectP(differencedData);
        int q = autoSelectQ(differencedData);

        return new int[]{p, d, q};
    }

    // Метод для автоматического выбора параметра d
    private int autoSelectD(double[] data) {
        int d = 0;
        while (!isStationary(difference(data, d))) {
            d++;
        }
        return d;
    }

    private boolean isStationary(double[] data) {
        // Вычисление порога для автокорреляции. Порог определяется как 1.96 / sqrt(длина данных)
        // 1.96 - это критическое значение для 95% доверительного интервала нормального распределения.
        double threshold = 1.96 / Math.sqrt(data.length);
        // Вычисление автокорреляционной функции (ACF) для данных
        double[] acf = calculateACF(data);
        // Проверка всех значений автокорреляционной функции, начиная со второго (i = 1)
        // Если абсолютное значение автокорреляции превышает порог, ряд считается нестационарным.
        for (int i = 1; i < acf.length; i++) {
            if (Math.abs(acf[i]) > threshold) {
                return false;
            }
        }
        // Если ни одно значение автокорреляции не превышает порог, ряд считается стационарным.
        return true;
    }

    // Метод для разностных преобразований
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

    // Метод для вычисления ACF
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

    // Метод для вычисления PACF
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

    // Метод для автоматического выбора параметра p
    private int autoSelectP(double[] data) {
        double[] pacf = calculatePACF(data);
        return findSignificantLag(pacf);
    }

    // Метод для автоматического выбора параметра q
    private int autoSelectQ(double[] data) {
        double[] acf = calculateACF(data);
        return findSignificantLag(acf);
    }

    // Метод для нахождения значимого лага
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
}
