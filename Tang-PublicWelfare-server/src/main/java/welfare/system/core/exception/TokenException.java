package welfare.system.core.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {
    private final String message;
    public TokenException(String msg) {
        message = msg;
    }

}
