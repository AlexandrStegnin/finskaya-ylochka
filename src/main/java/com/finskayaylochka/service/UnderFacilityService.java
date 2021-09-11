package com.finskayaylochka.service;

import com.finskayaylochka.model.Account;
import com.finskayaylochka.model.Facility;
import com.finskayaylochka.model.UnderFacility;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.UnderFacilityDTO;
import com.finskayaylochka.model.supporting.enums.OwnerType;
import com.finskayaylochka.repository.UnderFacilityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UnderFacilityService {

  UnderFacilityRepository underFacilityRepository;

  AccountService accountService;

  //    @Cacheable(Constant.UNDER_FACILITIES_CACHE_KEY)
  public List<UnderFacility> findAll() {
    return underFacilityRepository.findAll();
  }

  //    @Cacheable(Constant.UNDER_FACILITIES_CACHE_KEY)
  public UnderFacility findByName(String name) {
    return underFacilityRepository.findByName(name);
  }

  //    @Cacheable(Constant.UNDER_FACILITIES_CACHE_KEY)
  public UnderFacility findById(Long id) {
    return underFacilityRepository.findOne(id);
  }

  //    @CacheEvict(Constant.UNDER_FACILITIES_CACHE_KEY)
  public void deleteById(Long id) {
    underFacilityRepository.delete(id);
    accountService.deleteByOwnerId(id, OwnerType.UNDER_FACILITY);
  }

  public ApiResponse delete(UnderFacilityDTO dto) {
    underFacilityRepository.delete(dto.getId());
    accountService.deleteByOwnerId(dto.getId(), OwnerType.UNDER_FACILITY);
    return new ApiResponse("Подобъект успешно удалён");
  }

  //    @CachePut(Constant.UNDER_FACILITIES_CACHE_KEY)
  public void create(UnderFacility underFacility) {
    underFacilityRepository.saveAndFlush(underFacility);
    Facility facility = underFacility.getFacility();
    Account account = accountService.findByOwnerId(facility.getId(), OwnerType.FACILITY);
    int countUnderFacilities = underFacilityRepository.countByFacilityId(facility.getId());
    accountService.createAccount(underFacility, account, countUnderFacilities);
  }

  //    @Cacheable(Constant.UNDER_FACILITIES_CACHE_KEY)
  public List<UnderFacility> findByFacilityId(Long id) {
    return underFacilityRepository.findByFacilityId(id);
  }

  public List<UnderFacility> initializeUnderFacilities() {
    List<UnderFacility> underFacilityList = new ArrayList<>(0);
    UnderFacility underFacility = new UnderFacility();
    underFacility.setId(0L);
    underFacility.setName("Выберите подобъект");
    underFacilityList.add(underFacility);
    underFacilityList.addAll(findAll());
    return underFacilityList;
  }

  public List<UnderFacility> initializeUnderFacilitiesList() {
    return findAll();
  }

  //    @CachePut(value = Constant.UNDER_FACILITIES_CACHE_KEY, key = "#underFacility.id")
  public ApiResponse update(UnderFacilityDTO dto) {
    UnderFacility underFacility = new UnderFacility(dto);
    underFacilityRepository.save(underFacility);
    updateAccount(underFacility);
    return new ApiResponse("Подобъект успешно обновлён");
  }

  private void updateAccount(UnderFacility underFacility) {
    Account account = accountService.findByOwnerId(underFacility.getId(), OwnerType.UNDER_FACILITY);
    if (Objects.nonNull(account) && !underFacility.getName().equals(account.getOwnerName())) {
      account.setOwnerName(underFacility.getName());
      accountService.update(account);
    }
  }

  /**
   * Создать подобъект на основе DTO
   *
   * @param dto DTO подобъекта
   * @return ответ
   */
  public ApiResponse create(UnderFacilityDTO dto) {
    UnderFacility underFacility = new UnderFacility(dto);
    underFacilityRepository.save(underFacility);
    Facility facility = underFacility.getFacility();
    Account account = accountService.findByOwnerId(facility.getId(), OwnerType.FACILITY);
    int countUnderFacilities = underFacilityRepository.countByFacilityId(facility.getId());
    accountService.createAccount(underFacility, account, countUnderFacilities);
    return new ApiResponse("Подобъект успешно создан");
  }
}
