package apap.ta.rumahSehat.appointment.repository;

import apap.ta.rumahSehat.appointment.model.AppointmentModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentDb extends JpaRepository<AppointmentModel, String> {
    Optional<AppointmentModel> findByKodeAppointment(String kodeAppointment);
}
