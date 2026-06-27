package app.user.property;

import app.user.model.Country;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.beans.ConstructorProperties;

// тук ще се пази някакъв вид конфигурация, която ще идва от property файловете application-dev.properties
@Data
@Configuration
@ConfigurationProperties(prefix = "users")
public class UserProperties {

    private DefaultUser defaultUser;

    private String testProperty;

    @Data
    public static class DefaultUser{
        private String username;
        private String password;
        private Country country;
    }
//слагаме брейк пойнт и ни помага да прочетем дали сме си извлекли данните от application-dev.properties
//    @PostConstruct
//    public void test(){
//        System.out.println();
//    }
}
