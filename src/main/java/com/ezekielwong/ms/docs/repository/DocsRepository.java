package com.ezekielwong.ms.docs.repository;

import com.ezekielwong.ms.docs.entity.Docs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocsRepository extends JpaRepository<Docs, String> {

    Docs findByCaseId(String caseId);
}
