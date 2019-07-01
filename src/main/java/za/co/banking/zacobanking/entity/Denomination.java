package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "DENOMINATION")
public class Denomination {

    @Id
    @Column(name = "DENOMINATION_ID")
    private Integer denominationId;

    @Column(name = "VALUE")
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "DENOMINATION_TYPE_CODE", nullable = false)
    private DenominationType denominationType;

}
