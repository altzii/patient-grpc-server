package ru.rtlabs.example.grpc.server;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.rtlabs.example.mapper.PatientMapper;
import ru.rtlabs.example.protobuf.CreatePatientRequest;
import ru.rtlabs.example.protobuf.GetPatientByIdRequest;
import ru.rtlabs.example.protobuf.ListPatientsResponse;
import ru.rtlabs.example.protobuf.Patient;
import ru.rtlabs.example.protobuf.PatientServiceGrpc;
import ru.rtlabs.example.protobuf.SearchPatientsByDocRequest;
import ru.rtlabs.example.protobuf.SearchPatientsByNamesRequest;
import ru.rtlabs.example.repository.PatientRepository;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class PatientGrpcServiceImpl extends PatientServiceGrpc.PatientServiceImplBase {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    @Override
    public void listPatients(Empty request, StreamObserver<ListPatientsResponse> responseObserver) {
        ListPatientsResponse listPatients = ListPatientsResponse.newBuilder()
                                                                .addAllPatients(patientRepository.getAll())
                                                                .build();
        responseObserver.onNext(listPatients);
        responseObserver.onCompleted();
    }

    @Override
    public void getPatientById(GetPatientByIdRequest request, StreamObserver<Patient> responseObserver) {
        String patientId = request.getId();
        Patient patient = patientRepository.getById(patientId);
        if (patient != null) {
            responseObserver.onNext(patient);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription("Patient with id = " + patientId + " not found")
                                             .asRuntimeException()
            );
        }
    }

    @Override
    public void searchPatientsByDoc(
            SearchPatientsByDocRequest request,
            StreamObserver<ListPatientsResponse> responseObserver
    ) {
        ListPatientsResponse listPatients = ListPatientsResponse.newBuilder()
                                                                .addAllPatients(patientRepository.searchByDoc(request.getDoc()))
                                                                .build();
        responseObserver.onNext(listPatients);
        responseObserver.onCompleted();
    }

    @Override
    public void searchPatientsByNames(
            SearchPatientsByNamesRequest request,
            StreamObserver<ListPatientsResponse> responseObserver
    ) {
        List<Patient> patients = patientRepository.searchByNames(
                request.getFirstName(),
                request.getLastName(),
                request.getPatronymicName()
        );
        ListPatientsResponse listPatients = ListPatientsResponse.newBuilder()
                                                                .addAllPatients(patients)
                                                                .build();
        responseObserver.onNext(listPatients);
        responseObserver.onCompleted();
    }

    @Override
    public void createPatient(CreatePatientRequest request, StreamObserver<Patient> responseObserver) {
        Patient patient = patientRepository.createPatient(
                patientMapper.patientRequestToPatient(request)
        );
        responseObserver.onNext(patient);
        responseObserver.onCompleted();
    }

}
