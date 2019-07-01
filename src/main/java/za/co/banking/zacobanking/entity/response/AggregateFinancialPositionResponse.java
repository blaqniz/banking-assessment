package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import org.springframework.stereotype.Component;
import za.co.banking.zacobanking.entity.Client;

import java.math.BigDecimal;

@Component
@Data
public class AggregateFinancialPositionResponse {

    private BigDecimal netPosition;

    private Client client;

}
