package com.datavet.employee.infrastructure.persistence.document;

import com.datavet.shared.domain.valueobject.Address;
import com.datavet.shared.domain.valueobject.DocumentId;
import com.datavet.shared.domain.valueobject.Phone;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Document(collection = "employees")
@CompoundIndexes({
        @CompoundIndex(
                name = "document_number_clinic_idx",
                def  = "{'document_number': 1, 'clinic_id': 1}",
                unique = true
        ),
        @CompoundIndex(
                name = "user_id_idx",
                def  = "{'user_id': 1}",
                unique = true
        ),
        @CompoundIndex(
                name = "clinic_active_idx",
                def  = "{'clinic_id': 1, 'active': 1}"
        )
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDocument {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("clinic_id")
    private String clinicId;

    // Datos personales
    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("document_number")
    private DocumentId documentNumber;

    private Phone   phone;
    private Address address;

    @Field("avatar_url")
    private String avatarUrl;

    // Datos laborales
    private String    speciality;

    @Field("license_number")
    private String licenseNumber;

    @Field("hire_date")
    private LocalDate hireDate;

    // Salary — aplanado para evitar subdocumento innecesario
    @Field("salary_amount")
    private BigDecimal salaryAmount;

    @Field("salary_currency")
    private String salaryCurrency;

    @Field("salary_payments_per_year")
    private Integer salaryPaymentsPerYear;

    @Field("salary_effective_from")
    private LocalDate salaryEffectiveFrom;

    // VacationPolicy — aplanado
    @Field("vacation_annual_days")
    private Integer vacationAnnualDays;

    @Field("vacation_effective_from")
    private LocalDate vacationEffectiveFrom;

    // WorkSchedule — aplanado
    @Field("schedule_weekly_hours")
    private Integer scheduleWeeklyHours;

    @Field("schedule_work_days")
    private List<DayOfWeek> scheduleWorkDays;

    @Field("schedule_entry_time")
    private LocalTime scheduleEntryTime;

    @Field("schedule_exit_time")
    private LocalTime scheduleExitTime;

    @Field("schedule_notes")
    private String scheduleNotes;

    // Control
    private boolean active;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}