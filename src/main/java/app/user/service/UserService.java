package app.user.service;

import app.exceptions.EmailAlreadyExistException;
import app.subscription.model.Subscription;
import app.subscription.service.SubscriptionService;
import app.user.model.User;
import app.user.model.UserRoles;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.wallet.service.WalletService;
import app.web.dto.EditProfileRequest;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;
    private final UserProperties userProperties;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, WalletService walletService, SubscriptionService subscriptionService, UserProperties userProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
        this.userProperties = userProperties;
    }

    public User login(LoginRequest loginRequest){
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
        if(!optionalUser.isPresent()){
            throw new RuntimeException("Incorrect username or password.");
        }

        String rawPassword = loginRequest.getPassword();
        String hashedPassword = optionalUser.get().getPassword();
        if(!passwordEncoder.matches(rawPassword, hashedPassword)){
            throw new RuntimeException("Incorrect username or password.");
        }

        return optionalUser.get();
    }

    @Transactional
    public void register(RegisterRequest registerRequest){
         Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());
         if(optionalUser.isPresent()){
             throw new RuntimeException(String.format("User with [%s] username already exists.", registerRequest.getUsername()));
         }

         User user = User.builder()
                 .username(registerRequest.getUsername())
                 .password(passwordEncoder.encode(registerRequest.getPassword()))
                 .role(UserRoles.USER)
                 .country(registerRequest.getCountry())
                 .active(true)
                 .createdOn(LocalDateTime.now())
                 .updatedOn(LocalDateTime.now())
                 .build();

         user = userRepository.save(user);

         walletService.createDefaultWallet(user);

         subscriptionService.createDefaultSubscription(user);

         log.info(String.format("New user profile was registered in the system for user [%s].", registerRequest.getUsername()));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(String.format("User with [%s] username does not exists.", username)));
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new RuntimeException(String.format("User with [%s] id does not exists.", id)));
    }

    public User getDefaultUser() {
        return getByUsername(userProperties.getDefaultUser().getUsername());
    }

    public void updateProfile(UUID id, EditProfileRequest editProfileRequest) {
        if(userRepository.existsByEmailAndIdNot(editProfileRequest.getEmail(), id)){
            throw new EmailAlreadyExistException("Email is already used.");
        }

        User user = getById(id);

        user.setFirstName(editProfileRequest.getFirstName());
        user.setLastName(editProfileRequest.getLastName());
        user.setEmail(editProfileRequest.getEmail());
        user.setProfilePicture(editProfileRequest.getProfilePictureURL());

        userRepository.save(user);
    }

    public void switchStatus(UUID userId) {
        User user = getById(userId);

        user.setActive(!user.isActive());

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

    }


    public void switchRole(UUID userId) {

        User user = getById(userId);

        if(user.getRole() ==  UserRoles.USER){
            user.setRole(UserRoles.ADMIN);
        }else {
            user.setRole(UserRoles.USER);
        }
        user.setUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
    }

}
