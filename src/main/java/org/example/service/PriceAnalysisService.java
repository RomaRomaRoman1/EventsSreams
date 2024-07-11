package org.example.service;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.example.entity.PriceRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceAnalysisService {
    // Метод для расчета средней цены
    public double calculateAveragePrice(List<PriceRecord> records) {
        return records.stream().mapToDouble(PriceRecord::getValue).average().orElse(0.0);
        //Преобразуем каждый элемент коллекции в double, вычисляем среднее значение всех элементов, orElse(0.0) — это метод
        //класса OptionalDouble, который используется для обработки случая, когда среднее значение не может быть вычислено
        //(например, если поток пустой). В этом случае вместо возвращения пустого OptionalDouble, возвращается значение по умолчанию — 0.0.
    }

    // Метод для обнаружения аномалий
    public List<PriceRecord> detectAnomalies(List<PriceRecord> records, double anomalyCoefficient) {
        double average = calculateAveragePrice(records);
        return records.stream()
                .filter(record -> Math.abs(record.getValue() - average) / average > anomalyCoefficient / 100.0)
                .collect(Collectors.toList());
    }

    // Метод для расчета простой скользящей средней (SMA), на вход передаем список всех цен с датами и период (количество дней)
    public Double predictNextSMA(List<PriceRecord> records, int period) {
        if (records.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate SMA");
        }
        // Инициализируем переменную для хранения суммы цен за последний период
        double sum = 0.0;
        // Проходим по последним записям в текущем окне (от size - period до size - 1)
        for (int i = records.size() - period; i < records.size(); i++) {
            // Добавляем значение текущей записи к сумме
            sum += records.get(i).getValue();
        }
        // Возвращаем среднее значение для последнего окна, которое будет предсказанным значением
        return sum / period;
    }

    // Метод для расчета экспоненциальной скользящей средней (EMA)
    public Double predictNextEMA(List<PriceRecord> records, int period) {
        if (records.size() < period) {
            throw new IllegalArgumentException("Not enough data to calculate EMA");
        }
        // Вычисляем множитель для EMA
        double multiplier = 2.0 / (period + 1);
        // Вычисляем первое значение EMA как среднее значение для первого периода
        double previousEMA = calculateAveragePrice(records.subList(0, period));
        // Проходим по остальным записям после первого периода
        for (int i = period; i < records.size(); i++) {
            // Получаем текущее значение цены
            double currentValue = records.get(i).getValue();
            // Вычисляем текущее значение EMA
            previousEMA = (currentValue - previousEMA) * multiplier + previousEMA;
        }
        // Возвращаем последнее значение EMA, которое будет предсказанным значением
        return previousEMA;
    }

    // Метод для предсказания цены с помощью линейной регрессии
    public double predictNextPriceWithLinearRegression(List<PriceRecord> records) {
        //Определение размера списка
        int n = records.size();
        // массив для хранения значений цен из списка records.
        double[] y = new double[n];
        //массив для хранения индексов элементов из списка records.
        double[] x = new double[n];
        //Добавление данных для регрессии
        for (int i = 0; i < n; i++) {
            y[i] = records.get(i).getValue();
            x[i] = i;
        }
        //Создаем объект SimpleRegression из библиотеки Apache Commons Math, который используется для выполнения простой линейной регрессии.
        SimpleRegression regression = new SimpleRegression();
        for (int i = 0; i < n; i++) {
            //добавляем точки данных (индекс и цена) в объект SimpleRegression.
            regression.addData(x[i], y[i]);
        }
        //используем модель линейной регрессии для предсказания значения цены для следующего индекса, который равен n
        // (следующий день после последнего дня в списке records).
        return Math.abs(regression.predict(n));
    }
    }