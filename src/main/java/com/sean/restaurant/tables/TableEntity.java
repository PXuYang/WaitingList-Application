package com.sean.restaurant.tables;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dining_tables")
public class TableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status = TableStatus.AVAILABLE;

    private UUID activePartyId; // nullable

    @Column(nullable = false)
    private Instant lastChangeTs = Instant.now();

    public TableEntity() {}

    // Getters/setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { this.status = status; }
    public UUID getActivePartyId() { return activePartyId; }
    public void setActivePartyId(UUID activePartyId) { this.activePartyId = activePartyId; }
    public Instant getLastChangeTs() { return lastChangeTs; }
    public void setLastChangeTs(Instant lastChangeTs) { this.lastChangeTs = lastChangeTs; }
}

