package io.org.reactivestax.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ContactDTO {


    @Email
    @NotNull(message = "Email should not be null")
    private String email;

    @NotNull(message = "Mobile should not be null")
    private String mobileNumber;
}
