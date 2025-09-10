package welfare.system.core.exception;

import welfare.system.model.vo.CommonErr;

public class CommonErrException extends RuntimeException{
    public CommonErr ERROR;
    public CommonErrException(CommonErr err) {
        ERROR = err;
    }
}
