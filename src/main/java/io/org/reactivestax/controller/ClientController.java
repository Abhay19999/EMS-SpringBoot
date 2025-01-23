package io.org.reactivestax.controller;


import io.org.reactivestax.dto.ClientDTO;
import io.org.reactivestax.service.ClientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/save")
    public String saveClientToDatabase(@Valid @RequestBody ClientDTO clientDTO){
        clientService.save(clientDTO);
        return "Client saved successfully";
    }

}
