package com.chronos.timereg.service;

import com.chronos.timereg.dto.LeaveEntryRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.User;
import com.chronos.timereg.model.enums.LeaveStatus;
import com.chronos.timereg.repository.ContractRepository;
import com.chronos.timereg.repository.LeaveEntryRepository;
import com.chronos.timereg.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class LeaveEntryServiceImpl implements LeaveEntryService {

    private final LeaveEntryRepository leaveEntryRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;

    public LeaveEntryServiceImpl(LeaveEntryRepository leaveEntryRepository,
                                 UserRepository userRepository,
                                 ContractRepository contractRepository) {
        this.leaveEntryRepository = leaveEntryRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public LeaveEntry createLeaveEntry(LeaveEntryRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found with id: " + request.getUserId()));

        // Fetch the contract for the user.
        var contractOpt = contractRepository.findByUser_Id(user.getId());
        if (contractOpt.isEmpty()) {
            throw new BusinessException("Contract not found for user with id: " + user.getId());
        }
        var contract = contractOpt.get();

        // For external employees (trialPeriodMonths != null), leave registration is forbidden.
        if (contract.getTrialPeriodMonths() != null) {
            throw new BusinessException("External employees are not eligible to register leave.");
        }

        // For internal employees, validate against maximum annual leave.
        int approvedLeaves = leaveEntryRepository.countByUser_IdAndLeaveStatusAndDateBetween(
                request.getUserId(),
                LeaveStatus.APPROVED,
                LocalDate.of(request.getDate().getYear(), 1, 1),
                LocalDate.of(request.getDate().getYear(), 12, 31)
        );

        if (contract.getWorkingHoursStart() != null) {
            if (approvedLeaves >= contract.getMaxAnnualLeave()) {
                throw new BusinessException("Exceeded maximum annual leave (" + contract.getMaxAnnualLeave() + ") for the user.");
            }
        }

        LeaveEntry leave = new LeaveEntry();
        leave.setUser(user);
        leave.setDate(request.getDate());
        leave.setLeaveType(request.getLeaveType());
        leave.setLeaveStatus(LeaveStatus.PENDING);
        return leaveEntryRepository.save(leave);
    }

    @Override
    public LeaveEntry updateLeaveEntry(Long id, LeaveEntryRequest request) {
        LeaveEntry leave = getLeaveEntryById(id);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found with id: " + request.getUserId()));
        leave.setUser(user);
        leave.setDate(request.getDate());
        leave.setLeaveType(request.getLeaveType());
        // Typically, updating details should not change the current leave status.
        return leaveEntryRepository.save(leave);
    }

    @Override
    public LeaveEntry getLeaveEntryById(Long id) {
        return leaveEntryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Leave entry not found with id: " + id));
    }

    @Override
    public List<LeaveEntry> getAllLeaveEntries() {
        return leaveEntryRepository.findAll();
    }

    @Override
    public void deleteLeaveEntry(Long id) {
        LeaveEntry leave = getLeaveEntryById(id);
        leaveEntryRepository.delete(leave);
    }

    @Override
    public List<LeaveEntry> getLeavesOfSubordinates(Long managerId) {
        // Assuming the repository method finds leaves for users whose responsibleManager has ID equal to managerId.
        return leaveEntryRepository.findByUser_ResponsibleManagerId(managerId);
    }

    @Override
    public LeaveEntry updateSubordinateLeaveStatus(Long leaveId, LeaveStatus newStatus, Long managerId) {
        LeaveEntry leave = getLeaveEntryById(leaveId);
        if (leave.getUser().getResponsibleManager() == null ||
                !leave.getUser().getResponsibleManager().getId().equals(managerId)) {
            throw new BusinessException("Not authorized: This leave does not belong to one of your subordinates.");
        }
        // Allowed transitions:
        if (leave.getLeaveStatus() == LeaveStatus.PENDING) {
            if (newStatus != LeaveStatus.APPROVED && newStatus != LeaveStatus.REJECTED) {
                throw new BusinessException("For a PENDING leave, status must be set to APPROVED or REJECTED.");
            }
        } else if (leave.getLeaveStatus() == LeaveStatus.CANCELLATION_REQUESTED) {
            if (newStatus != LeaveStatus.CANCELED && newStatus != LeaveStatus.APPROVED) {
                throw new BusinessException("For a CANCELLATION_REQUESTED leave, status must be set to CANCELED (to approve cancellation) or remain APPROVED (to reject cancellation).");
            }
        } else {
            throw new BusinessException("Operation not allowed: Leave status " + leave.getLeaveStatus() + " cannot be updated by a manager.");
        }
        leave.setLeaveStatus(newStatus);
        return leaveEntryRepository.save(leave);
    }

    @Override
    public LeaveEntry cancelLeave(Long leaveId, Long userId) {
        LeaveEntry leave = getLeaveEntryById(leaveId);
        if (!leave.getUser().getId().equals(userId)) {
            throw new BusinessException("Not authorized: You can only cancel your own leave.");
        }
        if (leave.getLeaveStatus() == LeaveStatus.PENDING) {
            leave.setLeaveStatus(LeaveStatus.CANCELED);
        } else if (leave.getLeaveStatus() == LeaveStatus.APPROVED) {
            leave.setLeaveStatus(LeaveStatus.CANCELLATION_REQUESTED);
        } else {
            throw new BusinessException("Cancellation not allowed for leave status: " + leave.getLeaveStatus());
        }
        return leaveEntryRepository.save(leave);
    }

    @Override
    public List<LeaveEntry> getLeaveEntryByUserIdAndDates(Long userId, LocalDate startDate, LocalDate endDate) {
        return leaveEntryRepository.findByUser_IdAndDateBetween(userId, startDate, endDate);
    }
}
