package io.org.reactivestax.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;


@Entity
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String mobileNumber;


    @ManyToOne
    @JoinColumn(name = "client_id")
    @JsonBackReference
    @ToString.Exclude
    private Client client;
}
