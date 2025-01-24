package io.org.reactivestax.service;


import io.org.reactivestax.domain.Client;
import io.org.reactivestax.domain.Contact;
import io.org.reactivestax.dto.ClientDTO;
import io.org.reactivestax.dto.ContactDTO;
import io.org.reactivestax.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public String save(ClientDTO clientDTO){
        Client client = convertToEntity(clientDTO);
        clientRepository.save(client);
        return "Client added with contact";
    }

    private Client convertToEntity(ClientDTO clientDTO) {
        Client client = new Client();
        client.setFirstName(clientDTO.getFirstName());
        client.setLastName(clientDTO.getLastName());
        client.setCreatedAt(LocalDateTime.now());
        client.setPreferredContactMethod(clientDTO.getContactMethod());
        List<Contact> contactList = new ArrayList<>();
        for(ContactDTO contactDTO:clientDTO.getContactDTOS()){
            Contact contact = new Contact();
            contact.setEmail(contactDTO.getEmail());
            contact.setMobileNumber(contactDTO.getMobileNumber());
            contact.setClient(client);
            contactList.add(contact);
        }
        client.getContactList().addAll(contactList);
        clientRepository.save(client);
        return client;
    }


}
