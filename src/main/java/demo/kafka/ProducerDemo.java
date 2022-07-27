package demo.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
    private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        //create Producer Props
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //create Producer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("quickstart-events", "hello world 2");
        //send data - async
        producer.send(producerRecord, new Callback() {
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null) {
                    logger.info("Sent message successfully to " + metadata.topic());
                } else {
                    logger.error(exception.toString());
                }
            }
        });
        //flush data - sync
        producer.flush();
        //flush and close Producer
        producer.close();
    }
}
