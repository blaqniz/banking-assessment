package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "CLIENT_SUB_TYPE")
public class ClientSubType {

    @Id
    @Column(name = "CLIENT_SUB_TYPE_CODE")
    private String clientSubTypeCode;

    @ManyToOne
    @JoinColumn(name = "CLIENT_TYPE_CODE", nullable = false)
    private ClientType clientType;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(/*mappedBy = "clientSubType", */cascade = CascadeType.ALL)
    @JoinColumn(name = "CLIENT_SUB_TYPE_CODE")
    private List<Client> client;

}
