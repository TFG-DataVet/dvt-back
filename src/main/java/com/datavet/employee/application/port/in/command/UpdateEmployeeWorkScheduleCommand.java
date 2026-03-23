package com.datavet.employee.application.port.in.command;

import lombok.Builder;
import lombok.Value;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Value
@Builder
public class UpdateEmployeeWorkScheduleCommand {
    String         employeeId;
    Integer        weeklyHours;
    List<DayOfWeek> workDays;
    LocalTime      entryTime;
    LocalTime      exitTime;
    String         notes;
}