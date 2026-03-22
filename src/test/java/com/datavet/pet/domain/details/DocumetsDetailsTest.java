package com.datavet.pet.domain.details;

import com.datavet.pet.domain.exception.MedicalRecordValidationException;
import com.datavet.pet.domain.model.details.document.DocumentDetails;
import com.datavet.pet.domain.model.details.weight.WeightDetails;
import com.datavet.pet.domain.model.details.weight.WeightUnit;
import com.datavet.pet.domain.model.action.RecordAction;
import com.datavet.pet.domain.valueobject.MedicalRecordType;
import com.datavet.pet.testutil.medicalrecord.DocumentDetailsTestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DocumentDetails Domain Model Tests")
class DocumetsDetailsTest {

    // ================================================================
    // create() — happy path
    // ================================================================

    @Test
    @DisplayName("Should create DocumentDetails with all valid fields (non-confidential)")
    void create_shouldCreateDocumentWithValidData() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aValidDocument();

        assertNotNull(details);
        assertEquals("Resultados_analisis.pdf", details.getDocumentName());
        assertEquals("Análisis de sangre", details.getDocumentType());
        assertEquals("https://storage.datavet.com/docs/analisis.pdf", details.getFileUrl());
        assertEquals("application/pdf", details.getMimeType());
        assertNotNull(details.getUploadedAt());
        assertEquals("vet-id-789", details.getUploadedBy());
        assertEquals("Resultados del hemograma completo", details.getDescription());
        assertNull(details.getFileSizeInBytes());
        assertFalse(details.isConfidential());
        assertNull(details.getChecksum());
    }

    @Test
    @DisplayName("Should create a valid confidential document with description")
    void create_shouldCreateConfidentialDocumentWithDescription() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aValidConfidentialDocument();

        assertTrue(details.isConfidential());
        assertNotNull(details.getDescription());
        assertFalse(details.getDescription().isBlank());
    }

    @Test
    @DisplayName("Should create DocumentDetails with fileSizeInBytes and checksum populated")
    void create_shouldCreateDocumentWithSizeAndChecksum() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aValidDocumentWithSizeAndChecksum();

        assertEquals(204800L, details.getFileSizeInBytes());
        assertEquals("abc123def456", details.getChecksum());
    }

    @Test
    @DisplayName("Should return MedicalRecordType.DOCUMENT")
    void getType_shouldReturnDocumentType() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aValidDocument();

        assertEquals(MedicalRecordType.DOCUMENT, details.getType());
    }

    @Test
    @DisplayName("Should allow description to be null when not confidential")
    void create_shouldAllowNullDescriptionWhenNotConfidential() {
        DocumentDetails details = DocumentDetailsTestDataBuilder
                .aDocumentWithConfidential(false, null);

        assertNotNull(details);
        assertNull(details.getDescription());
    }

    @Test
    @DisplayName("Should allow fileSizeInBytes to be null (optional field)")
    void create_shouldAllowNullFileSize() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aDocumentWithFileSize(null);

        assertNotNull(details);
        assertNull(details.getFileSizeInBytes());
    }

    @Test
    @DisplayName("Should allow checksum to be null (optional field)")
    void create_shouldAllowNullChecksum() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aDocumentWithChecksum(null);

        assertNotNull(details);
        assertNull(details.getChecksum());
    }

    @Test
    @DisplayName("Should allow uploadedAt = exactly now (boundary)")
    void create_shouldAllowUploadedAtNow() {
        assertDoesNotThrow(() ->
                DocumentDetailsTestDataBuilder.aDocumentWithUploadedAt(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should allow fileSizeInBytes of exactly 1 byte (minimum valid size)")
    void create_shouldAllowFileSizeOfOneByteAsMinimum() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aDocumentWithFileSize(1L);

        assertEquals(1L, details.getFileSizeInBytes());
    }

    // ================================================================
    // validate() — documentName
    // ================================================================

    @Test
    @DisplayName("Should throw when documentName is null")
    void create_shouldFailWhenDocumentNameIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithName(null));

        assertTrue(ex.getMessage().contains("Document - name"));
    }

    @Test
    @DisplayName("Should throw when documentName is blank")
    void create_shouldFailWhenDocumentNameIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithName("   "));

        assertTrue(ex.getMessage().contains("Document - name"));
    }

    // ================================================================
    // validate() — documentType
    // ================================================================

    @Test
    @DisplayName("Should throw when documentType is null")
    void create_shouldFailWhenDocumentTypeIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithType(null));

        assertTrue(ex.getMessage().contains("Document - type"));
    }

    @Test
    @DisplayName("Should throw when documentType is blank")
    void create_shouldFailWhenDocumentTypeIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithType("   "));

        assertTrue(ex.getMessage().contains("Document - type"));
    }

    // ================================================================
    // validate() — fileUrl
    // ================================================================

    @Test
    @DisplayName("Should throw when fileUrl is null")
    void create_shouldFailWhenFileUrlIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithFileUrl(null));

        assertTrue(ex.getMessage().contains("Document - FileUrl"));
    }

    @Test
    @DisplayName("Should throw when fileUrl is blank")
    void create_shouldFailWhenFileUrlIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithFileUrl("   "));

        assertTrue(ex.getMessage().contains("Document - FileUrl"));
    }

    // ================================================================
    // validate() — mimeType
    // ================================================================

    @Test
    @DisplayName("Should throw when mimeType is null")
    void create_shouldFailWhenMimeTypeIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithMimeType(null));

        assertTrue(ex.getMessage().contains("Document - mimeType"));
    }

    @Test
    @DisplayName("Should throw when mimeType is blank")
    void create_shouldFailWhenMimeTypeIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithMimeType("   "));

        assertTrue(ex.getMessage().contains("Document - mimeType"));
    }

    // ================================================================
    // validate() — uploadedAt
    // ================================================================

    @Test
    @DisplayName("Should throw when uploadedAt is null")
    void create_shouldFailWhenUploadedAtIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithUploadedAt(null));

        assertTrue(ex.getMessage().contains("Document - uploadedAt"));
    }

    @Test
    @DisplayName("Should throw when uploadedAt is in the future")
    void create_shouldFailWhenUploadedAtIsInFuture() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithUploadedAt(
                        LocalDateTime.now().plusHours(1)));

        assertTrue(ex.getMessage().contains("Document - uploadedAt"));
    }

    // ================================================================
    // validate() — uploadedBy
    // ================================================================

    @Test
    @DisplayName("Should throw when uploadedBy is null")
    void create_shouldFailWhenUploadedByIsNull() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithUploadedBy(null));

        assertTrue(ex.getMessage().contains("Document - uploadedBy"));
    }

    @Test
    @DisplayName("Should throw when uploadedBy is blank")
    void create_shouldFailWhenUploadedByIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithUploadedBy("   "));

        assertTrue(ex.getMessage().contains("Document - uploadedBy"));
    }

    // ================================================================
    // validate() — fileSizeInBytes
    // ================================================================

    @Test
    @DisplayName("Should throw when fileSizeInBytes is 0")
    void create_shouldFailWhenFileSizeIsZero() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithFileSize(0L));

        assertTrue(ex.getMessage().contains("Document - fileSizeInBytes"));
    }

    @Test
    @DisplayName("Should throw when fileSizeInBytes is negative")
    void create_shouldFailWhenFileSizeIsNegative() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithFileSize(-1L));

        assertTrue(ex.getMessage().contains("Document - fileSizeInBytes"));
    }

    // ================================================================
    // validate() — confidential + description
    // ================================================================

    @Test
    @DisplayName("Should throw when confidential=true but description is null")
    void create_shouldFailWhenConfidentialWithNullDescription() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithConfidential(true, null));

        assertTrue(ex.getMessage().contains("Document - confidential, description"));
    }

    @Test
    @DisplayName("Should throw when confidential=true but description is blank")
    void create_shouldFailWhenConfidentialWithBlankDescription() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithConfidential(true, "   "));

        assertTrue(ex.getMessage().contains("Document - confidential, description"));
    }

    @Test
    @DisplayName("Valid: confidential=false and description=null should not throw")
    void create_shouldNotThrowWhenNotConfidentialAndNoDescription() {
        assertDoesNotThrow(() ->
                DocumentDetailsTestDataBuilder.aDocumentWithConfidential(false, null));
    }

    @Test
    @DisplayName("Valid: confidential=true and description provided should not throw")
    void create_shouldNotThrowWhenConfidentialWithValidDescription() {
        assertDoesNotThrow(() ->
                DocumentDetailsTestDataBuilder.aDocumentWithConfidential(
                        true, "Expediente con historial completo del paciente"));
    }

    // ================================================================
    // validate() — checksum
    // ================================================================

    @Test
    @DisplayName("Should throw when checksum is blank (not null but empty string)")
    void create_shouldFailWhenChecksumIsBlank() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetailsTestDataBuilder.aDocumentWithChecksum("   "));

        assertTrue(ex.getMessage().contains("Document - checksum"));
    }

    @Test
    @DisplayName("Valid: checksum=null should not throw (optional field)")
    void create_shouldNotThrowWhenChecksumIsNull() {
        assertDoesNotThrow(() ->
                DocumentDetailsTestDataBuilder.aDocumentWithChecksum(null));
    }

    // ================================================================
    // validate() — error accumulation
    // ================================================================

    @Test
    @DisplayName("Should accumulate multiple validation errors")
    void create_shouldAccumulateMultipleValidationErrors() {
        MedicalRecordValidationException ex = assertThrows(MedicalRecordValidationException.class,
                () -> DocumentDetails.create(
                        null,                              // documentName null
                        null,                              // documentType null
                        null,                              // fileUrl null
                        null,                              // mimeType null
                        null,                              // uploadedAt null
                        null,                              // uploadedBy null
                        null,                              // description null
                        -1L,                               // fileSizeInBytes negative
                        true,                              // confidential=true → needs description
                        "   "                              // checksum blank
                ));

        String msg = ex.getMessage();
        assertTrue(msg.contains("Document - name"));
        assertTrue(msg.contains("Document - type"));
        assertTrue(msg.contains("Document - FileUrl"));
        assertTrue(msg.contains("Document - mimeType"));
        assertTrue(msg.contains("Document - uploadedAt"));
        assertTrue(msg.contains("Document - uploadedBy"));
        assertTrue(msg.contains("Document - fileSizeInBytes"));
        assertTrue(msg.contains("Document - confidential, description"));
        assertTrue(msg.contains("Document - checksum"));
    }

    // ================================================================
    // canCorrect() — always returns false
    // ================================================================

    @Test
    @DisplayName("canCorrect should always return false (documents are immutable by design)")
    void canCorrect_shouldAlwaysReturnFalse() {
        DocumentDetails correction = DocumentDetailsTestDataBuilder.aValidDocument();
        DocumentDetails original   = DocumentDetailsTestDataBuilder.aValidDocument();

        assertFalse(correction.canCorrect(original));
    }

    @Test
    @DisplayName("canCorrect should return false even when compared against a different type")
    void canCorrect_shouldReturnFalseEvenWithDifferentType() {
        DocumentDetails correction = DocumentDetailsTestDataBuilder.aValidDocument();
        WeightDetails wrongType    = WeightDetails.create(10.0, WeightUnit.KG);

        // DocumentDetails.canCorrect always returns false — no exception, no true
        assertFalse(correction.canCorrect(wrongType));
    }

    // ================================================================
    // applyAction() — not supported
    // ================================================================

    @Test
    @DisplayName("applyAction should throw since DocumentDetails does not support state changes")
    void applyAction_shouldThrowForAnyAction() {
        DocumentDetails details = DocumentDetailsTestDataBuilder.aValidDocument();

        assertThrows(RuntimeException.class,
                () -> details.applyAction(RecordAction.ADMIT));
    }
}