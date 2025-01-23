package io.org.reactivestax.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ClientDTO {
    @NotNull(message = "First name should not be null")
    private String firstName;
    @NotNull(message = "Last name should not be null")
    private String lastName;

    @NotNull(message = "contact method should not be null.")
    private String contactMethod;

    @NotNull(message = "Please provide at least one contact method")
    private List<ContactDTO> contactDTOS = new ArrayList<>();
}
