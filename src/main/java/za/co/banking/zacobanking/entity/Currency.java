package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "CURRENCY")
public class Currency implements Serializable {

    @Id
    @Column(name = "CURRENCY_CODE")
    private String currencyCode;

    @Column(name = "DECIMAL_PLACES")
    private int decimalPlaces;

    @Column(name = "DESCRIPTION")
    private String description;

}
