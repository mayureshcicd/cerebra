package com.cerebra.fileprocessor.monitor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfiguration {

    @Bean
    public Counter successCounter(MeterRegistry meterRegistry) {
        return Counter.builder("file.processing.success")
                .description("Number of files processed successfully")
                .register(meterRegistry);
    }

    @Bean
    public Counter failureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("file.processing.failure")
                .description("Number of files processing failures")
                .register(meterRegistry);
    }

    @Bean
    public Timer processingTimer(MeterRegistry meterRegistry) {
        return Timer.builder("file.processing.time")
                .description("Time taken to process files")
                .register(meterRegistry);
    }
}