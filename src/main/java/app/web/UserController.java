package app.web;

import app.exceptions.EmailAlreadyExistException;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.DtoMapper;
import app.web.dto.EditProfileRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}/profile")
    public ModelAndView getProfilePage(@PathVariable UUID id){

        User user = userService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("editProfileRequest", DtoMapper.fromUser(user));
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateProfile(@Valid EditProfileRequest editProfileRequest,
                                      BindingResult bindingResult,
                                      @PathVariable UUID id){
        User user = userService.getById(id);

        if(bindingResult.hasErrors()){
            ModelAndView modelAndView = new ModelAndView("profile-menu");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        try{
            userService.updateProfile(id, editProfileRequest);
        }catch (EmailAlreadyExistException e){
            bindingResult.rejectValue("email", "email.exists", e.getMessage());

            ModelAndView modelAndView = new ModelAndView("profile-menu");
            modelAndView.addObject("user", user);
            return modelAndView;
        }

        return new ModelAndView("redirect:/home");
    }


    @RequestMapping
    public ModelAndView getAllUsers(){

        List<User> users = userService.getAll();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);

        return modelAndView;
    }

    @PatchMapping("/{userId}/status")
    public String switchUserStatus(@PathVariable UUID userId){
        userService.switchStatus(userId);

        return "redirect:/users";
    }

    @PatchMapping("/{userId}/role")
    public String switchUserRole(@PathVariable UUID userId){
        userService.switchRole(userId);
        return "redirect:/users";
    }
}
