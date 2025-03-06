package sd.device.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserService {

    void addUserId(Integer id);

    void deleteUserId(Integer id) throws JsonProcessingException;
}
