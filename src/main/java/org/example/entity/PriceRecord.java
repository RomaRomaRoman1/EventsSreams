package org.example.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="PriceRecord")
public class PriceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//Эта стратегия указывает на использование автоинкрементных столбцов
    // в базе данных, где значение id генерируется самой базой данных при вставке новой записи.
    private long id;
    private LocalDate date;
    private Double value;

    public PriceRecord(long id, LocalDate date, Double value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }

    public PriceRecord(LocalDate date, Double value) {
        this.date = date;
        this.value = value;
    }

    public PriceRecord() {//необходим для создания экземпляров класса через рефлексию JPA.
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
