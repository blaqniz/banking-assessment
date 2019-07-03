package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import za.co.banking.zacobanking.entity.Client;

import java.util.List;

@Data
public class ClientResponse {

    private List<Client> clients;
    private String errorMessage;

}
