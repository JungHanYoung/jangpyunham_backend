package io.onezero.jangpyunham.domain.purchase;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("select p from Purchase p where p.completed = false")
    List<Purchase> findByUncompleted();

    List<Purchase> findByCompleted(boolean completed, Pageable pageable);



}
