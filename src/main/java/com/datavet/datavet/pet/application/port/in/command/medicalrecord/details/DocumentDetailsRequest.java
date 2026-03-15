package com.datavet.datavet.pet.application.port.in.command.medicalrecord.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class DocumentDetailsRequest implements MedicalRecordDetailsRequest {
    private String documentName;
    private String documentType;
    private String fileUrl;
    private String mimeType;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private String description;
    private Long fileSizeInBytes;
    private boolean confidential;
    private String checksum;
}