package com.chronos.timereg.service;

import com.chronos.timereg.dto.LeaveEntryRequest;
import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.Contract;
import com.chronos.timereg.model.LeaveEntry;
import com.chronos.timereg.model.User;
import com.chronos.timereg.model.enums.EmploymentType;
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
    public LeaveEntry createLeaveEntry(LeaveEntryRequest dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new BusinessException("User not found."));

        Contract contract = contractRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Contract not found."));

        validateLeaveEntry(dto, contract);

        LeaveEntry leaveEntry = new LeaveEntry();
        leaveEntry.setUser(user);
        leaveEntry.setDate(dto.getDate());
        leaveEntry.setLeaveType(dto.getLeaveType());
        leaveEntry.setLeaveStatus(LeaveStatus.PENDING);

        return leaveEntryRepository.save(leaveEntry);
    }

    @Override
    public LeaveEntry updateLeaveEntry(Long id, LeaveEntryRequest request) {
        LeaveEntry leave = getLeaveEntryById(id);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("User not found with id: " + request.getUserId()));

        Contract contract = contractRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Contract not found."));

        validateLeaveEntry(request, contract);
        leave.setUser(user);
        leave.setDate(request.getDate());
        leave.setLeaveType(request.getLeaveType());
        return leaveEntryRepository.save(leave);
    }

    private void validateLeaveEntry(LeaveEntryRequest dto, Contract contract) {
        if (contract.getEmploymentType() == EmploymentType.INTERNAL) {
            int usedLeaves = leaveEntryRepository.countByUser_IdAndLeaveStatusAndDateBetween(
                    dto.getUserId(),
                    LeaveStatus.APPROVED,
                    LocalDate.of(dto.getDate().getYear(), 1, 1),
                    LocalDate.of(dto.getDate().getYear(), 12, 31)
            );
            if (usedLeaves >= contract.getMaxAnnualLeave()) {
                throw new BusinessException("Exceeded maximum annual leave allowed (" + contract.getMaxAnnualLeave() + ").");
            }
        } else {
            throw new BusinessException("External employees cannot register leaves.");
        }

        if (!leaveEntryRepository.findByUser_IdAndDate(dto.getUserId(), dto.getDate()).isEmpty()) {
            throw new BusinessException("Leave already registered for the given date.");
        }
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

    /**
     * Manager retrieves leaves of his/her subordinates.
     * Assumes the repository method finds leaves for users whose responsible manager is managerId.
     */
    @Override
    public List<LeaveEntry> getLeavesOfSubordinates(Long managerId) {
        return leaveEntryRepository.findByUser_ResponsibleManagerId(managerId);
    }

    /**
     * Manager updates the status of a subordinate's leave.
     * Validations:
     * - The leave must belong to one of the manager's subordinates.
     * - If the leave is PENDING, new status must be APPROVED or REJECTED.
     * - If the leave is CANCELLATION_REQUESTED, new status must be CANCELED (to approve the cancellation)
     *   or remain APPROVED (to reject the cancellation). For simplicity, we'll require that the manager sets
     *   CANCELED if approving a cancellation, and APPROVED if rejecting it.
     */
    @Override
    public LeaveEntry updateSubordinateLeaveStatus(Long leaveId, LeaveStatus newStatus, Long managerId) {
        LeaveEntry leave = getLeaveEntryById(leaveId);
        // Validate that the leave belongs to a subordinate of this manager.
        if (leave.getUser().getResponsibleManager() == null ||
                !leave.getUser().getResponsibleManager().getId().equals(managerId)) {
            throw new BusinessException("Not authorized: This leave does not belong to one of your subordinates.");
        }
        // Validate current status and allowed transitions.
        if (leave.getLeaveStatus() == LeaveStatus.PENDING) {
            if (newStatus != LeaveStatus.APPROVED && newStatus != LeaveStatus.REJECTED) {
                throw new BusinessException("Invalid update: For a PENDING leave, status must be set to APPROVED or REJECTED.");
            }
        } else if (leave.getLeaveStatus() == LeaveStatus.CANCELLATION_REQUESTED) {
            if (newStatus != LeaveStatus.CANCELED && newStatus != LeaveStatus.APPROVED) {
                throw new BusinessException("Invalid update: For a leave with CANCELLATION_REQUESTED status, status must be set to CANCELED (if approving cancellation) or remain APPROVED (if rejecting cancellation).");
            }
        } else {
            throw new BusinessException("Operation not allowed: Leave status " + leave.getLeaveStatus() + " cannot be updated by a manager.");
        }
        leave.setLeaveStatus(newStatus);
        return leaveEntryRepository.save(leave);
    }

    /**
     * Employee cancel leave.
     * Business rules:
     * - If the leave is PENDING, cancel it immediately (set to CANCELED).
     * - If the leave is APPROVED, mark it as CANCELLATION_REQUESTED.
     * - Otherwise, cancellation is forbidden.
     */
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
}
