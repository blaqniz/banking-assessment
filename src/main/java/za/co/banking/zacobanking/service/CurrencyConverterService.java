package za.co.banking.zacobanking.service;

import za.co.banking.zacobanking.entity.ClientAccount;
import java.util.List;

public interface CurrencyConverterService {

    List<ClientAccount> getAllNonZARCurrenciesConverted(List<ClientAccount> clientAccounts);

}
