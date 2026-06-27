package app.web;

import app.user.model.User;
import app.user.property.UserProperties;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;



@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private final UserService userService;
    private final UserProperties userProperties;

    @Autowired
    public TransactionController(UserService userService, UserProperties userProperties) {
        this.userService = userService;
        this.userProperties = userProperties;
    }


    @GetMapping
    public ModelAndView getUserTransactions(){
        User user = userService.getByUsername(userProperties.getDefaultUser().getUsername());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("transactions");
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
