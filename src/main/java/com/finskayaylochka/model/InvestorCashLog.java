package com.finskayaylochka.model;

import com.finskayaylochka.model.supporting.enums.CashType;
import com.finskayaylochka.model.supporting.enums.ShareType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Alexandr Stegnin
 */

@Data
@Entity
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "investor_cash_log")
public class InvestorCashLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "investor_cash_log_generator")
    @SequenceGenerator(name = "investor_cash_log_generator", sequenceName = "investor_cash_log_id_seq")
    @Column(name = "id")
    Long id;

    @Column(name = "cash_id")
    Long cashId;

    @OneToOne
    @JoinColumn(name = "investor_id")
    AppUser investor;

    @OneToOne
    @JoinColumn(name = "facility_id")
    Facility facility;

    @OneToOne
    @JoinColumn(name = "under_facility_id")
    UnderFacility underFacility;

    @Column(name = "given_cash")
    BigDecimal givenCash;

    @Column(name = "date_given_cash")
    Date dateGivenCash;

    @OneToOne
    @JoinColumn(name = "cash_source_id")
    CashSource cashSource;

    @OneToOne
    @JoinColumn(name = "new_cash_detail_id")
    NewCashDetail newCashDetail;

    @Column(name = "date_closing_invest")
    Date dateClosingInvest;

    @OneToOne
    @JoinColumn(name = "type_closing_invest_id")
    TypeClosing typeClosing;

    @Enumerated(EnumType.STRING)
    @Column(name = "share_type")
    ShareType shareType;

    @Column(name = "date_report")
    Date dateReport;

    @OneToOne
    @JoinColumn(name = "source_facility_id")
    Facility sourceFacility;

    @OneToOne
    @JoinColumn(name = "source_under_facility_id")
    UnderFacility sourceUnderFacility;

    @Column(name = "source_flows_id")
    String sourceFlowsId;

    @OneToOne
    @JoinColumn(name = "room_id")
    Room room;

    @Column(name = "reinvest")
    int reinvest;

    @Column(name = "source_id")
    Long sourceId;

    @Column(name = "source")
    String source;

    @Column(name = "divide")
    int divide;

    @Column(name = "real_date_given")
    Date realDateGiven;

    @OneToOne
    @JoinColumn(name = "tx_id")
    TransactionLog transactionLog;

    @Enumerated
    @Column(name = "instance_of")
    CashType instanceOf;

    public InvestorCashLog(Money cash, TransactionLog log, CashType instanceOf) {
        this.cashId = cash.getId();
        this.investor = cash.getInvestor();
        this.facility = cash.getFacility();
        this.underFacility = cash.getUnderFacility();
        this.givenCash = cash.getGivenCash();
        this.dateGivenCash = cash.getDateGiven();
        this.cashSource = cash.getCashSource();
        this.newCashDetail = cash.getNewCashDetail();
        this.shareType = cash.getShareType();
        this.dateReport = cash.getDateReport();
        this.sourceFacility = cash.getSourceFacility();
        this.sourceUnderFacility = cash.getSourceUnderFacility();
        this.sourceFlowsId = cash.getSourceFlowsId();
        this.room = cash.getRoom();
        this.reinvest = cash.getIsReinvest();
        this.sourceId = cash.getSourceId();
        this.source = cash.getSource();
        this.divide = cash.getIsDivide();
        this.transactionLog = log;
        this.instanceOf = instanceOf;
    }

    public InvestorCashLog(SalePayment flowsSale, TransactionLog log, CashType instanceOf) {
        this.cashId = flowsSale.getId();
        this.investor = flowsSale.getInvestor();
        this.facility = flowsSale.getFacility();
        this.dateGivenCash = flowsSale.getDateGiven();
        this.givenCash = flowsSale.getProfitToReInvest();
        this.transactionLog = log;
        this.instanceOf = instanceOf;
        this.shareType = flowsSale.getShareType();
    }

}
