package com.woowacamp.soolsool.core.liquor.config;

import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorBrewTypeConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorRegionTypeConverter;
import com.woowacamp.soolsool.core.liquor.domain.liquor.converter.LiquorStatusTypeConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LiquorConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(final FormatterRegistry registry) {
        registry.addConverter(new LiquorBrewTypeConverter());
        registry.addConverter(new LiquorRegionTypeConverter());
        registry.addConverter(new LiquorStatusTypeConverter());
    }
}
