package com.swisspost.cryptowalletmanagement.service.batch;

import com.swisspost.cryptowalletmanagement.repository.AssetDetailRepository;
import com.swisspost.cryptowalletmanagement.repository.entity.AssetDetailEntity;
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
public class RepositoryAssetItemReader extends AbstractItemCountingItemStreamItemReader<AssetDetailEntity> {

    private final AssetDetailRepository repository;
    private Iterator<AssetDetailEntity> assetIterator;
    private int page = 0;
    private final int pageSize;


    @Autowired
    public RepositoryAssetItemReader(AssetDetailRepository repository) {
        this.repository = repository;
        this.pageSize = 100;
        setName("assetReader");
    }

    private void readNextPage() {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<AssetDetailEntity> assetPage = repository.findAll(pageable);
        assetIterator = assetPage.getContent().iterator();
        page++;
    }

    @Override
    protected AssetDetailEntity doRead() {
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