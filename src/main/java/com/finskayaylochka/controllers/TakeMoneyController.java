package com.finskayaylochka.controllers;

import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.TakeMoneyDTO;
import com.finskayaylochka.service.TakeMoneyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Alexandr Stegnin
 */
@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TakeMoneyController {

  TakeMoneyService takeMoneyService;

  @ResponseBody
  @PostMapping("/take-money")
  public ApiResponse takeMoney(@RequestBody TakeMoneyDTO takeMoneyDTO) {
    return takeMoneyService.takeMoney(takeMoneyDTO);
  }

  @ResponseBody
  @PostMapping("/take-all-money")
  public ApiResponse takeAllMoney(@RequestBody TakeMoneyDTO takeMoneyDTO) {
    return takeMoneyService.takeAllMoney(takeMoneyDTO);
  }

}
