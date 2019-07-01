package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "CLIENT_ACCOUNT")
public class ClientAccount {

    @Id
    @Column(name = "CLIENT_ACCOUNT_NUMBER")
    private String clientAccountNumber;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ACCOUNT_TYPE_CODE")
    private AccountType accountType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CURRENCY_CODE", nullable = false)
    private Currency currency;

    @Column(name = "DISPLAY_BALANCE")
    private BigDecimal displayBalance;

    @OneToOne
    private CurrencyConversionRate currencyConversionRate;

}
