package com.example.risk_scoring_service.config;

import com.example.risk_scoring_service.model.TransactionRequest;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JacksonJsonSerde;

/**
 * Defines the Kafka Streams topology for risk-scoring-service.
 * <p>
 * Wires up the {@code StreamsBuilder} (via {@code @EnableKafkaStreams}) used to
 * build the pipeline that reads from {@code transactions.raw} and, as scoring
 * logic is added, processes each transaction.
 */
@Configuration
@EnableKafkaStreams
public class TopologyConfig {

    private static final Logger log = LoggerFactory.getLogger(TopologyConfig.class);

    @Bean
    public KStream<String, TransactionRequest> transactionsRawStream(
            StreamsBuilder streamsBuilder,
            @Value("${spring.kafka.topic.transactions-raw}") String topic) {

        // JacksonJsonSerde converts JSON bytes <-> TransactionRequest (Spring Kafka 4.x;
        // replaces the deprecated JsonSerde). ignoreTypeHeaders() ignores ingestion-service's
        // __TypeId__ header (which names ITS package) and always targets our own class.
        JacksonJsonSerde<TransactionRequest> transactionValueSerde =
                new JacksonJsonSerde<>(TransactionRequest.class).ignoreTypeHeaders();

        KStream<String, TransactionRequest> transactionsStream = streamsBuilder.stream(
                topic, Consumed.with(Serdes.String(), transactionValueSerde));

        // Skeleton: just log every record consumed for now.
        transactionsStream.foreach((key, value) ->
                log.info("Consumed transaction: key={}, value={}", key, value));

        return transactionsStream;
    }
}
