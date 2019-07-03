package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import za.co.banking.zacobanking.entity.ClientAccount;

import java.util.List;

@Data
public class CurrencyAccountResponse {

    private List<ClientAccount> clientAccounts;

    private String errorMessage;

}
