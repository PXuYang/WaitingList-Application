package com.sean.restaurant.tables;

import com.sean.restaurant.tables.dto.CreateTableRequest;
import com.sean.restaurant.tables.dto.StatusChangeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tables")
public class TableController {
    private final TableRepository repo;

    public TableController(TableRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<TableEntity> list() {
        return repo.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TableEntity create(@Valid @RequestBody CreateTableRequest req) {
        TableEntity t = new TableEntity();
        t.setLabel(req.label);
        t.setCapacity(req.capacity);
        t.setStatus(TableStatus.AVAILABLE);
        t.setLastChangeTs(Instant.now());
        return repo.save(t);
    }

    @PatchMapping("/{id}/status")
    public TableEntity setStatus(@PathVariable UUID id, @RequestBody StatusChangeRequest body) {
        TableEntity t = repo.findById(id).orElseThrow();
        TableStatus newStatus = TableStatus.valueOf(body.status.toUpperCase());
        t.setStatus(newStatus);
        t.setLastChangeTs(Instant.now());
        return repo.save(t);
    }
}

