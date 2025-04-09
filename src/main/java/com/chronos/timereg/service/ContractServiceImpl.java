package com.chronos.timereg.service;

import com.chronos.timereg.dto.ContractRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Contract;
import com.chronos.timereg.model.User;
import com.chronos.timereg.model.enums.EmploymentType;
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
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (contractRepository.existsByUser_Id(user.getId()))
            throw new BusinessException("Contract already exists for this user.");

        Contract contract = new Contract();
        contract.setUser(user);
        applyContractDetails(contract, request);

        return contractRepository.save(contract);
    }

    @Override
    public Contract updateContract(Long contractId, ContractRequest request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("Contract not found"));

        applyContractDetails(contract, request);

        return contractRepository.save(contract);
    }

    private void applyContractDetails(Contract contract, ContractRequest request) {
        validateContractDetails(request);

        contract.setEmploymentType(request.getEmploymentType());
        contract.setWorkingHoursStart(request.getWorkingHoursStart());
        contract.setWorkingHoursEnd(request.getWorkingHoursEnd());
        contract.setContractStartDate(request.getContractStartDate());
        contract.setContractEndDate(request.getContractEndDate());
        contract.setDaysOfficePerWeek(request.getDaysOfficePerWeek() != null ? request.getDaysOfficePerWeek() : 3);
        contract.setDaysHomePerWeek(request.getDaysHomePerWeek() != null ? request.getDaysHomePerWeek() : 2);

        if (request.getEmploymentType() == EmploymentType.INTERNAL) {
            contract.setMaxAnnualLeave(request.getMaxAnnualLeave());
            contract.setTrialPeriodMonths(null);
        } else {
            contract.setMaxAnnualLeave(null);
            contract.setTrialPeriodMonths(request.getTrialPeriodMonths());
        }
    }

    private void validateContractDetails(ContractRequest request) {
        long hours = Duration.between(request.getWorkingHoursStart(), request.getWorkingHoursEnd()).toMinutes() / 60;

        if (request.getEmploymentType() == EmploymentType.INTERNAL) {
            if (hours < 7.5 || hours > 8.5)
                throw new BusinessException("Working hours for internal employees must be between 7.5 and 8.5 hours");
            if (request.getMaxAnnualLeave() == null)
                throw new BusinessException("Internal employees must have maxAnnualLeave set");
        } else {
            if (hours < 8 || hours > 8.5)
                throw new BusinessException("Working hours for external employees must be between 8 and 8.5 hours");
            if (request.getTrialPeriodMonths() != null && request.getTrialPeriodMonths() > 3)
                throw new BusinessException("Trial period for external employees cannot exceed 3 months");
        }

        if (request.getContractEndDate() != null && request.getContractEndDate().isBefore(request.getContractStartDate()))
            throw new BusinessException("Contract end date cannot be before start date");
    }

    @Override
    public Contract getContractById(Long contractId) {
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new BusinessException("Contract not found"));
    }

    @Override
    public Contract getContractByUserId(Long userId) {
        return contractRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException("Contract not found for the user"));
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
