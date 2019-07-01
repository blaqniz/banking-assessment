package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "CLIENT_TYPE")
public class ClientType {

    @Id
    @Column(name = "CLIENT_TYPE_CODE")
    private String clientTypeCode;

    @Column(name = "DESCRIPTION")
    private String description;

}
