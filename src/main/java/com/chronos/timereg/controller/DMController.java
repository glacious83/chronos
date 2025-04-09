package com.chronos.timereg.controller;

import com.chronos.timereg.model.DM;
import com.chronos.timereg.service.DMService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dms")
public class DMController {

    private final DMService dmService;

    public DMController(DMService dmService) {
        this.dmService = dmService;
    }

    @PostMapping
    public ResponseEntity<DM> createDM(@RequestBody DM dm) {
        return ResponseEntity.ok(dmService.createDM(dm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DM> updateDM(@PathVariable Long id, @RequestBody DM dm) {
        return ResponseEntity.ok(dmService.updateDM(id, dm));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DM> getDMById(@PathVariable Long id) {
        return ResponseEntity.ok(dmService.getDMById(id));
    }

    @GetMapping
    public ResponseEntity<List<DM>> getAllDMs() {
        return ResponseEntity.ok(dmService.getAllDMs());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDM(@PathVariable Long id) {
        dmService.deleteDM(id);
        return ResponseEntity.noContent().build();
    }
}
