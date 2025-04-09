package com.chronos.timereg.service;

import com.chronos.timereg.dto.ContractRequest;
import com.chronos.timereg.model.Contract;

import java.util.List;

public interface ContractService {
    Contract createContract(ContractRequest request);
    Contract updateContract(Long contractId, ContractRequest request);
    Contract getContractById(Long contractId);
    Contract getContractByUserId(Long userId);
    List<Contract> getAllContracts();
    void deleteContract(Long contractId);
}
