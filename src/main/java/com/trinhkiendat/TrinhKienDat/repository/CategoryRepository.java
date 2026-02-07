package com.trinhkiendat.TrinhKienDat.repository;

import com.trinhkiendat.TrinhKienDat.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}