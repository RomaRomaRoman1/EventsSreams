package org.example.service;
import org.example.Hibernate.PriceRecordHibernateImpl;
import org.example.entity.PriceRecord;
import org.example.repository.PriceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceRecordService {
    private final PriceRecordHibernateImpl priceRecordHibernateImpl;
    private final PriceRecordRepository priceRecordRepository;

    @Autowired
    public PriceRecordService(PriceRecordHibernateImpl priceRecordHibernateImpl, PriceRecordRepository priceRecordRepository) {
        this.priceRecordHibernateImpl = priceRecordHibernateImpl;
        this.priceRecordRepository = priceRecordRepository;
    }
    @Transactional
    public void deleteAllPriceRecords() {
        priceRecordRepository.deleteAllInBatch();
    }
    @Transactional
    public void addAllFromXml(List<PriceRecord> listWithXml) {
        priceRecordHibernateImpl.addAllFromXml(listWithXml);
    }

}
