package com.opsrequests.repository;

import com.opsrequests.entity.Request;
import com.opsrequests.entity.Request.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long requesterId);

    @Query("SELECT r FROM Request r WHERE r.manager.id = :managerId AND r.status = :status")
    List<Request> findByManagerIdAndStatus(@Param("managerId") Long managerId, @Param("status") Status status);

    List<Request> findByManagerId(Long managerId);
}
