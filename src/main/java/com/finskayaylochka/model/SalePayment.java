package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.ShareType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sale_payment")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = {"facility", "investor", "underFacility"})
public class SalePayment implements Cash {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  Long id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "facility_id", referencedColumnName = "id")
  Facility facility;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "investor_id", referencedColumnName = "id")
  AppUser investor;

  @Enumerated(EnumType.STRING)
  @Column(name = "share_type")
  ShareType shareType;

  @Column(name = "cash_in_facility")
  BigDecimal cashInFacility;

  @Column(name = "date_given")
  Date dateGiven;

  @Column(name = "cash_in_under_facility")
  BigDecimal cashInUnderFacility;

  @Column(name = "profit_to_reinvest")
  BigDecimal profitToReInvest;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "under_facility_id", referencedColumnName = "id")
  UnderFacility underFacility;

  @Column(name = "date_sale")
  Date dateSale;

  @Column(name = "is_reinvest")
  int isReinvest;

  @Column(name = "source_id")
  Long sourceId;

  @Column(name = "acc_tx_id")
  Long accTxId;

  @Column(name = "real_date_given")
  Date realDateGiven;

  public SalePayment(SalePayment salePayment) {
    this.id = null;
    this.facility = salePayment.getFacility();
    this.investor = salePayment.getInvestor();
    this.shareType = salePayment.getShareType();
    this.cashInFacility = salePayment.getCashInFacility();
    this.dateGiven = salePayment.getDateGiven();
    this.cashInUnderFacility = salePayment.getCashInUnderFacility();
    this.underFacility = salePayment.getUnderFacility();
    this.dateSale = salePayment.getDateSale();
    this.isReinvest = 0;
    this.sourceId = null;
    this.profitToReInvest = null;
    this.accTxId = salePayment.getAccTxId();
    this.realDateGiven = salePayment.getRealDateGiven();
  }

  @Override
  public BigDecimal getGivenCash() {
    return this.profitToReInvest;
  }

  @Override
  public CashType getCashType() {
    return CashType.SALE_CASH;
  }

  @Override
  public String getOwnerName() {
    return this.investor.getLogin();
  }

  @Override
  public String getFromName() {
    return this.facility.getFullName();
  }
}
