package com.finskayaylochka.service;

import com.finskayaylochka.config.SecurityUtils;
import com.finskayaylochka.config.exception.EntityNotFoundException;
import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.AppUser_;
import com.finskayaylochka.model.UserProfile;
import com.finskayaylochka.model.supporting.ApiResponse;
import com.finskayaylochka.model.supporting.dto.UserDTO;
import com.finskayaylochka.model.supporting.enums.KinEnum;
import com.finskayaylochka.model.supporting.enums.UserRole;
import com.finskayaylochka.model.supporting.filters.AppUserFilter;
import com.finskayaylochka.repository.AppUserRepository;
import com.finskayaylochka.repository.MarketingTreeRepository;
import com.finskayaylochka.specifications.AppUserSpecification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUserService {

  final AppUserRepository appUserRepository;

  final PasswordEncoder passwordEncoder;

  final AccountService accountService;

  final MarketingTreeRepository marketingTreeRepository;

  final AppUserSpecification specification;

  @PersistenceContext(name = "persistanceUnit")
  EntityManager em;

  //    @Cacheable(Constant.USERS_CACHE_KEY)
  public List<AppUser> findAll() {
    return appUserRepository.findAll();
  }

  //    @CacheEvict(value = Constant.USERS_CACHE_KEY, key = "#id")
  public void deleteUser(Long id) {
    appUserRepository.deleteById(id);
  }

  public AppUser findByLogin(String login) {
    return appUserRepository.findByLogin(login);
  }

  public AppUser findById(Long id) {
    return appUserRepository.findOne(id);
  }

  public UserDTO find(UserDTO dto) {
    AppUser user = appUserRepository.findOne(dto.getId());
    if (user == null) {
      throw new EntityNotFoundException("Пользователь не найден");
    }
    return new UserDTO(user);
  }

  public List<AppUser> initializeInvestors() {
    return getUsers("Выберите инвестора");
  }

  public List<AppUser> initializeMultipleInvestors() {
    return findAll();
  }

  public List<AppUser> initializePartners() {
    return getUsers("Выберите партнёра");
  }

  private List<AppUser> getUsers(String s) {
    AppUser partner = new AppUser();
    partner.setId(0L);
    partner.setLogin(s);
    List<AppUser> users = new ArrayList<>(0);
    users.add(partner);
    users.addAll(findAll());
    return users;
  }

  @Transactional
  public void confirm(Long userId) {
    AppUser investor = findById(userId);
    if (!investor.isConfirmed()) {
      investor.setConfirmed(true);
      update(investor);
    }
  }

    /*

    CRITERIA API

     */

  public AppUser findByLoginWithAnnexes(String login) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<AppUser> usersCriteriaQuery = cb.createQuery(AppUser.class);
    Root<AppUser> usersRoot = usersCriteriaQuery.from(AppUser.class);
    usersCriteriaQuery.select(usersRoot).distinct(true);
    usersCriteriaQuery.where(cb.equal(usersRoot.get(AppUser_.login), login));
    return em.createQuery(usersCriteriaQuery).getSingleResult();
  }

  //    @CachePut(value = Constant.USERS_CACHE_KEY, key = "#user?.id")
  public ApiResponse create(AppUser user) {
    ApiResponse response = new ApiResponse();
    if (loginIsBusy(user.getLogin())) {
      response.setError(String.format("Логин [%s] занят", user.getLogin()));
      response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      return response;
    }
    String password = generatePassword();
    user.setPassword(passwordEncoder.encode(password));
    user.getProfile().setUser(user);
    user.getProfile().setEmail(user.getProfile().getEmail().toLowerCase());
    if (Objects.isNull(user.getProfile().getFirstName())) {
      user.getProfile().setFirstName("Инвестор " + user.getLogin().substring(8));
    }
    user.setLogin(user.getLogin().toLowerCase());
    appUserRepository.save(user);
    if (SecurityUtils.isUserInRole(user, UserRole.ROLE_INVESTOR)) {
      accountService.createAccount(user);
    }
    response.setMessage("Пользователь успешно создан");
    return response;
  }

  //    @CachePut(value = Constant.USERS_CACHE_KEY, key = "#user?.id")
  public void updateProfile(AppUser user) {
    AppUser dbUser = findById(user.getId());
    if (null != user.getPassword()) {
      dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    if (null != user.getProfile().getEmail()) {
      dbUser.getProfile().setEmail(user.getProfile().getEmail());
    }
    appUserRepository.save(dbUser);
  }

  public ApiResponse update(AppUser user) {
    AppUser dbUser = findById(user.getId());
    dbUser.setPartner(user.getPartner());
    dbUser.setKin(user.getKin());
    dbUser.setRole(user.getRole());
    updateProfile(user, dbUser);
    appUserRepository.save(dbUser);
    return new ApiResponse("Пользователь успешно обновлён");
  }

  private void updateProfile(AppUser user, AppUser dbUser) {
    UserProfile profile = dbUser.getProfile();
    profile.setFirstName(user.getProfile().getFirstName());
    profile.setLastName(user.getProfile().getLastName());
    profile.setPatronymic(user.getProfile().getPatronymic());
    profile.setEmail(user.getProfile().getEmail());
  }

  private String generatePassword() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * Проверка занят ли логин
   *
   * @param login логин для проверки
   * @return статус
   */
  public boolean loginIsBusy(String login) {
    return appUserRepository.existsByLogin(login);
  }

  /**
   * Деактивировать пользователя, чтоб убрать из маркетингового дерева
   *
   * @param dto DTO для деактивации
   * @return ответ
   */
  public ApiResponse deactivateUser(UserDTO dto) {
    if (dto.getId() == null) {
      return new ApiResponse("Не задан id пользователя", HttpStatus.PRECONDITION_FAILED.value());
    }
    AppUser user = findById(dto.getId());
    if (user == null) {
      return new ApiResponse("Пользователь не найден", HttpStatus.PRECONDITION_FAILED.value());
    }
    user.setConfirmed(false);
    user.setKin(KinEnum.EMPTY);
    user.setPartner(null);
    user.getProfile().setLocked(true);
    update(user);
    marketingTreeRepository.deleteByInvestorId(user.getId());
    return new ApiResponse("Пользователь успешно деактивирован");
  }

  public Page<AppUser> findAll(AppUserFilter filter, Pageable pageable) {
    return appUserRepository.findAll(
        specification.getFilter(filter),
        pageable
    );
  }

  public List<AppUser> getInvestors() {
    return appUserRepository.getInvestors();
  }

  public List<AppUser> getSellers() {
    return appUserRepository.getSellers();
  }

  public List<AppUser> initializeSellers() {
    List<AppUser> appUsers = new ArrayList<>(200);
    AppUser appUser = new AppUser();
    appUser.setId(0L);
    appUser.setLogin("Выберите продавца");
    appUsers.add(appUser);
    appUsers.addAll(getSellers());
    return appUsers;
  }

}
