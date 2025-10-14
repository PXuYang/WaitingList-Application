package com.sean.restaurant.parties;

import com.sean.restaurant.parties.dto.CreatePartyRequest;
import com.sean.restaurant.parties.dto.UpdatePartyStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/parties")
public class PartyController {

    private final PartyRepository repo;

    public PartyController(PartyRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<PartyEntity> list() {
        return repo.findAllByCreatedAsc();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartyEntity create(@Valid @RequestBody CreatePartyRequest req) {
        // Parse kind
        final PartyKind kind;
        try {
            kind = PartyKind.valueOf(req.kind.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid kind. Allowed: WAITLIST, RESERVATION");
        }

        // Parse reservation time if provided
        Instant resAt = null;
        if (req.reservationAt != null && !req.reservationAt.isBlank()) {
            try {
                resAt = Instant.parse(req.reservationAt.trim()); // expects ISO-8601 e.g. 2025-10-11T19:00:00Z
            } catch (Exception e) {
                throw new IllegalArgumentException("reservationAt must be ISO-8601, e.g., 2025-10-11T19:00:00Z");
            }
        }

        // Business rules
        if (kind == PartyKind.RESERVATION && resAt == null) {
            throw new IllegalArgumentException("reservationAt is required for RESERVATION");
        }

        // Build and save
        PartyEntity p = new PartyEntity();
        p.setKind(kind);
        p.setName(req.name.trim());
        p.setSize(req.size);
        p.setPhone(req.phone);
        p.setQuotedMinutes(req.quotedMinutes);
        p.setReservationAt(resAt);
        p.setStatus(PartyStatus.WAITING);

        return repo.save(p);
    }

    @PatchMapping("/{id}")
    public PartyEntity updateStatus(@PathVariable UUID id,
                                    @RequestBody UpdatePartyStatusRequest req) {
        PartyEntity p = repo.findById(id).orElseThrow();

        final String raw = (req.status == null ? "" : req.status.trim().toUpperCase());
        final PartyStatus newStatus;
        try {
            newStatus = PartyStatus.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid status. Allowed: WAITING, CALLED, SEATED, CANCELLED, NO_SHOW"
            );
        }

        p.setStatus(newStatus);
        return repo.save(p);
    }
	@GetMapping("/{id}")
	public PartyEntity getOne(@PathVariable java.util.UUID id) {
   	 return repo.findById(id).orElseThrow(); // your GlobalExceptionHandler will return 404 if missing
	}	
}

