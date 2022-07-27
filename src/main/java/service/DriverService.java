package service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class DriverService {

    private KafkaConsumer<String, String> driverConsumer;
    private static final String DRIVER_TOPIC = "driver-orders";

    public DriverService(Properties properties) {
        this.driverConsumer = new KafkaConsumer<>(properties);
    }

    public void listenToDriverOrder() {
        try {
            this.driverConsumer.subscribe(Collections.singleton(DRIVER_TOPIC));
            while (true) {
                ConsumerRecords<String, String> records = this.driverConsumer.poll(Duration.ofMillis(1000));
                for(ConsumerRecord<String, String> record: records) {
                    System.out.println("Ticket received. Driver is on the way...");
                    System.out.print(record);
                    System.out.println("");
                }
            }
        } catch (WakeupException e) {
            System.out.print("driverConsumer wake up exception");
        } catch (Exception e) {
            System.out.print("driverConsumer unexpected exception");
        } finally {
            this.driverConsumer.close();
            System.out.print("driverConsumer closed");
        }
    }
}
