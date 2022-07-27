package demo.kafka;

import modal.Address;
import modal.Driver;
import modal.User;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserAppDemo {
    public static void main(String[] args) {
        Address address = new Address("39B Truong Son", "HoChiMinh", "Viet Nam");

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        UserService userService = new UserService(properties);
        List<String> cities = List.of("HoChiMinh", "DaNang", "QuyNhon");
        for(int i = 0; i < 1000; i++) {
            var name = "user-" + String.valueOf(i);
            User user = new User(name, address);
            user.getLocation().setCity(handleRandomCity(cities));
            System.out.println(user.getLocation().getCity());
            userService.bookCar(user);
        }
        userService.closeUserApp();
    }

    private static String handleRandomCity(List<String> cties) {
        return cties.get((int) (Math.random()*cties.size()));
    }
}
