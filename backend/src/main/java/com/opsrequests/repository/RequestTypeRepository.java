package com.opsrequests.repository;

import com.opsrequests.entity.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType, Long> {

    List<RequestType> findByActiveTrue();

    Optional<RequestType> findByCode(String code);

    boolean existsByCode(String code);
}
