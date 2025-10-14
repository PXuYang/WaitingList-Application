package com.sean.restaurant.parties;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "parties")
public class PartyEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyKind kind;           // WAITLIST or RESERVATION

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus status = PartyStatus.WAITING;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int size;

    @Column
    private String phone;            // optional

    @Column
    private Integer quotedMinutes;   // optional for waitlist

    @Column
    private Instant reservationAt;   // only set for reservations

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public PartyEntity() {}

    // --- getters & setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public PartyKind getKind() { return kind; }
    public void setKind(PartyKind kind) { this.kind = kind; }

    public PartyStatus getStatus() { return status; }
    public void setStatus(PartyStatus status) { this.status = status; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getQuotedMinutes() { return quotedMinutes; }
    public void setQuotedMinutes(Integer quotedMinutes) { this.quotedMinutes = quotedMinutes; }

    public Instant getReservationAt() { return reservationAt; }
    public void setReservationAt(Instant reservationAt) { this.reservationAt = reservationAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

