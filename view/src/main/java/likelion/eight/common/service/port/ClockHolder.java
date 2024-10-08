package likelion.eight.common.service.port;

import java.time.LocalDateTime;
import java.util.Date;

public interface ClockHolder {

    long millis();
    long systemMillis();
    LocalDateTime now();
    LocalDateTime plusHours(Long hours);
    Date convertAbsoluteTime(LocalDateTime expiredTime);
}
