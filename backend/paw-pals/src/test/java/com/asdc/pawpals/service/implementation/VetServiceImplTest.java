package com.asdc.pawpals.service.implementation;

import com.asdc.pawpals.Enums.AppointmentStatus;
import com.asdc.pawpals.dto.VetAvailabilityDto;
import com.asdc.pawpals.dto.VetDto;
import com.asdc.pawpals.dto.VetScheduleDto;
import com.asdc.pawpals.model.*;
import com.asdc.pawpals.repository.AppointmentRepository;
import com.asdc.pawpals.repository.UserRepository;
import com.asdc.pawpals.repository.VetAvailabilityRepository;
import com.asdc.pawpals.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class VetServiceImplTest {
    @Autowired
    VetServiceImpl vetService;

    VetRepository vetRepoMock;
    UserRepository userRepoMock;
    VetAvailabilityRepository vetAvailabilityRepoMock;
    AppointmentRepository aptRepoMock;

    MailServiceImpl mailServiceMock;

    @BeforeEach
    public void setup() {
        vetRepoMock = mock(VetRepository.class);
        vetService.vetRepository = vetRepoMock;

        userRepoMock = mock(UserRepository.class);
        vetService.userRepository = userRepoMock;

        vetAvailabilityRepoMock = mock(VetAvailabilityRepository.class);
        vetService.vetAvailabilityRepository = vetAvailabilityRepoMock;

        aptRepoMock = mock(AppointmentRepository.class);
        vetService.appointmentRepository = aptRepoMock;

        mailServiceMock = mock(MailServiceImpl.class);
        vetService.mailService = mailServiceMock;

    }

    @Test
    public void objectCreated() {
        assertNotNull(vetRepoMock);
    }

    @Test
    public void registerVet_shouldReturnTrueWhenVetIsRegistered() {
        //Arrange
        VetDto vetToRegister = new VetDto();
        Vet vet = new Vet();
        when(vetRepoMock.save(any(Vet.class))).thenReturn(vet);
        when(vetRepoMock.count()).thenReturn(1L).thenReturn(2L);
        when(userRepoMock.findById(anyString())).thenReturn(Optional.of(new User()));
        doNothing().when(mailServiceMock).sendMail(eq(""), eq(""), eq(""));

        vetToRegister.setUserName("jDoe");

        //Act
        Boolean saved = vetService.registerVet(vetToRegister);
        //Assert
        assertTrue(saved);
    }

    @Test
    public void registerVet_shouldReturnFalseWhenVetIsNotRegistered() {
        //Arrange
        VetDto vetToRegister = new VetDto();
        Vet vet = new Vet();
        when(vetRepoMock.save(any(Vet.class))).thenReturn(vet);
        when(vetRepoMock.count()).thenReturn(1L).thenReturn(1L);
        when(userRepoMock.findById(anyString())).thenReturn(Optional.of(new User()));
        vetToRegister.setUserName("jDoe");

        //Act
        Boolean saved = vetService.registerVet(vetToRegister);

        //Assert
        assertFalse(saved);
    }

    @Test
    public void registerVet_shouldThrowUserNotFoundException() {
        //Arrange
        VetDto vetToRegister = new VetDto();
        Vet vet = new Vet();
        when(vetRepoMock.save(any(Vet.class))).thenReturn(vet);
        when(vetRepoMock.count()).thenReturn(1L).thenReturn(1L);
        when(userRepoMock.findById(anyString())).thenReturn(Optional.empty());
        vetToRegister.setUserName("jDoe");

        //Act + Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> vetService.registerVet(vetToRegister));

        //Assert 2
        assertEquals("Please provide a valid username", exception.getMessage());
    }

    @Test
    public void getVetAvailability_shouldReturnVetAvailability() {
        //Arrange
        String userId = "jDoe";
        List<VetAvailability> vetAvailability = new ArrayList<>();
        VetAvailability avl = new VetAvailability();
        avl.setId(1);
        avl.setDayOfWeek("Monday");
        avl.setStartTime("10:00");
        avl.setEndTime("17:00");
        vetAvailability.add(avl);
        when(vetAvailabilityRepoMock.findByVet_User_UserId(anyString())).thenReturn(vetAvailability);

        //Act
        List<VetAvailabilityDto> actual = vetService.getVetAvailability(userId);

        //Assert
        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getAvailabilityId());
    }

    @Test
    public void getVetAvailability_shouldReturnVetAvailabilityForVetId() {
        //Arrange
        Long vetId = 1L;
        List<VetAvailability> vetAvailability = new ArrayList<>();
        VetAvailability avl = new VetAvailability();
        avl.setId(1);
        avl.setDayOfWeek("Monday");
        avl.setStartTime("10:00");
        avl.setEndTime("17:00");
        vetAvailability.add(avl);
        when(vetAvailabilityRepoMock.findByVetId(anyLong())).thenReturn(vetAvailability);

        //Act
        List<VetAvailabilityDto> actual = vetService.getVetAvailability(vetId);

        //Assert
        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getAvailabilityId());
    }

    @Test
    public void getVetAvailabilityOnSpecificDay_shouldReturnVetAvailabilityForUserId() {
        //Arrange
        String userId = "jDoe";
        List<VetAvailability> vetAvailability = new ArrayList<>();
        VetAvailability avl = new VetAvailability();
        avl.setId(1);
        avl.setDayOfWeek("Wednesday");
        avl.setStartTime("10:00");
        avl.setEndTime("17:00");
        vetAvailability.add(avl);
        when(vetAvailabilityRepoMock.findByVet_User_UserId(anyString())).thenReturn(vetAvailability);

        List<Appointment> appointments = new ArrayList<>();
        Appointment appointment = new Appointment();
        appointment.setId(1);
        appointment.setAnimal(new Animal());
        appointment.setVet(new Vet());
        appointment.setDate("15-03-2023");
        appointment.setStartTime("10:00");
        appointment.setEndTime("10:30");
        appointment.setStatus(AppointmentStatus.CONFIRMED.getLabel());
        appointments.add(appointment);
        when(aptRepoMock.findByVet_User_UserId(anyString())).thenReturn(appointments);

        //Act
        VetAvailabilityDto availability = vetService.getVetAvailabilityOnSpecificDay(userId, "15-03-2023");

        //Assert
        assertNotNull(availability);
        assertEquals(13, availability.getSlots().size());

    }

    @Test
    public void getVetAvailabilityOnSpecificDay_shouldReturnVetAvailabilityForVetId() {
        //Arrange
        Long vetId = 1L;
        List<VetAvailability> vetAvailability = new ArrayList<>();
        VetAvailability avl = new VetAvailability();
        avl.setId(1);
        avl.setDayOfWeek("Wednesday");
        avl.setStartTime("10:00");
        avl.setEndTime("17:00");
        vetAvailability.add(avl);
        when(vetAvailabilityRepoMock.findByVetId(anyLong())).thenReturn(vetAvailability);

        List<Appointment> appointments = new ArrayList<>();
        Appointment appointment = new Appointment();
        appointment.setId(1);
        appointment.setAnimal(new Animal());
        appointment.setVet(new Vet());
        appointment.setDate("15-03-2023");
        appointment.setStartTime("10:00");
        appointment.setEndTime("10:30");
        appointment.setStatus(AppointmentStatus.CONFIRMED.getLabel());
        appointments.add(appointment);
        when(aptRepoMock.findByVetId(anyLong())).thenReturn(appointments);

        //Act
        VetAvailabilityDto availability = vetService.getVetAvailabilityOnSpecificDay(vetId, "15-03-2023");

        //Assert
        assertNotNull(availability);
        assertEquals(13, availability.getSlots().size());

    }

    @Test
    public void getVetScheduleOnSpecificDay_shouldReturnScheduleForUserId() {
        //Arrange
        String userId = "jDoe";

        List<Appointment> appointments = new ArrayList<>();
        Appointment appointment = new Appointment();
        appointment.setId(1);
        appointment.setAnimal(new Animal());
        appointment.setVet(new Vet());
        appointment.setDate("15-03-2023");
        appointment.setStartTime("10:00");
        appointment.setEndTime("10:30");
        appointment.setStatus(AppointmentStatus.CONFIRMED.getLabel());
        appointments.add(appointment);
        when(aptRepoMock.findByVet_User_UserId(anyString())).thenReturn(appointments);

        //Act
        VetScheduleDto schedule = vetService.getVetScheduleOnSpecificDay(userId, "15-03-2023");

        //Assert
        assertNotNull(schedule);
        assertEquals(1, schedule.getSlotsBooked().size());
    }

}
