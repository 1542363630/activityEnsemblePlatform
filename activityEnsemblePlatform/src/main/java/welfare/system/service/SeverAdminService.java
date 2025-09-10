package welfare.system.service;

import org.springframework.stereotype.Service;
import welfare.system.model.vo.Response;
import welfare.system.util.FileUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SeverAdminService {

    //移除文件
    public Response removeFile(Date date) {
        List<Map<String,Object>> unSuccessList = FileUtil.removeFile(date);
        if (unSuccessList == null) {
            return Response.failure(400,"该时间段前未查询到无效文件");
        }
        return Response.success(unSuccessList);
    }

}
