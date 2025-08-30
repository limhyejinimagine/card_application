package com.imagine.card.card_application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    // 트랜잭션 시작 지점에 이 메서드 호출됨
    @Override
    protected Object determineCurrentLookupKey() {
        boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
        boolean readOnly = txActive && TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        String key;
        if (readOnly) {
            key = "slave";
        } else {
            key = "master";
        }

        log.info("RoutingDataSource lookup: txActive={}, readOnly={}, key={}", txActive, readOnly, key);
        return key;
    }


}
