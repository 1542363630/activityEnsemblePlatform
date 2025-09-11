package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import welfare.system.model.dto.SDULoginData;
import welfare.system.model.dto.SduVerifyRequest;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.service.UserService;
import welfare.system.util.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserService userService;

    /*
    * SDU 爬虫两步登录：1) 提交学号/密码 -> 返回是否发送验证码以及临时 token
    * 2) 提交验证码和 token -> 返回用户信息
    * */
    @PostMapping("/login/start")
    public Response sduLoginStart(@RequestBody SDULoginData req) {
        return userService.sduLoginStart(req);
    }

    @PostMapping("/login/verify")
    public Response sduLoginVerify(@RequestBody SduVerifyRequest req) {
        return userService.sduLoginVerify(req);
    }

    /*
    * 统一认证登入
    * */
    // @PostMapping("/login")
    // public Response login(@RequestBody SDULoginData sduLoginData) {
    //     return userService.login(sduLoginData);
    // }

    /*
    * 获取个人信息
    * */
    @RequestMapping("/info")
    public Response getMyInfo(HttpServletRequest request) {
        return Response.success(request.getAttribute("user"));
    }

    /*
    * 更改用户信息
    * */
    @PostMapping("/update/info")
    public Response updateMyInfo(HttpServletRequest request,@RequestBody User user) {
        return userService.updateUserInfo(((User) request.getAttribute("user")).getUid(),user);
    }

    /*
    * 更新用户头像(web端)
    * */
    @PostMapping("/update/avatar")
    public Response updateMyAvatar(HttpServletRequest request,@RequestParam(name = "avatar") MultipartFile avatar) {
        return userService.updateAvatar(((User) request.getAttribute("user")).getUid(),avatar);
    }

    /*
    * 更新用户头像(小程序端)
    * */
    @PostMapping("/update/avatar/wx")
    public Response updateMyAvatar(HttpServletRequest request, @RequestBody Map<String,String> url) {
        return userService.updateAvatar(((User) request.getAttribute("user")).getUid(),url.get("url"));
    }

    /*
    * 刷新token
    * */
    @GetMapping("/refresh-token")
    public Response refreshToken(HttpServletRequest request) {
        return Response.success(JwtUtil.generateToken(request.getIntHeader("uid")));
    }

}
