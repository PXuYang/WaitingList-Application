package com.sean.restaurant.parties;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PartyRepository extends JpaRepository<PartyEntity, UUID> {

    List<PartyEntity> findAllByOrderByCreatedAtAsc();

    List<PartyEntity> findByStatus(PartyStatus status);

    List<PartyEntity> findByKind(PartyKind kind);

    List<PartyEntity> findByKindAndStatus(PartyKind kind, PartyStatus status);
}
