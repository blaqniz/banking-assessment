package za.co.banking.zacobanking.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WithdrawalResponse {

    private Boolean success;

    private String message;

}
