package com.ikun.wms_backend.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射规则：/image/** -> D:/wms-files/
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:F:/develop/javaweb/WMS/wms-backend/wms-backend/wms-files/");
    }
}