package com.trinhkiendat.TrinhKienDat.repository;

import com.trinhkiendat.TrinhKienDat.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {}
