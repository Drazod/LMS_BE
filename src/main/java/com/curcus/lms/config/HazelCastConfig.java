//package com.curcus.lms.config;
//
//import com.hazelcast.config.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class HazelCastConfig {
//    @Bean
//    public Config hazelcastConfig() {
//        return new Config().setProperty("hazelcast.jmx", "true")
//                .addMapConfig(new MapConfig("spring-boot-admin-application-store").setBackupCount(1)
//                        .setEvictionConfig(
//                                new EvictionConfig().setEvictionPolicy(EvictionPolicy.NONE)
//                        ))
//                .addListConfig(new ListConfig("spring-boot-admin-event-store").setBackupCount(1)
//                        .setMaxSize(1000));
//    }
//}
