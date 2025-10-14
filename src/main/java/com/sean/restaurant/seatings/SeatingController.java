package com.sean.restaurant.seatings;

import com.sean.restaurant.seatings.dto.SeatPartyRequest;
import com.sean.restaurant.seatings.dto.TableActionRequest;
import com.sean.restaurant.parties.*;
import com.sean.restaurant.tables.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/seatings")
public class SeatingController {

    private final PartyRepository partyRepo;
    private final TableRepository tableRepo;

    public SeatingController(PartyRepository partyRepo, TableRepository tableRepo) {
        this.partyRepo = partyRepo;
        this.tableRepo = tableRepo;
    }

    @PostMapping("/seat")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public SeatingResponse seat(@RequestBody SeatPartyRequest req) {
        PartyEntity party = partyRepo.findById(req.partyId).orElseThrow();
        TableEntity table = tableRepo.findById(req.tableId).orElseThrow();

        if (party.getStatus() == PartyStatus.CANCELLED
                || party.getStatus() == PartyStatus.NO_SHOW
                || party.getStatus() == PartyStatus.SEATED) {
            throw new IllegalArgumentException("Party must be WAITING or CALLED to be seated.");
        }

        if (table.getStatus() == TableStatus.SEATED) {
            throw new IllegalArgumentException("Table is already seated.");
        }
        if (table.getStatus() == TableStatus.UNAVAILABLE
                || table.getStatus() == TableStatus.BUSSING
                || table.getStatus() == TableStatus.PAID) {
            throw new IllegalArgumentException("Table is not available to seat right now.");
        }

        table.setStatus(TableStatus.SEATED);
        table.setActivePartyId(party.getId());
        table.setLastChangeTs(Instant.now());
        party.setStatus(PartyStatus.SEATED);

        tableRepo.save(table);
        partyRepo.save(party);

        return new SeatingResponse(table.getId(), party.getId(),
                table.getStatus().name(), party.getStatus().name());
    }

    @PostMapping("/paid")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public TableEntity markPaid(@RequestBody TableActionRequest req) {
        TableEntity table = tableRepo.findById(req.tableId).orElseThrow();
        if (table.getStatus() != TableStatus.SEATED) {
            throw new IllegalArgumentException("Table must be SEATED to mark PAID.");
        }
        table.setStatus(TableStatus.PAID);
        table.setLastChangeTs(Instant.now());
        return tableRepo.save(table);
    }

    @PostMapping("/bussing")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public TableEntity markBussing(@RequestBody TableActionRequest req) {
        TableEntity table = tableRepo.findById(req.tableId).orElseThrow();
        if (table.getStatus() != TableStatus.PAID) {
            throw new IllegalArgumentException("Table must be PAID to mark BUSSING.");
        }
        table.setStatus(TableStatus.BUSSING);
        table.setLastChangeTs(Instant.now());
        return tableRepo.save(table);
    }

    @PostMapping("/ready")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public TableEntity markReady(@RequestBody TableActionRequest req) {
        TableEntity table = tableRepo.findById(req.tableId).orElseThrow();
        if (table.getStatus() != TableStatus.BUSSING) {
            throw new IllegalArgumentException("Table must be BUSSING to mark AVAILABLE.");
        }
        table.setStatus(TableStatus.AVAILABLE);
        table.setActivePartyId(null);
        table.setLastChangeTs(Instant.now());
        return tableRepo.save(table);
    }

    public record SeatingResponse(UUID tableId, UUID partyId, String tableStatus, String partyStatus) {}
}
