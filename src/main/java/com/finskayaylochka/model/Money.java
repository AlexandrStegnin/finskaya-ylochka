package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.dto.CashingMoneyDTO;
import com.finskayaylochka.model.supporting.dto.SalePaymentDTO;
import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.MoneyState;
import com.finskayaylochka.model.supporting.enums.ShareType;
import com.finskayaylochka.config.SecurityUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Entity
@Table(name = "money")
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = {"investor", "facility", "transaction"})
@EqualsAndHashCode(exclude = {"investor", "facility", "transaction"})
public class Money implements Cash {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "money_generator")
    @SequenceGenerator(name = "money_generator", sequenceName = "money_id_seq")
    Long id;

    @Column(name = "given_cash")
    BigDecimal givenCash;

    @Temporal(TemporalType.DATE)
    @Column(name = "date_given")
    Date dateGiven;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    Facility facility;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "investor_id", referencedColumnName = "id")
    AppUser investor;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "cash_source_id", referencedColumnName = "id")
    CashSource cashSource;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "new_cash_detail_id", referencedColumnName = "id")
    NewCashDetail newCashDetail;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "under_facility_id", referencedColumnName = "id")
    UnderFacility underFacility;

    @Column(name = "date_closing")
    Date dateClosing;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "type_closing_id", referencedColumnName = "id")
    TypeClosing typeClosing;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "share_type")
    ShareType shareType;

    @Column(name = "date_report")
    Date dateReport;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_facility_id", referencedColumnName = "id")
    Facility sourceFacility;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "source_under_facility_id", referencedColumnName = "id")
    UnderFacility sourceUnderFacility;

    @Column(name = "source_flow_id")
    String sourceFlowsId;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    Room room;

    @Column(name = "is_reinvest")
    int isReinvest;

    @Column(name = "source_id")
    Long sourceId;

    @Column(name = "source")
    String source;

    @Column(name = "is_divide")
    int isDivide;

    @Column(name = "real_date_given")
    Date realDateGiven;

    @Column(name = "transaction_uuid")
    String transactionUUID;

    @Transient
    transient AppUser investorBuyer;

    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    MoneyState state = MoneyState.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "acc_tx_id")
    AccountTransaction transaction;

    @Column(name = "creation_time")
    Date creationTime;

    @Column(name = "modified_time")
    Date modifiedTime;

    @Column(name = "created_by")
    String createdBy;

    @Column(name = "modified_by")
    String modifiedBy;

    @PrePersist
    public void prePersist() {
        this.creationTime = new Date();
        this.createdBy = SecurityUtils.getUsername();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedTime = new Date();
        this.modifiedBy = SecurityUtils.getUsername();
    }

    public Money() {
    }

    public Money(Money cash) {
        this.id = null;
        this.givenCash = cash.getGivenCash();
        this.dateGiven = cash.getDateGiven();
        this.facility = cash.getFacility();
        this.investor = cash.getInvestor();
        this.cashSource = cash.getCashSource();
        this.newCashDetail = cash.getNewCashDetail();
        this.underFacility = cash.getUnderFacility();
        this.dateClosing = cash.getDateClosing();
        this.typeClosing = cash.getTypeClosing();
        this.shareType = cash.getShareType();
        this.dateReport = cash.getDateReport();
        this.sourceFacility = cash.getSourceFacility();
        this.sourceUnderFacility = cash.getSourceUnderFacility();
        this.sourceFlowsId = cash.getSourceFlowsId();
        this.room = cash.getRoom();
        this.isReinvest = cash.getIsReinvest();
        this.sourceId = cash.getSourceId();
        this.source = cash.getId().toString();
        this.isDivide = cash.getIsDivide();
        this.realDateGiven = null;
        this.transactionUUID = null;
        this.state = MoneyState.ACTIVE;
        this.transaction = cash.getTransaction();
    }

    public Money(Facility facility, UnderFacility underFacility, AppUser investor,
                 BigDecimal givenCash, Date dateGiven, CashSource cashSource,
                 NewCashDetail newCashDetail, ShareType shareType) {
        this.facility = facility;
        this.underFacility = underFacility;
        this.investor = investor;
        this.givenCash = givenCash;
        this.dateGiven = dateGiven;
        this.cashSource = cashSource;
        this.newCashDetail = newCashDetail;
        this.shareType = shareType;
    }

    public Money(SalePayment salePayment, SalePaymentDTO dto, Facility facility, UnderFacility underFacility, NewCashDetail newCashDetail) {
        this.givenCash = salePayment.getProfitToReInvest();
        this.dateGiven = dto.getDateGiven();
        this.facility = facility;
        this.investor = salePayment.getInvestor();
        this.shareType = ShareType.fromTitle(dto.getShareType());
        this.dateReport = salePayment.getDateSale();
        this.sourceFacility = salePayment.getFacility();
        this.sourceUnderFacility = salePayment.getUnderFacility();
        this.sourceFlowsId = String.valueOf(salePayment.getId());
        this.underFacility = underFacility;
        this.newCashDetail = newCashDetail;
        this.state = MoneyState.ACTIVE;
    }

    public Money(SalePayment salePayment, Facility facility, UnderFacility underFacility, NewCashDetail newCashDetail, Date dateReinvest, String shareType) {
        this.givenCash = salePayment.getProfitToReInvest();
        this.dateGiven = dateReinvest;
        this.facility = facility;
        this.investor = salePayment.getInvestor();
        this.shareType = ShareType.fromTitle(shareType);
        this.dateReport = salePayment.getDateSale();
        this.sourceFacility = salePayment.getFacility();
        this.sourceUnderFacility = salePayment.getUnderFacility();
        this.sourceFlowsId = String.valueOf(salePayment.getId());
        this.underFacility = underFacility;
        this.newCashDetail = newCashDetail;
        this.state = MoneyState.ACTIVE;
    }

    public Money(AppUser investor, Facility facility, UnderFacility underFacility, CashingMoneyDTO cashingMoneyDTO) {
        this.investor = investor;
        this.facility = facility;
        this.underFacility = underFacility;
        this.dateGiven = cashingMoneyDTO.getDateCashing();
        this.givenCash = cashingMoneyDTO.getCash();
    }

    @Transient
    public String getShareTypeTitle() {
        return shareType.getTitle();
    }

    @Transient
    public String getDateGivenToLocalDate() {
        String localDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            localDate = format.format(dateGiven);
        } catch (Exception ignored) {
        }

        return localDate;
    }

    @Transient
    public String getDateClosingToLocalDate() {
        String localDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            localDate = format.format(dateClosing);
        } catch (Exception ignored) {
        }

        return localDate;
    }

    @Transient
    public String getDateReportToLocalDate() {
        String localDate = "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            localDate = format.format(dateReport);
        } catch (Exception ignored) {
        }

        return localDate;
    }

    @Transient
    public AppUser getInvestorBuyer() {
        return investorBuyer;
    }

    public void setInvestorBuyer(AppUser investorBuyer) {
        this.investorBuyer = investorBuyer;
    }

    @Override
    public CashType getCashType() {
        return CashType.INVESTOR_CASH;
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
