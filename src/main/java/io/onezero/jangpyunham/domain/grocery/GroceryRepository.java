package io.onezero.jangpyunham.domain.grocery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryRepository extends JpaRepository<Grocery, Long> {

    List<Grocery> findByCategoryId(Long categoryId);
}
