package com.datavet.datavet.pet.testutil.medicalrecord;

import com.datavet.datavet.pet.domain.model.details.document.DocumentDetails;

import java.time.LocalDateTime;

/**
 * Test data builder for DocumentDetails.
 *
 * Default: confidential=false, checksum=null, fileSizeInBytes=null
 * because those are the simplest valid combinations.
 *
 * Use dedicated helpers to exercise the confidential branch
 * and the optional fileSizeInBytes / checksum fields.
 */
public class DocumentDetailsTestDataBuilder {

    // --- Defaults ---
    private static final String        DEFAULT_DOCUMENT_NAME    = "Resultados_analisis.pdf";
    private static final String        DEFAULT_DOCUMENT_TYPE    = "Análisis de sangre";
    private static final String        DEFAULT_FILE_URL         = "https://storage.datavet.com/docs/analisis.pdf";
    private static final String        DEFAULT_MIME_TYPE        = "application/pdf";
    private static final LocalDateTime DEFAULT_UPLOADED_AT      = LocalDateTime.now().minusHours(1);
    private static final String        DEFAULT_UPLOADED_BY      = "vet-id-789";
    private static final String        DEFAULT_DESCRIPTION      = "Resultados del hemograma completo";
    private static final Long          DEFAULT_FILE_SIZE        = null;
    private static final boolean       DEFAULT_CONFIDENTIAL     = false;
    private static final String        DEFAULT_CHECKSUM         = null;

    // ----------------------------------------------------------------
    // Happy path
    // ----------------------------------------------------------------

    /** Valid document — not confidential, no file size, no checksum (simplest valid case). */
    public static DocumentDetails aValidDocument() {
        return DocumentDetails.create(
                DEFAULT_DOCUMENT_NAME,
                DEFAULT_DOCUMENT_TYPE,
                DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE,
                DEFAULT_UPLOADED_AT,
                DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION,
                DEFAULT_FILE_SIZE,
                DEFAULT_CONFIDENTIAL,
                DEFAULT_CHECKSUM
        );
    }

    /** Valid confidential document — requires a non-blank description. */
    public static DocumentDetails aValidConfidentialDocument() {
        return DocumentDetails.create(
                DEFAULT_DOCUMENT_NAME,
                DEFAULT_DOCUMENT_TYPE,
                DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE,
                DEFAULT_UPLOADED_AT,
                DEFAULT_UPLOADED_BY,
                "Documento de acceso restringido",
                DEFAULT_FILE_SIZE,
                true,
                DEFAULT_CHECKSUM
        );
    }

    /** Valid document with file size and checksum populated. */
    public static DocumentDetails aValidDocumentWithSizeAndChecksum() {
        return DocumentDetails.create(
                DEFAULT_DOCUMENT_NAME,
                DEFAULT_DOCUMENT_TYPE,
                DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE,
                DEFAULT_UPLOADED_AT,
                DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION,
                204800L,
                DEFAULT_CONFIDENTIAL,
                "abc123def456"
        );
    }

    // ----------------------------------------------------------------
    // Field-specific variants
    // ----------------------------------------------------------------

    public static DocumentDetails aDocumentWithName(String name) {
        return DocumentDetails.create(name, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithType(String type) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, type, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithFileUrl(String fileUrl) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, fileUrl,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithMimeType(String mimeType) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                mimeType, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithUploadedAt(LocalDateTime uploadedAt) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, uploadedAt, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithUploadedBy(String uploadedBy) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, uploadedBy,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithFileSize(Long fileSizeInBytes) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, fileSizeInBytes, DEFAULT_CONFIDENTIAL, DEFAULT_CHECKSUM);
    }

    public static DocumentDetails aDocumentWithChecksum(String checksum) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                DEFAULT_DESCRIPTION, DEFAULT_FILE_SIZE, DEFAULT_CONFIDENTIAL, checksum);
    }

    public static DocumentDetails aDocumentWithConfidential(boolean confidential, String description) {
        return DocumentDetails.create(DEFAULT_DOCUMENT_NAME, DEFAULT_DOCUMENT_TYPE, DEFAULT_FILE_URL,
                DEFAULT_MIME_TYPE, DEFAULT_UPLOADED_AT, DEFAULT_UPLOADED_BY,
                description, DEFAULT_FILE_SIZE, confidential, DEFAULT_CHECKSUM);
    }
}