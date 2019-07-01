package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "CREDIT_CARD_LIMIT")
public class CreditCardLimit implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "CLIENT_ACCOUNT_NUMBER")
    private ClientAccount clientAccount;

    @Column(name = "ACCOUNT_LIMIT")
    private BigDecimal accountLimit;

}
