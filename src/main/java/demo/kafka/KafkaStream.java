package demo.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.json.JSONObject;

import java.util.Properties;

public class KafkaStream {
    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-topic-stream");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, String> kStream = streamsBuilder.stream("user-orders", Consumed.with(Serdes.String(), Serdes.String()));

        kStream.selectKey((key, value) -> {
            JSONObject obj = new JSONObject(value);
            String transaction = obj.getString("transaction");
            return transaction;
        }).filter((key, value) -> {
            JSONObject obj = new JSONObject(value);
            JSONObject location = obj.getJSONObject("location");
            String city = location.getString("city");
            return city.equals("HoChiMinh");
        }).to("hochiminh-sink");

        kStream.selectKey((key, value) -> {
            JSONObject obj = new JSONObject(value);
            String transaction = obj.getString("transaction");
            return transaction;
        }).filter((key, value) -> {
            JSONObject obj = new JSONObject(value);
            JSONObject location = obj.getJSONObject("location");
            String city = location.getString("city");
            return city.equals("DaNang");
        }).to("danang-sink");

        kStream.selectKey((key, value) -> {
            JSONObject obj = new JSONObject(value);
            String transaction = obj.getString("transaction");
            return transaction;
        }).filter((key, value) -> {
            JSONObject obj = new JSONObject(value);
            JSONObject location = obj.getJSONObject("location");
            String city = location.getString("city");
            return city.equals("QuyNhon");
        }).to("quynhon-sink");

        KafkaStreams stream = new KafkaStreams(streamsBuilder.build(), properties);
        stream.start();
    }
}
