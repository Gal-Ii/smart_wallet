package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest{
        @NotBlank
        @Size(min = 6, max = 14, message = "Username length must be between 6 and 14 symbols.")
        private String username;

        @NotBlank
        @Size(min = 6, message = "Password mast be exactly 6 symbols.")
        private String password;
}
