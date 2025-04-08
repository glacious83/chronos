package com.chronos.timereg.model;

import com.chronos.timereg.model.enums.LeaveStatus;
import com.chronos.timereg.model.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "leave_entries")
public class LeaveEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The employee who submitted the leave.
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // The date the leave applies to.
    @Column(nullable = false)
    private LocalDate date;

    // The type of leave (for example, FULL or HALF_DAY).
    // You can further define a LeaveType enum if you want to differentiate types.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    // The status of the leave request.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus leaveStatus;

    // Optionally, you can add extra fields (like reason, comments, etc.).
}
