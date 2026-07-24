package com.optimize.land.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.optimize.common.securities.models.User;
import com.optimize.common.securities.security.services.UserService;
import com.optimize.land.jms.AfisProducer;
import com.optimize.land.model.dto.ActorDto;
import com.optimize.land.model.dto.PersonDto;
import com.optimize.land.model.dto.v2.ActorDtoV2;
import com.optimize.land.model.dto.v2.FingerprintStoreV2Dto;
import com.optimize.land.model.entity.FingerprintStore;
import com.optimize.land.model.entity.Person;
import com.optimize.land.model.entity.Registration;
import com.optimize.land.model.enumeration.ActorType;
import com.optimize.land.model.enumeration.Finger;
import com.optimize.land.model.enumeration.HandType;
import com.optimize.land.model.enumeration.RoleActor;
import com.optimize.land.model.mapper.ActorMapper;
import com.optimize.land.repository.ActorRepository;
import com.optimize.land.repository.FingerprintStoreRepository;
import com.optimize.land.repository.InformalGroupRepository;
import com.optimize.land.repository.OutboxEventRepository;
import com.optimize.land.repository.PersonRepository;
import com.optimize.land.repository.PrivateLegalEntityRepository;
import com.optimize.land.repository.PublicLegalEntityRepository;
import com.optimize.land.repository.SynchroHistoryRepository;
import com.optimize.land.config.storage.MinioProperties;
import com.optimize.land.client.AfisClient;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ActorServiceRegisterV2Test {

    @Mock
    private ActorRepository actorRepository;
    @Mock
    private ActorMapper actorMapper;
    @Mock
    private FingerprintStoreService fingerprintStoreService;
    @Mock
    private FingerprintStoreRepository fingerprintStoreRepository;
    @Mock
    private SynchroHistoryService synchroHistoryService;
    @Mock
    private SynchroHistoryRepository synchroHistoryRepository;
    @Mock
    private AfisProducer afisProducer;
    @Mock
    private AfisClient afisClient;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private InformalGroupRepository informalGroupRepository;
    @Mock
    private PrivateLegalEntityRepository privateLegalEntityRepository;
    @Mock
    private PublicLegalEntityRepository publicLegalEntityRepository;
    @Mock
    private UserService userService;
    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private MinioProperties minioProperties;
    @Mock
    private ActorMultipartAssembler actorMultipartAssembler;

    @InjectMocks
    private ActorService actorService;

    @BeforeEach
    void setUp() {
        actorService.setUserService(userService);
        when(synchroHistoryService.getRepository()).thenReturn(synchroHistoryRepository);
        when(fingerprintStoreService.getRepository()).thenReturn(fingerprintStoreRepository);
        when(minioProperties.isEnabled()).thenReturn(false);
        when(synchroHistoryRepository.existsByBatchNumberAndPacketsNumberContains(any(), any())).thenReturn(false);
        when(userService.getCurrentUser()).thenReturn(
            new User("Test", "User", "MALE", "test@test.com", "12345678", "agent", "password")
        );
        when(
            personRepository.existsByLastnameAndFirstnameAndSexAndMaritalStatusAndBirthDateAndPlaceOfBirthAndNationalityAndProfessionAndAddressAndPrimaryPhoneAndEmail(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
            )
        ).thenReturn(false);
    }

    @Test
    void registerV2_usesAssemblerAndPersistsFingerprints() throws Exception {
        ActorDtoV2 actorDtoV2 = buildActorDtoV2();
        MockMultipartFile file = new MockMultipartFile(
            "fingerprints",
            "thumb.jpg",
            "image/jpeg",
            "image-bytes".getBytes(StandardCharsets.UTF_8)
        );
        Set<FingerprintStore> fingerprintStores = new HashSet<>();
        FingerprintStore store = new FingerprintStore();
        store.setFingerprintImage("image-bytes".getBytes(StandardCharsets.UTF_8));
        fingerprintStores.add(store);

        Registration registration = new Registration();
        registration.setType(ActorType.PHYSICAL_PERSON);
        registration.setFingerprintStores(fingerprintStores);
        Person person = new Person();
        person.setFirstname("Jane");
        person.setLastname("Doe");
        registration.setPhysicalPerson(person);

        when(actorMultipartAssembler.assembleFingerprintStores(actorDtoV2, List.of(file))).thenReturn(fingerprintStores);
        when(actorMapper.toRegistration(any(ActorDto.class))).thenReturn(registration);
        when(actorRepository.save(any(Registration.class))).thenAnswer(invocation -> {
            Registration saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        String response = actorService.registerV2(actorDtoV2, List.of(file));

        verify(actorMultipartAssembler).assembleFingerprintStores(actorDtoV2, List.of(file));
        verify(fingerprintStoreRepository).saveAll(fingerprintStores);
        verify(afisProducer).sendMatchingRequest(any());

        ArgumentCaptor<Registration> registrationCaptor = ArgumentCaptor.forClass(Registration.class);
        verify(actorRepository).save(registrationCaptor.capture());
        assertEquals(fingerprintStores, registrationCaptor.getValue().getFingerprintStores());
        assertTrue(response.contains("rid"));
    }

    private ActorDtoV2 buildActorDtoV2() {
        ActorDtoV2 actorDtoV2 = new ActorDtoV2();
        actorDtoV2.setSynchroBatchNumber("BATCH-V2");
        actorDtoV2.setSynchroPacketNumber("1");
        actorDtoV2.setRole(RoleActor.OWNER_OR_REPRESENTATIVE);
        actorDtoV2.setType(ActorType.PHYSICAL_PERSON);

        PersonDto personDto = new PersonDto();
        personDto.setFirstname("Jane");
        personDto.setLastname("Doe");
        personDto.setSex(com.optimize.land.model.enumeration.Sex.FEMININ);
        personDto.setMaritalStatus(com.optimize.land.model.enumeration.MaritalStatus.CELIBATAIRE);
        personDto.setBirthDate(java.time.LocalDate.of(1990, 1, 1));
        personDto.setPlaceOfBirth("City");
        personDto.setNationality("Nationality");
        personDto.setProfession("Engineer");
        personDto.setAddress("Address");
        personDto.setPrimaryPhone("12345678");
        personDto.setEmail("jane@example.com");
        actorDtoV2.setPhysicalPerson(personDto);

        FingerprintStoreV2Dto meta = new FingerprintStoreV2Dto();
        meta.setFingerStr("Pouce Gauche");
        meta.setHandType(HandType.LEFT);
        meta.setFingerName(Finger.THUMB);
        actorDtoV2.setFingerprintStores(Set.of(meta));
        return actorDtoV2;
    }
}
