package com.finskayaylochka.service;

import com.finskayaylochka.model.AppUser;
import com.finskayaylochka.model.UsersAnnexToContracts;
import com.finskayaylochka.model.UsersAnnexToContracts_;
import com.finskayaylochka.repository.UserAnnexRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UsersAnnexToContractsService {

    private final UserAnnexRepository annexRepository;

    private final AppUserService appUserService;

    @PersistenceContext(name = "persistanceUnit")
    private EntityManager em;

    public UsersAnnexToContractsService(UserAnnexRepository annexRepository, AppUserService appUserService) {
        this.annexRepository = annexRepository;
        this.appUserService = appUserService;
    }

    public List<UsersAnnexToContracts> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<UsersAnnexToContracts> usersAnnexToContractsCriteriaQuery = cb.createQuery(UsersAnnexToContracts.class);
        Root<UsersAnnexToContracts> usersAnnexToContractsRoot = usersAnnexToContractsCriteriaQuery.from(UsersAnnexToContracts.class);
        usersAnnexToContractsCriteriaQuery.select(usersAnnexToContractsRoot);

        return em.createQuery(usersAnnexToContractsCriteriaQuery).getResultList();
    }

    public UsersAnnexToContracts findById(Long id) {
        return this.em.find(UsersAnnexToContracts.class, id);
    }

    public List<UsersAnnexToContracts> findByUserId(Long userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsersAnnexToContracts> query = cb.createQuery(UsersAnnexToContracts.class);
        Root<UsersAnnexToContracts> root = query.from(UsersAnnexToContracts.class);
        query.select(root);
        query.where(cb.equal(root.get(UsersAnnexToContracts_.userId), userId));
        query.orderBy(cb.desc(root.get(UsersAnnexToContracts_.id)));
        return em.createQuery(query).getResultList();
    }

    public List<UsersAnnexToContracts> findByLogin(String login) {
        if (Objects.isNull(login)) {
            throw new RuntimeException("Необходимо передать логин пользователя");
        }
        AppUser user = appUserService.findByLogin(login);
        if (Objects.isNull(user)) {
            throw new RuntimeException("Пользователь с логином = [" + login + "] не найден");
        }
        return findByUserId(user.getId());
    }

    public void deleteById(Long id) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaDelete<UsersAnnexToContracts> delete = cb.createCriteriaDelete(UsersAnnexToContracts.class);
        Root<UsersAnnexToContracts> usersAnnexToContractsRoot = delete.from(UsersAnnexToContracts.class);
        delete.where(cb.equal(usersAnnexToContractsRoot.get(UsersAnnexToContracts_.id), id));
        this.em.createQuery(delete).executeUpdate();
    }

    public void update(UsersAnnexToContracts usersAnnexToContracts) {
        this.em.merge(usersAnnexToContracts);
    }

    public void create(UsersAnnexToContracts usersAnnexToContracts) {
        this.em.persist(usersAnnexToContracts);
    }

    public boolean haveUnread(String login) {
        AppUser user = appUserService.findByLogin(login);
        if (Objects.nonNull(user)) {
            return haveUnread(user.getId());
        }
        return false;
    }

    private boolean haveUnread(Long userId) {
        return annexRepository.existsByUserIdAndDateReadIsNull(userId);
    }
}
