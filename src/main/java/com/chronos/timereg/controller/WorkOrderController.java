package com.chronos.timereg.controller;

import com.chronos.timereg.model.WorkOrder;
import com.chronos.timereg.model.enums.WOStatus;
import com.chronos.timereg.service.WorkOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workorders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    public ResponseEntity<WorkOrder> createWorkOrder(@RequestBody WorkOrder workOrder) {
        return ResponseEntity.ok(workOrderService.createWorkOrder(workOrder));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrder> updateWorkOrder(@PathVariable Long id, @RequestBody WorkOrder workOrder) {
        return ResponseEntity.ok(workOrderService.updateWorkOrder(id, workOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrder> getWorkOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(workOrderService.getWorkOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<WorkOrder>> getAllWorkOrders() {
        return ResponseEntity.ok(workOrderService.getAllWorkOrders());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkOrder(@PathVariable Long id) {
        workOrderService.deleteWorkOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<WorkOrder> updateWorkOrderStatus(@PathVariable Long id,
                                                           @RequestParam String newStatus) {
        WOStatus status;
        try {
            status = WOStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new com.chronos.timereg.exception.BusinessException("Invalid status: " + newStatus);
        }
        WorkOrder updated = workOrderService.updateWorkOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
