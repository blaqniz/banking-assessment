package za.co.banking.zacobanking.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "CLIENT")
public class Client {

    @Id
    @Column(name = "CLIENT_ID")
    private Integer clientId;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "name")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "dob")
    private Date dob;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CLIENT_ID")
    private List<ClientAccount> clientAccounts;

/*    @ManyToOne
    @JoinColumn(name = "CLIENT_SUB_TYPE_CODE", nullable = false)
    private ClientSubType clientSubType;*/

}
