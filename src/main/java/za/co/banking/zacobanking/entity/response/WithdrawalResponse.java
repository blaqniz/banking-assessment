package za.co.banking.zacobanking.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WithdrawalResponse {

    private Boolean success;

    private String message;

    private Map<String, String> notesMap = new HashMap<>();

    public WithdrawalResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
