package com.finskayaylochka.model.supporting.dto;

import com.finskayaylochka.model.Account;
import lombok.Data;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Data
public class AccountSummaryDTO {

    private Long accountId;

    private List<Account> owners;

    private List<Account> payers;

    private String parentPayer;

}
