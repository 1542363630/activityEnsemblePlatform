package welfare.system.core.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import welfare.system.core.exception.TokenException;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.po.User;
import welfare.system.util.DateUtil;
import welfare.system.util.JwtUtil;

import java.util.Arrays;

/*
* 拦截器
* 通过拦截器进行权限管理和访问限制
* */

@Component
public class ConnectInterceptor implements HandlerInterceptor {

    //对任意游客开放的接口
    private static final String[] FOR_TOURIST = {
            "/user/login/start",
            "/user/login/verify",
            "/home-page",
            "/news"
    };

    //对普通成员开放的接口
    private static final String[] FOR_COMMON = {
            "/user/update",
            "/user/tang-org/activity",
            "/user/refresh-token",
            "/tang-org/activity",
            "/tang-org/achieve",
            "/tang-org/classify"
    };

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object Handle) {

        //获取请求地址
        String path = request.getServletPath();

        System.out.println("\n"+DateUtil.getCurrentTime()+"  new request:"+path+" from "+request.getRemoteAddr());

        //测试未带token时使用
//        if (true) return true;

        //任意游客可访问的接口，无需请求头即可访问
        if (Arrays.stream(FOR_TOURIST).anyMatch(path::startsWith)) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null) throw new TokenException("请登入!");

        int uid = request.getIntHeader("uid");
        if (uid == -1) throw new TokenException("请登入!");

        int claims = JwtUtil.getClaimsByToken(token);
        if (claims == -1) throw new TokenException("请重新登入!error by token expiration");
        else if (claims != uid) throw new TokenException("请重新登入!error by wrong token");

        User user = MAPPER.user.getUserByUid(uid);
        if (user == null) throw new TokenException("请重新登入!error by wrong uid");  //账号不存在
        if (user.isDelete()) throw new TokenException("账号好像没了T_T请联系管理员");  //账号被删除

        request.setAttribute("user",user);
        System.out.println("identity: org-"+user.getOrgName()+" & auth-"+user.getAuthority()+" & uid-"+user.getUid()+"\n");

        //权限为3，为超级管理员，可访问任意接口
        if (user.isSuperAdmin()) return true;

        //普通成员就可访问的接口
        if (Arrays.stream(FOR_COMMON).anyMatch(path::startsWith)) return true;

        //如果该成员有所属组织
        if (user.hasOrg()) {
            //访问社团下普通成员的数据，要权限大于1
            if (Arrays.stream(user.getOrgName().FOR_MEMBER).anyMatch(path::startsWith)) return true;

            //访问社团下管理员的数据，要权限大于2
            if (Arrays.stream(user.getOrgName().FOR_ADMIN).anyMatch(path::startsWith) && user.isAdmin()) return true;
        }

        throw new RuntimeException("你不能访问这里哦!");

    }
}
