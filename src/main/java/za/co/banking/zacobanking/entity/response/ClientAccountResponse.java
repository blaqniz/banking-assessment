package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import org.springframework.stereotype.Component;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.CurrencyConversionRate;

@Component
@Data
public class ClientAccountResponse {

    private ClientAccount clientAccount;

    private CurrencyConversionRate currencyConversionRate;

}
