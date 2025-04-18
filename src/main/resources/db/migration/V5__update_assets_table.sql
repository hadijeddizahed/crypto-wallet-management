ALTER TABLE assets
DROP COLUMN symbol,
DROP COLUMN price;

ALTER TABLE assets
ADD COLUMN asset_detail_id BIGINT NOT NULL,
ADD CONSTRAINT fk_asset_detail
    FOREIGN KEY (asset_detail_id) REFERENCES asset_details(id) ON DELETE CASCADE;
