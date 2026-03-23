package com.datavet.clinic.infrastructure.adapter.output;

import com.datavet.clinic.application.port.out.ClinicRepositoryPort;
import com.datavet.clinic.domain.model.Clinic;
import com.datavet.clinic.domain.valueobject.ClinicSchedule;
import com.datavet.clinic.infrastructure.persistence.document.ClinicDocument;
import com.datavet.clinic.infrastructure.persistence.repository.MongoClinicRepositoryAdapter;
import com.datavet.shared.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClinicRepositoryAdapter implements ClinicRepositoryPort {

    private final MongoClinicRepositoryAdapter repository;

    private ClinicDocument toDocument(Clinic clinic) {
        return ClinicDocument.builder()
                .id(clinic.getClinicID())
                .name(clinic.getClinicName())
                .legalName(clinic.getLegalName())
                .legalNumber(clinic.getLegalNumber())
                .legalType(clinic.getLegalType())
                .address(clinic.getAddress())
                .phone(clinic.getPhone())
                .email(clinic.getEmail())
                .logoUrl(clinic.getLogoUrl())
                .scheduleOpenDays(clinic.getSchedule()  != null ? clinic.getSchedule().getOpenDays()   : null)
                .scheduleOpenTime(clinic.getSchedule()  != null ? clinic.getSchedule().getOpenTime()   : null)
                .scheduleCloseTime(clinic.getSchedule() != null ? clinic.getSchedule().getCloseTime()  : null)
                .scheduleNotes(clinic.getSchedule()     != null ? clinic.getSchedule().getNotes()      : null)
                .status(clinic.getStatus())
                .build();
    }

    private Clinic toDomain(ClinicDocument doc) {
        // Reconstruimos el value object solo si hay datos de schedule
        ClinicSchedule schedule = null;
        if (doc.getScheduleOpenDays() != null) {
            schedule = ClinicSchedule.of(
                    doc.getScheduleOpenDays(),
                    doc.getScheduleOpenTime(),
                    doc.getScheduleCloseTime(),
                    doc.getScheduleNotes()
            );
        }

        return Clinic.reconstitute(
                doc.getId(),
                doc.getName(),
                doc.getLegalName(),
                doc.getLegalNumber(),
                doc.getLegalType(),
                doc.getAddress(),
                doc.getPhone(),
                doc.getEmail(),
                doc.getLogoUrl(),
                schedule,
                doc.getStatus(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }

    @Override
    public Clinic save(Clinic clinic) {
        return toDomain(repository.save(toDocument(clinic)));
    }

    @Override
    public Optional<Clinic> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Clinic> findAll() {
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
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByLegalNumber(String legalNumber) {
        return repository.existsByLegalNumber(legalNumber);
    }

    @Override
    public boolean existsByEmailAndIdNot(Email email, String id) {
        return repository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByLegalNumberAndIdNot(String legalNumber, String id) {
        return repository.existsByLegalNumberAndIdNot(legalNumber, id);
    }
}