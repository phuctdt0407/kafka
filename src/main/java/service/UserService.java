package service;

import modal.User;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import java.util.Properties;
import java.util.UUID;

public class UserService {

    private KafkaProducer<String, String> userProducer;
    private static final String USER_TOPIC = "user-orders";

    public UserService(Properties properties) {
        this.userProducer = new KafkaProducer<String, String>(properties);
    }

    public void bookCar(User user) {
        user.setTransaction(UUID.randomUUID().toString());
        ProducerRecord<String, String> newOrder = new ProducerRecord<>(USER_TOPIC, new JSONObject(user).toString());
        this.userProducer.send(newOrder, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if(e == null) {
                    System.out.print("Center received order, please wait...\n"
                            + recordMetadata.toString() + "\n");
                    System.out.println("");
                } else {
                    System.out.print(e.toString());
                }
            }
        });
        this.userProducer.flush();
    }

    public void closeUserApp() {
        this.userProducer.close();
    }
}