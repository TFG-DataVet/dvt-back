package com.datavet.datavet.shared.infrastructure.config;

import com.datavet.datavet.pet.domain.model.details.allergy.AllergyDetails;
import com.datavet.datavet.pet.domain.model.details.consultation.ConsultationDetails;
import com.datavet.datavet.pet.domain.model.details.diagnosis.DiagnosisDetails;
import com.datavet.datavet.pet.domain.model.details.document.DocumentDetails;
import com.datavet.datavet.pet.domain.model.details.hospitalization.HospitalizationDetails;
import com.datavet.datavet.pet.domain.model.details.surgery.SurgeryDetails;
import com.datavet.datavet.pet.domain.model.details.treatment.TreatmentDetails;
import com.datavet.datavet.pet.domain.model.details.vaccine.VaccineDetails;
import com.datavet.datavet.pet.domain.model.details.weight.WeightDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * MongoDB configuration for the application.
 * Enables MongoDB auditing for automatic timestamp management.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Configures custom converters for MongoDB.
     * MongoDB automatically serializes Value Objects as embedded documents,
     * so custom converters are only needed for special serialization requirements.
     *
     * @return MongoCustomConversions with registered converters
     */

    @Bean
    public MongoCustomConversions customConversions() {
        List<Object> converters = new ArrayList<>();

        converters.add(new Converter<Date, LocalDateTime>() {
            @Override
            public LocalDateTime convert(Date source) {
                return source.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        });

        converters.add(new Converter<LocalDateTime, Date>() {
            @Override
            public Date convert(LocalDateTime source) {
                return Date.from(source.atZone(ZoneId.systemDefault()).toInstant());
            }
        });

        converters.add(new Converter<Date, LocalDate>() {
            @Override
            public LocalDate convert(Date source) {
                return source.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }
        });

        converters.add(new Converter<LocalDate, Date>() {
            @Override
            public Date convert(LocalDate source) {
                return Date.from(source.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        });

        return new MongoCustomConversions(converters);
    }

//    @Bean
//    public MongoCustomConversions customConversions() {
//        List<Object> converters = new ArrayList<>();
//
//        converters.add(new Converter<LocalDate, String>() {
//            @Override
//            public String convert(LocalDate source) {
//                return source.toString();
//            }
//        });
//
//        converters.add(new Converter<String, LocalDate>() {
//            @Override
//            public LocalDate convert(String source) {
//                return LocalDate.parse(source);
//            }
//        });
//
//        converters.add(new Converter<LocalDateTime, String>() {
//            @Override
//            public String convert(LocalDateTime source) {
//                return source.toString();
//            }
//        });
//
//        converters.add(new Converter<String, LocalDateTime>() {
//            @Override
//            public LocalDateTime convert(String source) {
//                return LocalDateTime.parse(source);
//            }
//        });
//
//        return new MongoCustomConversions(converters);
//    }
}