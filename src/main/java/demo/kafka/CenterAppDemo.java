package demo.kafka;

import modal.Driver;
import modal.Order;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import service.OrderService;

import java.util.ArrayList;
import java.util.Properties;

public class CenterAppDemo {
    public static void main(String[] args) {
        Properties producerProperties = new Properties();
        Properties consumerProperties = new Properties();

        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerProperties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        producerProperties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
        producerProperties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");

        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "user-orders-group");
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //none | earliest | latest
        consumerProperties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        consumerProperties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // by default true => kafka will auto commit 5s

        Driver driver1 = new Driver("taxi-alex", "alex");
        Driver driver2 = new Driver("taxi-susan", "susan");
        Driver driver3 = new Driver("taxi-naomi", "naomi");
        Driver driver4 = new Driver("taxi-adrew", "adrew");
        Driver driver5 = new Driver("taxi-javis", "javis");
        Driver driver6 = new Driver("taxi-joel", "joel");
        ArrayList<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);
        drivers.add(driver3);
        drivers.add(driver4);
        drivers.add(driver5);
        drivers.add(driver6);

        OrderService orderService = new OrderService(consumerProperties, producerProperties, drivers);

        orderService.listenToUserOrder();
    }
}
