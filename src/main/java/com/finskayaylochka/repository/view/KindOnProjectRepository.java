package com.finskayaylochka.repository.view;

import com.finskayaylochka.model.supporting.view.KindOnProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface KindOnProjectRepository extends JpaRepository<KindOnProject, Long> {

    List<KindOnProject> findByLoginOrderByBuyDate(String login);

}
