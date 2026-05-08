package com.datavet.appointment.infrastructure.persistence.repository;

import com.datavet.appointment.infrastructure.persistence.document.AppointmentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAppointmentRepository extends MongoRepository<AppointmentDocument, String> {
}
