package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ACCOUNT_TYPE")
public class AccountType {

    @Id
    @Column(name = "ACCOUNT_TYPE_CODE")
    private String accountTypeCode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TRANSACTIONAL")
    private boolean transactional;
}
