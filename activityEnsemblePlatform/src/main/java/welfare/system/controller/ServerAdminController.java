package welfare.system.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import welfare.system.model.vo.Response;
import welfare.system.service.SeverAdminService;

import java.util.Date;
import java.util.Map;

/*
* 以下接口需要权限为3的超级管理员才能访问
* */

@RestController
@RequestMapping("/admin")
public class ServerAdminController {

    @Resource
    SeverAdminService severAdminService;

    /*
    * 移除存储在服务器上的无效文件资源
    * */
    @PostMapping("/file/remove")
    public Response removeFile(@RequestBody Map<String, Date> date) {
        return severAdminService.removeFile(date.get("date"));
    }

}
