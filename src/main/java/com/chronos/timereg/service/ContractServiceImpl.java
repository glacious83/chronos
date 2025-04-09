package com.chronos.timereg.service;

import com.chronos.timereg.dto.ContractRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Contract;
import com.chronos.timereg.repository.ContractRepository;
import com.chronos.timereg.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final UserRepository userRepository;

    public ContractServiceImpl(ContractRepository contractRepository, UserRepository userRepository) {
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Contract createContract(ContractRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found with id: " + request.getUserId()));

        if (contractRepository.findByUser_Id(user.getId()).isPresent()) {
            throw new BusinessException("Contract already exists for this user.");
        }

        Contract contract = new Contract();
        contract.setUser(user);
        applyContractDetails(contract, request);
        return contractRepository.save(contract);
    }

    @Override
    public Contract updateContract(Long contractId, ContractRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + contractId));
        applyContractDetails(contract, request);
        return contractRepository.save(contract);
    }

    private void applyContractDetails(Contract contract, ContractRequest request) {
        validateContractDetails(request);
        contract.setWorkingHoursStart(request.getWorkingHoursStart());
        contract.setWorkingHoursEnd(request.getWorkingHoursEnd());
        contract.setContractStartDate(request.getContractStartDate());
        contract.setContractEndDate(request.getContractEndDate());
        contract.setDaysOfficePerWeek(request.getDaysOfficePerWeek() != null ? request.getDaysOfficePerWeek() : 3);
        contract.setDaysHomePerWeek(request.getDaysHomePerWeek() != null ? request.getDaysHomePerWeek() : 2);

        // Determine internal vs. external based on trialPeriodMonths
        if (request.getTrialPeriodMonths() == null) {
            // Internal: maxAnnualLeave must be provided
            if (request.getMaxAnnualLeave() == null) {
                throw new BusinessException("Internal employees must have maximum annual leave specified.");
            }
            contract.setMaxAnnualLeave(request.getMaxAnnualLeave());
            contract.setTrialPeriodMonths(null);
        } else {
            // External: trial period must be <= 3 months, and maxAnnualLeave should be null.
            if (request.getTrialPeriodMonths() > 3) {
                throw new BusinessException("Trial period for external employees cannot exceed 3 months.");
            }
            contract.setTrialPeriodMonths(request.getTrialPeriodMonths());
            contract.setMaxAnnualLeave(null);
        }
    }

    private void validateContractDetails(ContractRequest request) {
        if (request.getWorkingHoursStart() == null || request.getWorkingHoursEnd() == null) {
            throw new BusinessException("Working hours start and end must be provided.");
        }
        long minutes = Duration.between(request.getWorkingHoursStart(), request.getWorkingHoursEnd()).toMinutes();
        double hours = minutes / 60.0;

        // If trialPeriodMonths is provided, we consider it external.
        if (request.getTrialPeriodMonths() == null) {
            // Internal: working hours must be between 7.5 and 8.5.
            if (hours < 7.5 || hours > 8.5) {
                throw new BusinessException("Internal employees' working hours must be between 7.5 and 8.5 hours.");
            }
        } else {
            // External: working hours must be between 8.0 and 8.5.
            if (hours < 8.0 || hours > 8.5) {
                throw new BusinessException("External employees' working hours must be between 8 and 8.5 hours.");
            }
        }
        if (request.getContractStartDate() == null) {
            throw new BusinessException("Contract start date must be provided.");
        }
        if (request.getContractEndDate() != null && request.getContractEndDate().isBefore(request.getContractStartDate())) {
            throw new BusinessException("Contract end date cannot be before the start date.");
        }
    }

    @Override
    public Contract getContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("Contract not found with id: " + contractId));
    }

    @Override
    public Contract getContractByUserId(Long userId) {
        return contractRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException("Contract not found for user with id: " + userId));
    }

    @Override
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    @Override
    public void deleteContract(Long contractId) {
        Contract contract = getContractById(contractId);
        contractRepository.delete(contract);
    }
}
