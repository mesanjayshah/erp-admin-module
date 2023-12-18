package com.mesanjay.admin.repository;

import com.mesanjay.admin.model.Admin;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByUsername(String username);

    @Query("SELECT u FROM Admin u WHERE u.isAccountNonLocked = false AND u.lockTime < :currentTime")
    List<Admin> findExpiredLockedAdmin(@Param("currentTime") Date date);

    @Query("update Admin u set u.failedAttempt=?1 where u.username=?2 ")
    @Modifying
    @Transactional
    void updateFailedAttempt(int attempt, String username);

}
