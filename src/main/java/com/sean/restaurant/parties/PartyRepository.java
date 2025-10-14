package com.sean.restaurant.parties;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PartyRepository extends JpaRepository<PartyEntity, UUID> {
    default List<PartyEntity> findAllByCreatedAsc() {
        return findAll(Sort.by(Sort.Direction.ASC, "createdAt"));
    }
}

