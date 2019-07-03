package za.co.banking.zacobanking.entity.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProcessNotesAndCoinsResponse {

    private Boolean success = Boolean.FALSE;

    private BigDecimal totalSuggestedAmount = BigDecimal.valueOf(0);

    private Map<String, String> notesMap = new HashMap<>();

}
