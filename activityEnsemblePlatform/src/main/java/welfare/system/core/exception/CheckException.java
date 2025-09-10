package welfare.system.core.exception;

import lombok.Getter;

@Getter
public class CheckException extends RuntimeException {
    private final String message;
    public CheckException(String msg) {
        this.message = msg;
    }
}
