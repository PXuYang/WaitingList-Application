package com.sean.restaurant.tables;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TableRepository extends JpaRepository<TableEntity, UUID> {}

