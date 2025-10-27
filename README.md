# DataVet - Veterinary Management System

## Overview

DataVet is a comprehensive veterinary management system built with Spring Boot, following Domain-Driven Design (DDD) principles and hexagonal architecture. The system is organized by business domains to support scalable development and clear separation of concerns.

## Architecture

### Domain-Driven Design

The system is organized into business domains, each following hexagonal architecture principles:

```
com.datavet.datavet/
├── shared/                    # Shared components across domains
├── clinic/                   # Clinic Management Domain (Core)
├── pet/                      # Pet Management Domain (Core) - Future
├── appointment/              # Appointment Scheduling Domain (Core) - Future
├── medical/                  # Medical Records Domain (Core) - Future
├── billing/                  # Billing & Payments Domain (Core) - Future
├── notification/             # Notification Domain (Support) - Future
└── user/                     # User Management Domain (Support) - Future
```

### Domain Types

**Core Domains** (Independent business capabilities):
- **Clinic**: Clinic management and configuration ✅ Implemented
- **Pet**: Pet registration and profile management 🔄 Planned
- **Appointment**: Scheduling and appointment management 🔄 Planned
- **Medical**: Medical records and treatment history 🔄 Planned
- **Billing**: Invoicing, payments, and financial tracking 🔄 Planned

**Supporting Domains** (Enable core domains):
- **Notification**: Email, SMS, and push notifications 🔄 Planned
- **User**: Authentication, authorization, and user management 🔄 Planned

### Hexagonal Architecture per Domain

Each domain follows the same internal structure:

```
{domain}/
├── domain/
│   ├── model/               # Domain entities and value objects
│   ├── exception/           # Domain-specific exceptions
│   └── service/            # Domain services (business logic)
├── application/
│   ├── port/
│   │   ├── in/             # Use case interfaces
│   │   └── out/            # Repository interfaces
│   ├── service/            # Application services (orchestration)
│   ├── dto/                # Data transfer objects
│   └── mapper/             # Entity-DTO mappers
└── infrastructure/
    ├── adapter/
    │   ├── input/          # Controllers, REST endpoints
    │   └── output/         # Repository implementations
    ├── persistence/
    │   ├── entity/         # JPA entities
    │   └── repository/     # JPA repositories
    └── config/             # Domain-specific configuration
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Database (H2 for development, configurable for production)

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/swagger-ui.html` (if configured)
- Actuator endpoints: `http://localhost:8080/actuator`

## Current Features

### Clinic Management
- Create new clinics
- Update clinic information
- Retrieve clinic details
- List all clinics

**API Endpoints:**
- `POST /api/v1/clinics` - Create a new clinic
- `GET /api/v1/clinics/{id}` - Get clinic by ID
- `PUT /api/v1/clinics/{id}` - Update clinic
- `GET /api/v1/clinics` - List all clinics

## Development Guidelines

### Adding New Domains

For detailed guidelines on adding new domains, see [Domain Development Guidelines](docs/DOMAIN_DEVELOPMENT_GUIDELINES.md).

### Key Principles

1. **Domain Independence**: Core domains should not depend on each other
2. **Hexagonal Architecture**: Each domain follows the same architectural pattern
3. **Shared Components**: Common functionality is extracted to shared packages
4. **Cross-Domain Communication**: Domains communicate through IDs and events, not direct references

### Testing Strategy

The project includes comprehensive testing at multiple levels:

- **Unit Tests**: Test domain logic and application services in isolation
- **Integration Tests**: Test infrastructure adapters and external integrations
- **Boundary Tests**: Verify domain isolation and architectural constraints
- **End-to-End Tests**: Validate complete business workflows

Run tests with:
```bash
mvn test
```

### Code Quality

The project maintains high code quality through:
- Comprehensive test coverage
- Architectural boundary enforcement
- Domain-driven design principles
- Clean code practices

## Project Structure

```
src/
├── main/java/com/datavet/datavet/
│   ├── shared/                    # Shared components
│   │   ├── domain/               # Common domain concepts
│   │   ├── application/          # Shared application services
│   │   └── infrastructure/       # Common infrastructure
│   └── clinic/                   # Clinic domain implementation
│       ├── domain/
│       ├── application/
│       └── infrastructure/
├── test/java/                    # Test structure mirrors main
└── resources/
    └── application.properties    # Configuration
```

## Configuration

### Database Configuration

The application uses H2 database by default for development. For production, update `application.properties`:

```properties
# H2 Database (Development)
spring.datasource.url=jdbc:h2:mem:datavet
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Production Database Example (PostgreSQL)
# spring.datasource.url=jdbc:postgresql://localhost:5432/datavet
# spring.datasource.username=datavet_user
# spring.datasource.password=datavet_password
# spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Application Properties

Key configuration properties:

```properties
# Server Configuration
server.port=8080

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
```

## Migration History

### Project Restructure (Completed)

The project was successfully migrated from a single-domain structure to a multi-domain architecture:

**Migration Highlights:**
- ✅ Created shared components foundation
- ✅ Established domain-driven package structure
- ✅ Migrated existing Clinic functionality to new domain structure
- ✅ Updated all imports and references
- ✅ Maintained backward compatibility
- ✅ Created comprehensive test coverage
- ✅ Established development guidelines

**Lessons Learned:**
1. **Gradual Migration**: Moving one domain at a time minimized risk and allowed for iterative improvements
2. **Test-First Approach**: Comprehensive tests ensured no functionality was lost during migration
3. **Clear Boundaries**: Establishing domain boundaries early prevented architectural drift
4. **Documentation**: Detailed guidelines ensure consistent future development

## Contributing

### Development Workflow

1. Follow the [Domain Development Guidelines](docs/DOMAIN_DEVELOPMENT_GUIDELINES.md)
2. Create feature branches from `main`
3. Ensure all tests pass before submitting PRs
4. Follow established naming conventions and package structure
5. Update documentation for new features

### Code Standards

- Follow Java coding conventions
- Use meaningful names for classes, methods, and variables
- Write comprehensive tests for new functionality
- Document complex business logic
- Maintain domain boundaries and architectural principles

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: H2 (development), PostgreSQL (production ready)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test
- **Architecture**: Hexagonal Architecture, Domain-Driven Design

## Monitoring and Observability

The application includes Spring Boot Actuator for monitoring:

- Health checks: `/actuator/health`
- Application info: `/actuator/info`
- Metrics: `/actuator/metrics`

## Future Roadmap

### Planned Domains

1. **Pet Management**: Pet registration, profiles, and basic information
2. **Appointment Scheduling**: Booking, scheduling, and calendar management
3. **Medical Records**: Treatment history, diagnoses, and medical documentation
4. **Billing System**: Invoicing, payments, and financial tracking
5. **User Management**: Authentication, authorization, and user profiles
6. **Notification System**: Email, SMS, and push notification capabilities

### Technical Improvements

- API documentation with OpenAPI/Swagger
- Event-driven architecture for cross-domain communication
- Caching layer for improved performance
- Advanced monitoring and logging
- CI/CD pipeline setup
- Docker containerization

## Support

For questions about the architecture or development guidelines, refer to:
- [Domain Development Guidelines](docs/DOMAIN_DEVELOPMENT_GUIDELINES.md)
- Project documentation in the `docs/` directory
- Code examples in the existing `clinic` domain

## License

This project is proprietary software for DataVet veterinary management system.