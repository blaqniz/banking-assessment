package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "CURRENCY_CONVERSION_RATE")
public class CurrencyConversionRate implements Serializable {

    @Id
    private String currencyCode;

    @JoinColumn(name = "CURRENCY_CODE")
    @OneToOne
    @MapsId
    private Currency currency;

    @Column(name = "CONVERSION_INDICATOR")
    private String conversionIndicator;

    @Column(name = "RATE")
    private BigDecimal rate;

}
