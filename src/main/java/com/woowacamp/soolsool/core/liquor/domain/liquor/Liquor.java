package com.woowacamp.soolsool.core.liquor.domain.liquor;

import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorAlcoholConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorBrandConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorImageUrlConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorNameConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorPriceConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorVolumeConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorAlcohol;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorBrand;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorImageUrl;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorName;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorPrice;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorStatusType;
import com.woowacamp.soolsool.core.liquor.domain.liquor.vo.LiquorVolume;
import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStockCount;
import com.woowacamp.soolsool.core.liquor.domain.stock.LiquorStockCountConverter;
import com.woowacamp.soolsool.core.liquor.dto.request.LiquorModifyRequest;
import com.woowacamp.soolsool.global.common.BaseEntity;
import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Table(name = "liquors")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class Liquor extends BaseEntity {

    private static final LiquorStockCount DEFAULT_TOTAL_COUNT = new LiquorStockCount(0);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Getter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brew_id", nullable = false)
    @Getter
    private LiquorBrew brew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    @Getter
    private LiquorRegion region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    @Getter
    private LiquorStatus status;

    @Column(name = "name", nullable = false, length = 100)
    @Convert(converter = LiquorNameConverter.class)
    private LiquorName name;

    @Column(name = "price", nullable = false, length = 255)
    @Convert(converter = LiquorPriceConverter.class)
    private LiquorPrice price;

    @Column(name = "brand", nullable = false, length = 20)
    @Convert(converter = LiquorBrandConverter.class)
    private LiquorBrand brand;

    @Column(name = "image_url", nullable = false, length = 255)
    @Convert(converter = LiquorImageUrlConverter.class)
    private LiquorImageUrl imageUrl;

    @Column(name = "alcohol", nullable = false)
    @Convert(converter = LiquorAlcoholConverter.class)
    private LiquorAlcohol alcohol;

    @Column(name = "volume", nullable = false)
    @Convert(converter = LiquorVolumeConverter.class)
    private LiquorVolume volume;

    @Column(name = "total_stock", nullable = false)
    @Convert(converter = LiquorStockCountConverter.class)
    private LiquorStockCount totalStock;

    @Builder
    public Liquor(
        final LiquorBrew brew,
        final LiquorRegion region,
        final LiquorStatus status,
        final String name,
        final String price,
        final String brand,
        final String imageUrl,
        final Double alcohol,
        final int volume
    ) {
        this(null, brew, region, status,
            name, price, brand, imageUrl,
            alcohol, volume);
    }

    public Liquor(
        final Long id,
        @NonNull final LiquorBrew brew,
        @NonNull final LiquorRegion region,
        @NonNull final LiquorStatus status,
        final String name,
        final String price,
        final String brand,
        final String imageUrl,
        final Double alcohol,
        final int volume
    ) {
        this.id = id;
        this.brew = brew;
        this.region = region;
        this.status = status;
        this.name = new LiquorName(name);
        this.price = new LiquorPrice(new BigInteger(price));
        this.brand = new LiquorBrand(brand);
        this.imageUrl = new LiquorImageUrl(imageUrl);
        this.alcohol = new LiquorAlcohol(alcohol);
        this.volume = new LiquorVolume(volume);
        this.totalStock = DEFAULT_TOTAL_COUNT;
    }

    public void update(
        final LiquorBrew brew,
        final LiquorRegion region,
        final LiquorStatus status,
        final LiquorModifyRequest request
    ) {
        this.brew = brew;
        this.region = region;
        this.status = status;
        this.name = new LiquorName(request.getName());
        this.price = LiquorPrice.from(request.getPrice());
        this.brand = new LiquorBrand(request.getBrand());
        this.imageUrl = new LiquorImageUrl(request.getImageUrl());
        this.alcohol = new LiquorAlcohol(request.getAlcohol());
        this.volume = new LiquorVolume(request.getVolume());
    }

    public boolean isStopped() {
        return status.getType().equals(LiquorStatusType.STOPPED);
    }

    public int getTotalStock() {
        return this.totalStock.getStock();
    }

    public String getName() {
        return this.name.getName();
    }

    public BigInteger getPrice() {
        return this.price.getPrice();
    }

    public String getBrand() {
        return this.brand.getBrand();
    }

    public String getImageUrl() {
        return this.imageUrl.getImageUrl();
    }

    public double getAlcohol() {
        return this.alcohol.getAlcohol();
    }

    public int getVolume() {
        return this.volume.getVolume();
    }

    public void decreaseTotalStock(final int quantity) {
        this.totalStock = this.totalStock.decrease(quantity);
    }

    public void increaseTotalStock(final int stock) {
        this.totalStock = this.totalStock.increase(stock);
    }
}
