package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import za.co.banking.zacobanking.entity.ClientAccount;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionalAccountResponse {

    private List<ClientAccount> clientAccounts = new ArrayList<>();

    private String errorMessage;

}
