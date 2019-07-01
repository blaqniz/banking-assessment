package za.co.banking.zacobanking.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@AllArgsConstructor
@Component
@Data
public class WithdrawalResponse {

    private Boolean success;

    private String message;

}
