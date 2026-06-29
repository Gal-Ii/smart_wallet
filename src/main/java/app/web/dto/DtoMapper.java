package app.web.dto;

import app.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    //convert User object toward EditProfileRequest object
    public static EditProfileRequest fromUser(User user){
        return EditProfileRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePictureURL(user.getProfilePicture())
                .build();
    }
}
