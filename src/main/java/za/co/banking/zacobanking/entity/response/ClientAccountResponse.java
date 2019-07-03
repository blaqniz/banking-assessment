package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.CurrencyConversionRate;

@Data
public class ClientAccountResponse {

    private ClientAccount clientAccount;

    private CurrencyConversionRate currencyConversionRate;

    private String errorMessage;

}
