package com.chronos.timereg.controller;

import com.chronos.timereg.dto.ContractRequest;
import com.chronos.timereg.model.Contract;
import com.chronos.timereg.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping
    public ResponseEntity<Contract> create(@RequestBody ContractRequest request) {
        return ResponseEntity.ok(contractService.createContract(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract> update(@PathVariable Long id, @RequestBody ContractRequest request) {
        return ResponseEntity.ok(contractService.updateContract(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getContractById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Contract> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(contractService.getContractByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAll() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }
}
