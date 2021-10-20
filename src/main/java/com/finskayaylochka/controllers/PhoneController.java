package com.finskayaylochka.controllers;

import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.PhoneDTO;
import com.finskayaylochka.model.supporting.dto.UserDTO;
import com.finskayaylochka.service.PhoneService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */
@RestController
@RequestMapping("/users/phones")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PhoneController {

  PhoneService phoneService;

  @PostMapping
  public List<PhoneDTO> getUserPhones(@RequestBody UserDTO dto) {
    return phoneService.getUserPhones(dto.getId());
  }

  @PostMapping("/add")
  public ApiResponse addUserPhone(@RequestBody PhoneDTO dto) {
    phoneService.save(dto);
    return ApiResponse.builder()
        .message("Телефон успешно добавлен")
        .status(HttpStatus.OK.value())
        .build();
  }

  @PostMapping("/edit")
  public ApiResponse updateUserPhone(@RequestBody PhoneDTO dto) {
    phoneService.update(dto);
    return ApiResponse.builder()
        .message("Телефон успешно обновлён")
        .status(HttpStatus.OK.value())
        .build();
  }

  @PostMapping("/delete")
  public ApiResponse deleteUserPhone(@RequestBody PhoneDTO dto) {
    phoneService.delete(dto.getId());
    return ApiResponse.builder()
        .message("Телефон успешно удалён")
        .status(HttpStatus.OK.value())
        .build();
  }

}
