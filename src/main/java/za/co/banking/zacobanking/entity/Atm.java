package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ATM")
public class Atm {

    @Id
    @Column(name = "ATM_ID")
    private Integer atmId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LOCATION")
    private String location;

}
