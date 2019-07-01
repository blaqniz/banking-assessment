package za.co.banking.zacobanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.banking.zacobanking.entity.AtmAllocation;

public interface AtmAllocationRepository extends JpaRepository<AtmAllocation, Integer> {
}
