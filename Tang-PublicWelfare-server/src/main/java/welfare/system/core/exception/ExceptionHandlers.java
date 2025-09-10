package welfare.system.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;

/*
* 进行一个错误的全局捕获
* */

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(RuntimeException.class)
    public Response error(Exception e) {
        if (e instanceof MyBatisSystemException) {
            System.out.println("连接到数据库异常!");
            e.printStackTrace();
            return Response.failure(CommonErr.CONNECT_TO_MYSQL_FAILED);
        }
        if (e instanceof MaxUploadSizeExceededException) {
            System.out.println(e.getMessage());
            return Response.failure(CommonErr.FILE_OUT_OF_LIMIT);
        }

        if (e instanceof CommonErrException) {
            System.out.println(((CommonErrException) e).ERROR.getMessage());
            return Response.failure(((CommonErrException) e).ERROR);
        }
        if (e instanceof TokenException) {
            System.out.println(e.getMessage());
            return Response.failure(CommonErr.TOKEN_CHECK_FAILED.setMsg(e.getMessage()));
        }
        if (e instanceof CheckException) {
            System.out.println(e.getMessage());
            return Response.failure(CommonErr.POST_CHECK_FAILED.setMsg(e.getMessage()));
        }

        System.out.println(e.getMessage());
        e.printStackTrace();
        return Response.error(401, String.valueOf(e));
    }


}
