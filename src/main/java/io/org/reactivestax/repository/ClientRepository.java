package io.org.reactivestax.repository;

import io.org.reactivestax.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client,Long> {

}
