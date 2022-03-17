package com.finskayaylochka.service;

import com.finskayaylochka.config.exception.EntityNotFoundException;
import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.Money;
import com.finskayaylochka.model.UnderFacility;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.FacilityDTO;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import com.finskayaylochka.repository.FacilityRepository;
import com.finskayaylochka.repository.MoneyRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FacilityService {

  UnderFacilityService underFacilityService;
  FacilityRepository facilityRepository;
  MoneyRepository moneyRepository;
  AccountService accountService;

  public List<Facility> findAll() {
    return facilityRepository.findAll();
  }

  public Facility findById(Long id) {
    return facilityRepository.findOne(id);
  }

  public List<Facility> initializeFacilities() {
    List<Facility> facilitiesList = new ArrayList<>(0);
    Facility facility = new Facility();
    facility.setId(0L);
    facility.setName("Выберите объект");
    facilitiesList.add(facility);
    facilitiesList.addAll(findAll());
    return facilitiesList;
  }

  public List<Facility> initializeFacilitiesForMultiple() {
    return findAll();
  }

  public Facility findByName(String name) {
    return facilityRepository.findByName(name);
  }

  private void deleteById(Long id) {
    facilityRepository.delete(id);
    accountService.deleteByOwnerId(id, OwnerType.FACILITY);
  }

  private ApiResponse create(Facility facility) {
    ApiResponse apiResponse = accountService.checkAccountNumber(facility);
    if (Objects.nonNull(apiResponse)) {
      return apiResponse;
    }
    facilityRepository.saveAndFlush(facility);
    accountService.createAccount(facility);
    apiResponse = new ApiResponse("Объект " + facility.getName() + " успешно добавлен.", HttpStatus.OK.value());
    return apiResponse;
  }

  public ApiResponse delete(FacilityDTO dto) {
    Facility facility = findById(dto.getId());
    if (Objects.isNull(facility)) {
      throw new EntityNotFoundException("Не найден объект для удаления");
    }
    List<Money> monies = moneyRepository.findByFacilityId(facility.getId());
    if (!monies.isEmpty()) {
      return new ApiResponse(String.format("В объект [%s] вложены деньги, необходимо перераспределить их", facility.getName()), HttpStatus.BAD_REQUEST.value());
    }
    try {
      List<UnderFacility> underFacilities = underFacilityService.findByFacilityId(facility.getId());
      underFacilities.forEach(underFacility -> underFacilityService.deleteById(underFacility.getId()));
      deleteById(facility.getId());
      return new ApiResponse("Объект " + facility.getName() + " успешно удалён.");
    } catch (Exception e) {
      log.error("Произошла ошибка: {}", e.getLocalizedMessage());
      return new ApiResponse("При удалении объекта " + facility.getName() + " произошла ошибка.", HttpStatus.BAD_REQUEST.value());
    }
  }

  public ApiResponse update(FacilityDTO dto) {
    Facility facility = new Facility(dto);
    facilityRepository.save(facility);
    updateAccount(facility);
    return new ApiResponse("Объект успешно обновлён");
  }

  private void updateAccount(Facility facility) {
    Account account = accountService.findByOwnerId(facility.getId(), OwnerType.FACILITY);
    if (Objects.nonNull(account) && !facility.getName().equals(account.getOwnerName())) {
      account.setOwnerName(facility.getName());
      accountService.update(account);
    }
  }

  public ApiResponse create(FacilityDTO dto) {
    Facility facility = new Facility(dto);
    return create(facility);
  }

  /**
   * Найти объекты, в которых у инвестора есть незакрытые суммы
   *
   * @param investorId id инвестора
   * @return список объектов
   */
  public List<Facility> findOpenedProjects(Long investorId) {
    return facilityRepository.findOpenedProjects(investorId);
  }

}
