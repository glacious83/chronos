package com.chronos.timereg.service;

import com.chronos.timereg.exception.BusinessException;
import com.chronos.timereg.model.WorkOrder;
import com.chronos.timereg.model.enums.WOStatus;
import com.chronos.timereg.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WorkOrderServiceImpl implements WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderServiceImpl(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    public WorkOrder createWorkOrder(WorkOrder workOrder) {
        if (workOrderRepository.findByCode(workOrder.getCode()).isPresent()) {
            throw new BusinessException("Work Order with code " + workOrder.getCode() + " already exists.");
        }
        if (workOrder.getBudget() == null || workOrder.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Budget must be non-negative.");
        }
        if (workOrder.getCompany() == null) {
            throw new BusinessException("Company must be provided.");
        }
        if (workOrder.getPm() == null || workOrder.getPm().trim().isEmpty()) {
            throw new BusinessException("Project Manager (PM) must be provided.");
        }
        if (workOrder.getTitle() == null || workOrder.getTitle().trim().isEmpty()) {
            throw new BusinessException("Title must be provided.");
        }
        if (workOrder.getStartDate() == null || workOrder.getEndDate() == null) {
            throw new BusinessException("Start date and end date must be provided.");
        }
        if (workOrder.getEndDate().isBefore(workOrder.getStartDate())) {
            throw new BusinessException("End date cannot be before start date.");
        }
        if (workOrder.getDeadline() != null && workOrder.getDeadline().isBefore(workOrder.getStartDate())) {
            throw new BusinessException("Deadline cannot be before start date.");
        }
        if (workOrder.getStatus() == null) {
            workOrder.setStatus(WOStatus.PENDING_APPROVAL);
        }
        workOrder.setCreatedDate(LocalDateTime.now());
        return workOrderRepository.save(workOrder);
    }

    @Override
    public WorkOrder updateWorkOrder(Long id, WorkOrder workOrder) {
        WorkOrder existing = getWorkOrderById(id);
        // Update code (unique identifier)
        existing.setCode(workOrder.getCode());

        // Update the linked company. This now references a Company entity.
        if (workOrder.getCompany() == null) {
            throw new BusinessException("Company must be provided.");
        }
        existing.setCompany(workOrder.getCompany());

        // Update PM (assumed to be a string)
        if (workOrder.getPm() == null || workOrder.getPm().trim().isEmpty()) {
            throw new BusinessException("Project Manager (PM) must be provided.");
        }
        existing.setPm(workOrder.getPm());

        // Validate and update budget
        if (workOrder.getBudget() == null || workOrder.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Budget must be non-negative.");
        }
        existing.setBudget(workOrder.getBudget());

        // Update title and description
        if (workOrder.getTitle() == null || workOrder.getTitle().trim().isEmpty()) {
            throw new BusinessException("Title must be provided.");
        }
        existing.setTitle(workOrder.getTitle());
        existing.setDescription(workOrder.getDescription());

        // Validate dates
        if (workOrder.getStartDate() == null || workOrder.getEndDate() == null) {
            throw new BusinessException("Start date and end date must be provided.");
        }
        if (workOrder.getEndDate().isBefore(workOrder.getStartDate())) {
            throw new BusinessException("End date cannot be before start date.");
        }
        existing.setStartDate(workOrder.getStartDate());
        existing.setEndDate(workOrder.getEndDate());

        // Validate and update deadline (if provided)
        if (workOrder.getDeadline() != null && workOrder.getDeadline().isBefore(workOrder.getStartDate())) {
            throw new BusinessException("Deadline cannot be before start date.");
        }
        existing.setDeadline(workOrder.getDeadline());

        // Update the last modified timestamp
        existing.setUpdatedDate(LocalDateTime.now());

        // Note: Status updates should be handled by the updateWorkOrderStatus method.
        return workOrderRepository.save(existing);
    }

    @Override
    public WorkOrder getWorkOrderById(Long id) {
        return workOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Work Order not found with id: " + id));
    }

    @Override
    public List<WorkOrder> getAllWorkOrders() {
        return workOrderRepository.findAll();
    }

    @Override
    public void deleteWorkOrder(Long id) {
        WorkOrder existing = getWorkOrderById(id);
        workOrderRepository.delete(existing);
    }

    @Override
    public WorkOrder updateWorkOrderStatus(Long id, WOStatus newStatus) {
        WorkOrder workOrder = getWorkOrderById(id);
        WOStatus currentStatus = workOrder.getStatus();

        // Business rules:
        if (currentStatus == WOStatus.PENDING_APPROVAL) {
            if (newStatus != WOStatus.APPROVED && newStatus != WOStatus.CANCELED) {
                throw new BusinessException("From PENDING_APPROVAL, status can only change to APPROVED or CANCELED.");
            }
        } else if (currentStatus == WOStatus.APPROVED) {
            // For now, let's allow only transition from APPROVED to CLOSED.
            if (newStatus != WOStatus.CLOSED) {
                throw new BusinessException("For APPROVED work orders, status can only change to CLOSED.");
            }
        } else if (currentStatus == WOStatus.CANCELED || currentStatus == WOStatus.CLOSED) {
            throw new BusinessException("No status change is allowed for work orders with status: " + currentStatus);
        }

        workOrder.setStatus(newStatus);
        workOrder.setUpdatedDate(LocalDateTime.now());
        return workOrderRepository.save(workOrder);
    }
}
