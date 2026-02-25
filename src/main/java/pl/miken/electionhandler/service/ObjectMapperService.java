package pl.miken.electionhandler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.miken.electionhandler.exception.CodedException;
import tools.jackson.databind.ObjectMapper;

import static pl.miken.electionhandler.enumeration.ErrorCodes.DESERIALIZATION_ERROR;
import static pl.miken.electionhandler.enumeration.ErrorCodes.SERIALIZATION_ERROR;

@Service
@RequiredArgsConstructor
public class ObjectMapperService {

    private final ObjectMapper objectMapper;

    public <T> String serializeObject(T object, String errorMessage) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new CodedException(SERIALIZATION_ERROR, errorMessage);
        }
    }

    public <T> T deserializeObject(String value,  Class<T> clazz, String errorMessage) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch (Exception e) {
            throw new CodedException(DESERIALIZATION_ERROR, errorMessage);
        }
    }
}
