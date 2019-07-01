package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import org.springframework.stereotype.Component;
import za.co.banking.zacobanking.entity.ClientAccount;

import java.util.List;

@Component
@Data
public class CurrencyAccountResponse {

    private List<ClientAccount> clientAccounts;
}
