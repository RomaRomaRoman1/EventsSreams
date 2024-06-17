package org.example.service;
import org.example.Hibernate.PriceRecordHibernateImpl;
import org.example.entity.PriceRecord;
import org.example.repository.PriceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceRecordService {
    private final PriceRecordHibernateImpl priceRecordHibernateImpl;
    private final PriceRecordRepository priceRecordRepository;

    @Autowired
    public PriceRecordService(PriceRecordHibernateImpl priceRecordHibernateImpl, PriceRecordRepository priceRecordRepository, PriceRecordRepository priceRecordRepository1) {
        this.priceRecordHibernateImpl = priceRecordHibernateImpl;
        this.priceRecordRepository = priceRecordRepository1;
    }
    public void addAllFromXml(List<PriceRecord> list) {
        priceRecordHibernateImpl.addAllFromXml(list);
    }

}
