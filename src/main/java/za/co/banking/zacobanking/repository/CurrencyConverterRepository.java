package za.co.banking.zacobanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.banking.zacobanking.entity.CurrencyConversionRate;

@Repository
public interface CurrencyConverterRepository extends JpaRepository<CurrencyConversionRate, String> {
}
