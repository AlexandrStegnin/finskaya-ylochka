package com.finskayaylochka.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "AnnexToContracts")
public class AnnexToContracts implements Serializable {

    BigInteger id;
    String annexName;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "annextocontracts_generator")
    @SequenceGenerator(name = "annextocontracts_generator", sequenceName = "annextocontracts_id_seq")
    @Column(name = "Id")
    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    @Column(name = "AnnexName")
    public String getAnnexName() {
        return annexName;
    }

    @Column(name = "FilePath")
    String filePath;

    Date dateLoad;

    @Column(name = "LoadedBy")
    Long loadedBy;

    @Column(name = "DateLoad")
    public Date getDateLoad() {
        return dateLoad;
    }

    public void setDateLoad(Date dateLoad) {
        this.dateLoad = dateLoad;
    }

    public Long getLoadedBy() {
        return loadedBy;
    }

    public void setLoadedBy(Long loadedBy) {
        this.loadedBy = loadedBy;
    }

    public void setAnnexName(String annexName) {
        this.annexName = annexName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
