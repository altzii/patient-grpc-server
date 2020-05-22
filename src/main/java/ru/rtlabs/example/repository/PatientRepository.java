package ru.rtlabs.example.repository;

import com.google.type.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import ru.rtlabs.example.protobuf.Patient;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class PatientRepository {

    private final List<Patient> patients = new CopyOnWriteArrayList<>();

    @PostConstruct
    private void init() {
        Patient aleksandr = Patient.newBuilder()
                                   .setId("0fec11a9-bc0e-420b-9aa7-47d9f44ecd0b")
                                   .setFirstName("Александр")
                                   .setLastName("Леонтьев")
                                   .setPatronymicName("Юрьевич")
                                   .setDoc("11111111")
                                   .setBirthdate(Date.newBuilder()
                                                     .setDay(4)
                                                     .setMonth(4)
                                                     .setYear(1996))
                                   .build();

        Patient petr = Patient.newBuilder()
                              .setId("55d42ac3-283d-443f-a764-3f63207cb430")
                              .setFirstName("Петр")
                              .setLastName("Петров")
                              .setPatronymicName("Петрович")
                              .setDoc("22222222")
                              .setBirthdate(Date.newBuilder()
                                                .setDay(1)
                                                .setMonth(1)
                                                .setYear(1960))
                              .build();

        patients.add(aleksandr);
        patients.add(petr);
    }

    public List<Patient> getAll() {
        return patients;
    }

    public Patient getById(String id) {
        return patients.stream()
                       .filter(patient -> id.equals(patient.getId()))
                       .findAny()
                       .orElse(null);
    }

    public List<Patient> searchByDoc(String doc) {
        return patients.stream()
                       .filter(patient -> StringUtils.containsIgnoreCase(patient.getDoc(), doc))
                       .collect(Collectors.toList());
    }

    public List<Patient> searchByNames(String firstName, String lastName, String patronymicName) {

        Predicate<Patient> searchByNamesPredicate = p -> Boolean.TRUE;

        if (!StringUtils.isEmpty(firstName)) {
            Predicate<Patient> firstNameSearchPredicate = p -> StringUtils.containsIgnoreCase(
                    p.getFirstName(),
                    firstName
            );
            searchByNamesPredicate = searchByNamesPredicate.and(firstNameSearchPredicate);
        }

        if (!StringUtils.isEmpty(lastName)) {
            Predicate<Patient> lastNameSearchPredicate = p -> StringUtils.containsIgnoreCase(
                    p.getLastName(),
                    lastName
            );
            searchByNamesPredicate = searchByNamesPredicate.and(lastNameSearchPredicate);
        }

        if (!StringUtils.isEmpty(patronymicName)) {
            Predicate<Patient> patronymicNameSearchPredicate = p -> StringUtils.containsIgnoreCase(
                    p.getPatronymicName(),
                    patronymicName
            );
            searchByNamesPredicate = searchByNamesPredicate.and(patronymicNameSearchPredicate);
        }

        return patients.stream()
                       .filter(searchByNamesPredicate)
                       .collect(Collectors.toList());
    }

    public Patient createPatient(Patient patient) {
        patient = patient.toBuilder()
                         .setId(UUID.randomUUID().toString())
                         .build();
        patients.add(patient);
        return patient;
    }

}
