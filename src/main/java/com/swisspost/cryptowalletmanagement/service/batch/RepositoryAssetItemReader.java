package com.swisspost.cryptowalletmanagement.service.batch;

import com.swisspost.cryptowalletmanagement.repository.AssetRepository;
import com.swisspost.cryptowalletmanagement.repository.entity.AssetEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class RepositoryAssetItemReader extends AbstractItemCountingItemStreamItemReader<AssetEntity> {

    private final AssetRepository repository;
    private Iterator<AssetEntity> assetIterator;
    private int page = 0;
    private final int pageSize;


    @Autowired
    public RepositoryAssetItemReader(AssetRepository repository) {
        this.repository = repository;
        this.pageSize = 100;
        setName("assetReader");
    }

    private void readNextPage() {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<AssetEntity> assetPage = repository.findAll(pageable);
        assetIterator = assetPage.getContent().iterator();
        page++;
    }

    @Override
    protected AssetEntity doRead() {
        if (assetIterator == null || !assetIterator.hasNext()) {
            readNextPage();
        }

        if (assetIterator.hasNext()) {
            return assetIterator.next();
        } else {
            return null;
        }
    }

    @Override
    protected void doOpen() {
        page = 0;
        readNextPage();
    }

    @Override
    protected void doClose() {

    }
}