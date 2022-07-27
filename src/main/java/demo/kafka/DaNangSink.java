package demo.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class DaNangSink {
    private static KafkaConsumer<String, String> consumer;
    private static final String DANANG_SINK_TOPIC = "danang-sink";
    public static void main(String[] args) {
        Properties consumerProperties = new Properties();
        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "danang-sink-group");
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //none | earliest | latest
        consumerProperties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        consumer = new KafkaConsumer<>(consumerProperties);
        try {
            consumer.subscribe(Collections.singleton(DANANG_SINK_TOPIC));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for(ConsumerRecord<String, String> record: records) {

                    System.out.print(record);
                    System.out.println("");
                }
            }
        } catch (WakeupException e) {
            System.out.print("consumer wake up exception");
        } catch (Exception e) {
            System.out.print("consumer unexpected exception");
        } finally {
            consumer.close();
            System.out.print("consumer closed");
        }
    }
}
