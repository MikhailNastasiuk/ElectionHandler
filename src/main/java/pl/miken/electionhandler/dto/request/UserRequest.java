package pl.miken.electionhandler.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotEmpty(message = "First name can't be empty")
    private String firstName;

    @NotEmpty(message = "Last name can't be empty")
    private String lastName;

    @NotEmpty(message = "Email name can't be empty")
    private String email;

    @NotEmpty(message = "Personal number name can't be empty")
    private String personalNumber;

    @NotNull(message = "Age can't be null")
    @Min(value = 1, message = "Age can't be zero or less")
    private Integer age;
}
