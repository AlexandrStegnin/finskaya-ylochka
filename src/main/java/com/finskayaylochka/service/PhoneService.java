package com.finskayaylochka.service;

import com.finskayaylochka.config.exception.ApiException;
import com.finskayaylochka.mapper.PhoneMapper;
import com.finskayaylochka.model.Phone;
import com.finskayaylochka.model.supporting.dto.PhoneDTO;
import com.finskayaylochka.repository.PhoneRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexandr Stegnin
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PhoneService {

  PhoneMapper phoneMapper;
  PhoneRepository phoneRepository;

  public Phone findById(Long id) {
    Phone phone = phoneRepository.findOne(id);
    if (Objects.isNull(phone)) {
      throw new ApiException("Не найден телефон по ID", HttpStatus.NOT_FOUND);
    }
    return phone;
  }

  public Phone save(PhoneDTO dto) {
    checkPhoneNumber(dto);
    Phone phone = toEntity(dto);
    return phoneRepository.save(phone);
  }

  public Phone update(PhoneDTO dto) {
    checkPhoneNumber(dto);
    Phone phone = findById(dto.getId());
    phone.setNumber(dto.getNumber());
    return phoneRepository.save(phone);
  }

  public void delete(Long id) {
    phoneRepository.delete(id);
  }

  public List<PhoneDTO> getUserPhones(Long id) {
    List<Phone> phones = phoneRepository.findByAppUserId(id);
    return phones.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  private Phone toEntity(PhoneDTO dto) {
    return phoneMapper.toEntity(dto);
  }

  private PhoneDTO toDTO(Phone phone) {
    return phoneMapper.toDTO(phone);
  }

  private void checkPhoneNumber(PhoneDTO dto) {
    if (phoneRepository.existsByNumber(dto.getNumber())) {
      throw new ApiException("НОМЕР ТЕЛЕФОНА УЖЕ ИСПОЛЬЗУЕТСЯ", HttpStatus.BAD_REQUEST);
    }
  }

}
