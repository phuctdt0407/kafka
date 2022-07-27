package service;

import modal.Driver;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class OrderService {
    private KafkaConsumer<String, String> orderConsumer;
    private KafkaProducer<String, String> orderProducer;
    private ArrayList<Driver> drivers = new ArrayList<>();
    private static final String USER_TOPIC = "user-orders";
    private static final String DRIVER_TOPIC = "driver-orders";

    public OrderService(Properties orderConsumerProperties, Properties orderProducerProperties, ArrayList<Driver> drivers){
        this.orderConsumer = new KafkaConsumer<String, String>(orderConsumerProperties);
        this.orderProducer = new KafkaProducer<String, String>(orderProducerProperties);
        this.drivers = drivers;
    }

    private void findDriverAndCreateTicket() {
        //return random driver
        Driver driver = this.drivers.get((int) (Math.random()*this.drivers.size()));
        Thread newThead = new Thread(new Runnable() {
            public void run()
            {
                createTicketForDriver(driver);
            }});
        newThead.start();

    }

    public void listenToUserOrder() {
        try {
            this.orderConsumer.subscribe(Collections.singleton(USER_TOPIC));
            while (true) {
                ConsumerRecords<String, String> records = this.orderConsumer.poll(Duration.ofMillis(1000));
                for(ConsumerRecord<String, String> record: records) {
                    orderConsumer.commitAsync();
                    findDriverAndCreateTicket();
                }
            }
        } catch (WakeupException e) {
            System.out.print("orderConsumer wake up exception");
        } catch (Exception e) {
            System.out.print("orderConsumer unexpected exception");
        } finally {
            this.orderConsumer.close();
            System.out.print("orderConsumer closed");
        }
    }

    private void createTicketForDriver(Driver driver) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(DRIVER_TOPIC, driver.toString());
        this.orderProducer.send(producerRecord, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null) {
                    System.out.println("Center created ticket for driver: " + driver.getUsername() + " located at partition " + metadata.partition() + "\n");
                } else {
                    System.out.println("Center crashed when create ticket for driver: " + driver.getUsername());
                }
            }
        });
    }

}
