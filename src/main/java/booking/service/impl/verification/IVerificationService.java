package booking.service.impl.verification;


import booking.entity.User;
import booking.entity.Verification;

import java.util.Date;
import java.util.List;

public interface IVerificationService {

    Verification create(User user);

    List<Verification> getVerifications(User user);

    boolean isExpired(Date date);
}
