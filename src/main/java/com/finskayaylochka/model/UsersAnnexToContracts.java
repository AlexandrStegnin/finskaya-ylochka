package com.finskayaylochka.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@ToString
@EqualsAndHashCode
@Table(name = "UsersAnnexToContracts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersAnnexToContracts implements Serializable {

    BigInteger id;

    @GenericGenerator(
        name = "usersannextocontracts_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "usersannextocontracts_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersannextocontracts_generator")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @Column(name = "UserId")
    Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "AnnexRead")
    int annexRead;

    public int getAnnexRead() {
        return annexRead;
    }

    public void setAnnexRead(int annexRead) {
        this.annexRead = annexRead;
    }

    @Column(name = "DateRead")
    Date dateRead;

    public Date getDateRead() {
        return dateRead;
    }

    public void setDateRead(Date dateRead) {
        this.dateRead = dateRead;
    }

    @Transient
    public String getDateReadToLocalDate() {
        String localDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            localDate = format.format(dateRead);
        } catch (Exception ignored) {
        }

        return localDate;
    }

    AnnexToContracts annex;

    @OneToOne(cascade =
            {
                    CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.REFRESH,
                    CascadeType.PERSIST
            },
            fetch = FetchType.EAGER)
    @JoinColumn(name = "AnnexToContractsId", referencedColumnName = "id")
    public AnnexToContracts getAnnex() {
        return annex;
    }

    public void setAnnex(AnnexToContracts annex) {
        this.annex = annex;
    }
}
