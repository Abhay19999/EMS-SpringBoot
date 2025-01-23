package io.org.reactivestax.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class MessageDTO {


    @NotNull(message = "client ID cannot be null")
    private Long clientId;


    @NotBlank(message = "recipient contact method should not be empty")
    @Pattern(regexp = "\\d{10}", message = "contact number should be exact 10 digit")
    private String recipientContactNumber;

    @NotNull(message = "Email cannot be null")
    private String recipientEmailAddress;


    private String message;

}
