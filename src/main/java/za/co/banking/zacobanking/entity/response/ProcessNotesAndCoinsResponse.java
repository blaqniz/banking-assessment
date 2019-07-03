package za.co.banking.zacobanking.entity.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcessNotesAndCoinsResponse {

    private Boolean success = Boolean.FALSE;

    private BigDecimal totalSuggestedAmount = BigDecimal.valueOf(0);

}
