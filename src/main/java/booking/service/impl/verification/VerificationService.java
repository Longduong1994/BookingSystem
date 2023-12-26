package booking.service.impl.verification;

import booking.entity.User;
import booking.entity.Verification;
import booking.repository.VerificationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class VerificationService implements IVerificationService{
    private final VerificationRepository verificationRepository;

    @Override
    public Verification create(User user) {
        Verification verification = new Verification();
        verification.setUser(user);
        verification.setVerificationCode(UUID.randomUUID().toString().substring(0, 6));
        verification.setCreatAt(new Date());
        verificationRepository.save(verification);
        return verification;
    }

    @Override
    public List<Verification> getVerifications(User user) {
        return verificationRepository.findAll();
    }

    @Override
    public boolean isExpired(Date date) {
        Instant currentInstant = Instant.now();
        Instant targetInstant = date.toInstant();

        Duration duration = Duration.between(targetInstant, currentInstant);
        long minutesDifference = duration.toMinutes();

        return minutesDifference >= 5;
    }
}
