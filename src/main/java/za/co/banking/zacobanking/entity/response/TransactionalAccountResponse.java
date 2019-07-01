package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import org.springframework.stereotype.Component;
import za.co.banking.zacobanking.entity.ClientAccount;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class TransactionalAccountResponse {

    private List<ClientAccount> clientAccounts = new ArrayList<>();

}
