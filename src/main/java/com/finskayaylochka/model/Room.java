package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.RoomDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "room")
@ToString(exclude = "underFacility")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = "underFacility")
public class Room implements Serializable {

    @GenericGenerator(
        name = "room_generator",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            @org.hibernate.annotations.Parameter(name = "sequence_name", value = "room_id_seq"),
            @org.hibernate.annotations.Parameter(name = "increment_size", value = "1"),
            @org.hibernate.annotations.Parameter(name = "optimizer", value = "hilo")
        }
    )
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "room_generator")
    Long id;

    @Column(name = "name")
    String name;

    @Column(name = "cost")
    BigDecimal cost;

    @Column(name = "room_size")
    BigDecimal roomSize;

    @Column(name = "sold")
    boolean sold;

    @Column(name = "date_sale")
    Date dateSale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "under_facility_id", referencedColumnName = "id")
    UnderFacility underFacility;

    @Column(name = "date_buy")
    Date dateBuy;

    @Column(name = "sale_price")
    BigDecimal salePrice;

    @Column(name = "total_year_profit")
    BigDecimal totalYearProfit;

    public Room(RoomDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.cost = dto.getCost();
        this.roomSize = dto.getRoomSize();
        this.sold = dto.isSold();
        this.dateSale = dto.getDateSale();
        this.dateBuy = dto.getDateBuy();
        this.salePrice = dto.getSalePrice();
        this.totalYearProfit = dto.getTotalYearProfit();
        this.underFacility = new UnderFacility(dto.getUnderFacility());
    }

}
