package app.web.dto;

import app.user.model.Country;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// анотацията @Builder ми помага да си направя един обект от този клас
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest{
        @NotBlank
        @Size(min = 6, max = 14, message = "Username length must be between 6 and 14 symbols.")
        private String username;

        @NotBlank
        @Size(min = 6, message = "Password mast be exactly 6 symbols.")
        private String password;

        @NotNull
        private Country country;
}
