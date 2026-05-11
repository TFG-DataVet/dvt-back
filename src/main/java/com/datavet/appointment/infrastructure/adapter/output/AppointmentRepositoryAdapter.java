package com.datavet.appointment.infrastructure.adapter.output;

import com.datavet.appointment.application.port.out.AppointmentRepositoryPort;
import com.datavet.appointment.domain.model.Appointment;
import com.datavet.appointment.domain.model.PetSnapshot;
import com.datavet.appointment.domain.valueobject.AppointmentStatus;
import com.datavet.appointment.domain.valueobject.AppointmentType;
import com.datavet.appointment.infrastructure.persistence.document.AppointmentDocument;
import com.datavet.appointment.infrastructure.persistence.document.PetSnapshotDocument;
import com.datavet.appointment.infrastructure.persistence.repository.MongoAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AppointmentRepositoryAdapter implements AppointmentRepositoryPort {

    private final MongoAppointmentRepository repository;
    private final MongoTemplate              mongoTemplate;

    // -------------------------------------------------------------------------
    // Mapping
    // -------------------------------------------------------------------------

    private AppointmentDocument toDocument(Appointment appt) {
        PetSnapshotDocument petDoc = null;
        if (appt.getPet() != null) {
            petDoc = PetSnapshotDocument.builder()
                    .petId(appt.getPet().getPetId())
                    .name(appt.getPet().getName())
                    .species(appt.getPet().getSpecies())
                    .build();
        }

        return AppointmentDocument.builder()
                .id(appt.getId())
                .clinicId(appt.getClinicId())
                .emergency(appt.isEmergency())
                .type(appt.getType())
                .status(appt.getStatus())
                .scheduledAt(appt.getScheduledAt())
                .ownerId(appt.getOwnerId())
                .ownerName(appt.getOwnerName())
                .ownerEmail(appt.getOwnerEmail())
                .ownerPhone(appt.getOwnerPhone())
                .pet(petDoc)
                .creationEmployeeId(appt.getCreationEmployeeId())
                .medicalEmployeeId(appt.getMedicalEmployeeId())
                .notes(appt.getNotes())
                .productIds(appt.getProductIds())
                .source(appt.getSource())
                .build();
    }

    private Appointment toDomain(AppointmentDocument doc) {
        PetSnapshot petSnapshot = null;
        if (doc.getPet() != null) {
            petSnapshot = PetSnapshot.of(
                    doc.getPet().getPetId(),
                    doc.getPet().getName(),
                    doc.getPet().getSpecies()
            );
        }

        return Appointment.reconstitute(
                doc.getId(),
                doc.getClinicId(),
                doc.isEmergency(),
                doc.getType(),
                doc.getStatus(),
                doc.getScheduledAt(),
                doc.getOwnerId(),
                doc.getOwnerName(),
                doc.getOwnerEmail(),
                doc.getOwnerPhone(),
                petSnapshot,
                doc.getCreationEmployeeId(),
                doc.getMedicalEmployeeId(),
                doc.getNotes(),
                doc.getProductIds(),
                doc.getSource(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    // -------------------------------------------------------------------------
    // Port methods
    // -------------------------------------------------------------------------

    @Override
    public Appointment save(Appointment entity) {
        return toDomain(repository.save(toDocument(entity)));
    }

    @Override
    public Optional<Appointment> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Appointment> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public List<Appointment> findByClinicIdWithFilters(
            String clinicId,
            LocalDate date,
            AppointmentStatus status,
            AppointmentType type,
            String ownerId) {

        Criteria criteria = Criteria.where("clinic_id").is(clinicId);

        if (date != null) {
            criteria = criteria.and("scheduled_at")
                    .gte(date.atStartOfDay())
                    .lt(date.atTime(LocalTime.MAX));
        }

        if (status != null) {
            criteria = criteria.and("status").is(status);
        }

        if (type != null) {
            criteria = criteria.and("type").is(type);
        }

        if (ownerId != null && !ownerId.isBlank()) {
            criteria = criteria.and("owner_id").is(ownerId);
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, AppointmentDocument.class)
                .stream()
                .map(this::toDomain)
                .toList();
    }
}
