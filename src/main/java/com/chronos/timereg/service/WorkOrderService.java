package com.chronos.timereg.service;

import com.chronos.timereg.model.WorkOrder;
import com.chronos.timereg.model.enums.WOStatus;

import java.util.List;

public interface WorkOrderService {
    WorkOrder createWorkOrder(WorkOrder workOrder);
    WorkOrder updateWorkOrder(Long id, WorkOrder workOrder);
    WorkOrder getWorkOrderById(Long id);
    List<WorkOrder> getAllWorkOrders();
    void deleteWorkOrder(Long id);

    // Endpoint to update status with validations.
    WorkOrder updateWorkOrderStatus(Long id, WOStatus newStatus);
}
