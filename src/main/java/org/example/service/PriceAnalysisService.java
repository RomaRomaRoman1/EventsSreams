package org.example.service;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.example.entity.PriceRecord;
import org.example.util.ArimaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceAnalysisService {
    private final ArimaHelper helper;
    @Autowired
    public PriceAnalysisService(ArimaHelper helper) {
        this.helper = helper;
    }
    // Метод для расчета средней цены
    public double calculateAveragePrice(List<PriceRecord> records) {
        return records.stream().mapToDouble(PriceRecord::getValue).average().orElse(0.0);
        //Преобразуем каждый элемент коллекции в double, вычисляем среднее значение всех элементов, orElse(0.0) — это метод
        //класса OptionalDouble, который используется для обработки случая, когда среднее значение не может быть вычислено
        //(например, если поток пустой). В этом случае вместо возвращения пустого OptionalDouble, возвращается значение по умолчанию — 0.0.
    }

    // Метод для обнаружения аномалий
    public List<Double> detectAnomalies(List<PriceRecord> records) {
        // Вычисление среднего значения
        double average = calculateAveragePrice(records);
        return records.stream()
                // Преобразование потока PriceRecord в поток значений double
                .map(PriceRecord::getValue)
                // Фильтрация значений, которые отклоняются от среднего более чем на 30%
                .filter(value -> Math.abs(value - average) / average > 0.3)
                // Сборка отфильтрованных значений в список
                .collect(Collectors.toList());
    }

    // Метод для расчета простой скользящей средней (SMA), на вход передаем список всех цен с датами и период (количество дней)
    public List<Double> calculateSMA(List<PriceRecord> records, int period) {
        // Создаем список для хранения значений SMA
        List<Double> smaValues = new ArrayList<>();
        // Идем по списку ценовых записей, начиная с индекса 0 до (размер списка - период)
        for (int i = 0; i <= records.size() - period; i++) {
            // Инициализируем переменную для хранения суммы цен за текущий период
            double sum = 0.0;
            // Проходим по записям в текущем окне (от i до i + period)
            for (int j = i; j < i + period; j++) {
                // Добавляем значение текущей записи к сумме
                sum += records.get(j).getValue();
            }
            // Вычисляем среднее значение для текущего окна и добавляем его в список SMA
            smaValues.add(sum / period);
        }
        // Возвращаем список значений SMA
        return smaValues;
    }

    // Метод для расчета экспоненциальной скользящей средней (EMA)
    public List<Double> calculateEMA(List<PriceRecord> records, int period) {
        // Создаем список для хранения значений EMA
        List<Double> emaValues = new ArrayList<>();
        // Вычисляем множитель для EMA
        double multiplier = 2.0 / (period + 1);
        // Вычисляем первое значение EMA как среднее значение для первого периода
        //sublist метод из интерфейса List, который возвращает подсписок элементов из исходного списка records(от 0 до period-1)
        double previousEMA = calculateAveragePrice(records.subList(0, period));
        emaValues.add(previousEMA);
        // Проходим по остальным записям после первого периода
        for (int i = period; i < records.size(); i++) {
            // Получаем текущее значение цены
            double currentValue = records.get(i).getValue();
            // Вычисляем текущее значение EMA
            double ema = (currentValue - previousEMA) * multiplier + previousEMA;
            // Добавляем вычисленное значение EMA в список
            emaValues.add(ema);
            // Обновляем значение previousEMA для следующей итерации
            previousEMA = ema;
        }
        // Возвращаем список значений EMA
        return emaValues;
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

    // Класс для модели ARIMA
    public class ArimaModel {
        private final int p; // Порядок авторегрессионной (AR) модели
        private final int d; // Порядок интеграции (разностей)
        private final int q; // Порядок скользящего среднего (MA) модели
        private double[] coefficients;

        // Конструктор для инициализации модели с заданными параметрами p, d, q
        public ArimaModel(int p, int d, int q) {
            this.p = p;
            this.d = d;
            this.q = q;
        }

        // Метод для обучения модели ARIMA на предоставленных данных
        public void fit(double[] data) {
            // Дифференцирование данных для достижения необходимого порядка интеграции (d)
            double[] diffData = difference(data, d);

            // Построение AR модели
            SimpleRegression ar = new SimpleRegression();
            for (int i = p; i < diffData.length; i++) {
                ar.addData(diffData[i - p], diffData[i]); // Добавление данных для построения AR модели
            }

            // Получение коэффициентов AR модели
            coefficients = new double[p + q + 1];
            for (int i = 0; i < p; i++) {
                coefficients[i] = ar.getSlope(); // Получение коэффициентов авторегрессионной части
            }

            // Построение MA модели
            for (int i = 0; i < q; i++) {
                coefficients[p + i] = ar.getIntercept(); // Получение коэффициентов скользящего среднего
            }
        }

        // Метод для прогнозирования следующих значений с использованием обученных коэффициентов
        public double[] forecast(double[] data, int steps) {
            double[] forecast = new double[steps];
            for (int i = 0; i < steps; i++) {
                forecast[i] = coefficients[0]; // Прогнозирование используя первый коэффициент
                // Здесь можно было бы реализовать более сложную логику прогнозирования, если были бы доступны
                // реальные данные и логика модели ARIMA.
            }
            return forecast;
        }

        // Вспомогательный метод для проведения дифференциации (разностей) данных
        private double[] difference(double[] data, int order) {
            double[] diff = new double[data.length - order];
            for (int i = order; i < data.length; i++) {
                diff[i - order] = data[i] - data[i - order]; // Разностная форма данных для достижения интеграции порядка order
            }
            return diff;
        }
    }
        public double[] predictNextPriceWithARIMA(List<PriceRecord> records) {
            double[] data = records.stream().mapToDouble(PriceRecord::getValue).toArray();
            // Автоматический выбор параметров p, d, q
            int[] params = helper.autoSelectARIMAParams(data);
            // Построение модели ARIMA с выбранными параметрами
            ArimaModel arima = new ArimaModel(params[0], params[1], params[2]);
            arima.fit(data);
            // Предсказание следующего значения
            return arima.forecast(data, 1);
        }
    }