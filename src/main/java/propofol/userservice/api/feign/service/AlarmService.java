package propofol.userservice.api.feign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import propofol.userservice.api.feign.AlarmServiceFeignClient;
import propofol.userservice.api.feign.AlarmType;
import propofol.userservice.api.feign.dto.AlarmSaveDto;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmServiceFeignClient alarmServiceFeignClient;

    public void saveAlarm(long toId, String message, String token){
        alarmServiceFeignClient.saveAlarm(token, new AlarmSaveDto(toId, message, AlarmType.SUBSCRIBE, null));
    }
}
