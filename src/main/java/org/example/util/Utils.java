package org.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.example.exception.CustomAppException;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Utils {
    public static UUID bytesToUuid(byte[] bytes) {
        var bufArr = ByteBuffer.wrap(bytes);
        return new UUID(bufArr.getLong(), bufArr.getLong());
    }

    public static Path getCachePath(String storeRootPath, UUID uid) {
        var uidStr = uid.toString();
        var dirPath = Path.of(storeRootPath, uidStr.substring(0, 3));
        var dirStr = dirPath.toAbsolutePath().toString();
        var dir = new File(dirStr);
        if (!dir.exists() && !dir.mkdirs())
            throw new CustomAppException("Can't create dir: " + dirStr + " for file: " + uid);
        return Path.of(dirStr, uidStr);
    }

    public static String getMessageForStatus(HttpStatus status) {
        return switch (status) {
            case NOT_FOUND -> "The resource does not exist";
            case INTERNAL_SERVER_ERROR -> "Something went wrong internally";
            case TOO_MANY_REQUESTS -> "Too many requests";
            case FORBIDDEN -> "Permission denied";
            case UNAUTHORIZED -> "Access denied!";
            case BAD_REQUEST -> "Invalid request";
            default -> status.getReasonPhrase();
        };
    }

    public static Set<String> getExceptionMessageChain(Throwable throwable) {
        return getExceptionMessageChain(throwable, null);
    }

    public static Set<String> getExceptionMessageChain(Throwable throwable, String rootMsg) {
        Set<String> result = null;
        if (rootMsg != null && rootMsg.equals(throwable.getMessage()))
            throwable = throwable.getCause();
        if (throwable != null)
            result = new HashSet<>();
        while (throwable != null) {
            result.add(throwable.getMessage());
            throwable = throwable.getCause();
        }
        return result;
    }

//    public static <T> String convertObjectToJson(ObjectMapper objectMapper, T object) {
//        log.info("Converting message for: " + object);
//        String message = "";
//        try {
//            message = objectMapper.writeValueAsString(object);
//        } catch (JsonProcessingException jpe) {
//            log.error("Couldn't convert object to JSON.", jpe);
//        }
//        log.info("Message converted for: " + object);
//        return message;
//    }
}
