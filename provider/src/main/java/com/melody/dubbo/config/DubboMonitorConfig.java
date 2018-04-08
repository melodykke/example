package com.melody.dubbo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {"classpath:dubbo-monitor.xml",
        "classpath:zookeeper.xml"})

public class DubboMonitorConfig {
}
