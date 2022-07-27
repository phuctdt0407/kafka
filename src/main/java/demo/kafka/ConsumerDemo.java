package demo.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());

    public static void main(String[] args) {
        Properties properties = new Properties();
        String groupId = "group-consumer";
        String topic = "quickstart-events";

        //create consumer config
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //none | earliest | latest
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        //create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        //shut down consumer
        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                logger.info("Detected shut down, let's exit by calling consumer wake up....");
                consumer.wakeup();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        try {
            //subscribe consumer to topic(s)
            consumer.subscribe(Collections.singleton(topic));
            //consumer.subscribe(Arrays.asList(topic));
            while(true) {
                logger.info("Polling");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for(ConsumerRecord<String, String> record: records) {
                    logger.info(record.toString());
                }
            }
        } catch (WakeupException e) {
            logger.info("wake up exception");
        } catch (Exception e) {
            logger.error("Unexpected exception");
        } finally {
            consumer.close();
            logger.info("Closed");
        }

    }
}
