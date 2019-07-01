package za.co.banking.zacobanking.entity.response;

import lombok.Data;
import org.springframework.stereotype.Component;
import za.co.banking.zacobanking.entity.Client;

import java.util.List;

@Component
@Data
public class ClientResponse {

    private List<Client> clients;

}
