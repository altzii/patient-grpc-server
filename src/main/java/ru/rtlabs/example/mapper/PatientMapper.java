package ru.rtlabs.example.mapper;

import org.springframework.stereotype.Component;
import ru.rtlabs.example.protobuf.CreatePatientRequest;
import ru.rtlabs.example.protobuf.Patient;

@Component
public class PatientMapper {

    public Patient patientRequestToPatient(CreatePatientRequest createPatientRequest) {
        return Patient.newBuilder()
                      .setFirstName(createPatientRequest.getFirstName())
                      .setLastName(createPatientRequest.getLastName())
                      .setPatronymicName(createPatientRequest.getPatronymicName())
                      .setDoc(createPatientRequest.getDoc())
                      .setBirthdate(createPatientRequest.getBirthdate())
                      .build();
    }

}
