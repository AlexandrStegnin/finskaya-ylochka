package com.finskayaylochka.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "AnnexToContracts")
public class AnnexToContracts implements Serializable {

    @GenericGenerator(
        name = "annextocontracts_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "annextocontracts_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "annextocontracts_generator")
    @Column(name = "Id")
    BigInteger id;

    @Column(name = "AnnexName")
    String annexName;

    @Column(name = "FilePath")
    String filePath;

    @Column(name = "DateLoad")
    Date dateLoad;

    @Column(name = "LoadedBy")
    Long loadedBy;

}
