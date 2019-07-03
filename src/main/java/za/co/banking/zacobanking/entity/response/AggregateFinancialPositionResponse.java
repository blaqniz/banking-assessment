package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import za.co.banking.zacobanking.entity.Client;

import java.math.BigDecimal;

@Data
public class AggregateFinancialPositionResponse {

    private BigDecimal netPosition;

    private Client client;

}
