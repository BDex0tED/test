package com.manas.repo;

import com.manas.model.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepo extends JpaRepository<Balance, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT b FROM Balance b WHERE b.id=:id")
    Optional<Balance> findBalanceWithLock(@Param("id") Long id);


}
