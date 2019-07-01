package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DENOMINATION_TYPE")
public class DenominationType {

    @Id
    @Column(name = "DENOMINATION_TYPE_CODE ")
    private String denominationTypeCode;

    @Column(name = "DESCRIPTION")
    private String description;

}
