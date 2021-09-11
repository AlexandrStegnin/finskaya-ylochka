package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.OwnerType;
import lombok.Data;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@Table(name = "account")
@ToString(of = {"id", "accountNumber"})
@EqualsAndHashCode(of = {"id", "accountNumber"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Account parentAccount;

    @OneToMany(mappedBy = "recipient")
    private Set<AccountTransaction> transactions = new HashSet<>();

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "owner_name")
    private String ownerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type")
    private OwnerType ownerType;

}
