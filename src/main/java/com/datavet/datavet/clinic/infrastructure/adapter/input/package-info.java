/**
 * Clinic Infrastructure Input Adapters Package
 * 
 * This package contains input adapters for the Clinic domain.
 * Input adapters handle incoming requests from external systems such as
 * REST controllers, message consumers, and scheduled tasks.
 * 
 * Key principles:
 * - Input adapters should handle protocol-specific concerns (HTTP, messaging, etc.)
 * - Adapters should validate input and convert to domain commands/queries
 * - Adapters should handle authentication and authorization
 * - Adapters should provide appropriate error responses for the protocol
 * 
 * @author DataVet Development Team
 * @version 1.0
 * @since 1.0
 */
package com.datavet.datavet.clinic.infrastructure.adapter.input;