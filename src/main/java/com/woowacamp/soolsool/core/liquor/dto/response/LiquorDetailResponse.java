package com.woowacamp.soolsool.core.liquor.dto.response;

import com.woowacamp.soolsool.core.liquor.domain.liquor.Liquor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LiquorDetailResponse {

    private final Long id;
    private final String name;
    private final String price;
    private final String brand;
    private final String imageUrl;
    private final Integer stock;
    private final Double alcohol;
    private final Integer volume;

    public static LiquorDetailResponse of(final Liquor liquor) {
        return new LiquorDetailResponse(
            liquor.getId(),
            liquor.getName(),
            liquor.getPrice().toString(),
            liquor.getBrand(),
            liquor.getImageUrl(),
            liquor.getTotalStock(),
            liquor.getAlcohol(),
            liquor.getVolume()
        );
    }
}
