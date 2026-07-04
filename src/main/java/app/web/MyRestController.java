package app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class MyRestController {

    private final RestClientSsl restClientSsl;

    public MyRestController(RestClientSsl restClientSsl) {
        this.restClientSsl = restClientSsl;
    }

    @GetMapping("/info")
    public String getInfo(HttpServletRequest request, HttpServletResponse response){

        String userResponse = "Hello" + request.getHeader("author") + "nice to speak with you again. You are " + request.getHeader("age") + " years old.";
        response.setHeader("Greetings", userResponse);

        return "Yes";
    }
}
