package za.co.banking.zacobanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.banking.zacobanking.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
