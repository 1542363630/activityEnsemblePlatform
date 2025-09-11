package welfare.system.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.ENUM.FileUsageTypeEnum;
import welfare.system.model.ENUM.OrganizationEnum;
import welfare.system.model.dto.page.PageData;
import welfare.system.model.dto.result.PageResultData;
import welfare.system.model.dto.SDULoginData;
import welfare.system.model.dto.SDULoginResponse;
import welfare.system.model.dto.UserInfoResponse;
import welfare.system.model.dto.SduVerifyRequest;
import welfare.system.model.dto.search.UserSearchData;
import welfare.system.model.po.FileResource;
import welfare.system.model.po.User;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;
import welfare.system.spider.*;
import welfare.system.util.FileUtil;
import welfare.system.util.JwtUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    // 临时缓存：token -> 临时会话数据
    private final ConcurrentHashMap<String, SduSession> sduTemp = new ConcurrentHashMap<>();

    private static class SduSession {
        SduLogin sduLogin;
        String ticket;

        SduSession(SduLogin sduLogin, String ticket) {
            this.sduLogin = sduLogin;
            this.ticket = ticket;
        }
    }

    // 第一步：提交学号/密码，返回是否需要验证码以及临时 token
    public Response sduLoginStart(SDULoginData sduLoginData) {
        if (sduLoginData == null) return Response.failure(400,"缺少学号或密码");
        if(!sduLoginData.checkSid()) return Response.failure(400,"错误的学号格式");

        // 创建爬虫实例并尝试登录以获取 ticket 或验证码
        try {
            SduLogin sduLogin = new SduLogin(sduLoginData.getSid(), sduLoginData.getPassword());
            // 调用 login 方法尝试获取 ticket；这里我们假定 login 方法若需要验证码会返回 null 或抛出特定异常。
            String ticket = null;
            try {
                ticket = sduLogin.login(ServicedeskLogin.GATE_WAY,"");
            } catch (Exception ex) {
                // 如果登录过程抛出异常，继续保存会话以便后续 verify 使用
                ex.printStackTrace();
                return Response.failure(500,"请输入验证码");
            }

            String token = UUID.randomUUID().toString();
            // 将 sduLogin 与 ticket 存入临时缓存，verify 时会使用
            SduSession session = new SduSession(sduLogin, ticket);
            sduTemp.put(token, session);
            if (ticket != null) {
                // 已直接拿到 ticket，可以直接换取用户信息，不需要验证码
                String cookie = ServicedeskLogin.fetchServicedeskCookie(ticket);
                Map<String,String> userInfo = ServicedeskLogin.fetchStudentInfo(cookie, sduLogin.sdu_id);
                

                User user = MAPPER.user.getUserBySid(sduLogin.sdu_id);
                //如果已经注册过，则直接返回数据，否则注册
                if (user == null) {
                    user = User.fromExternalInfo(userInfo);
                    user = MAPPER.user.register(sduLogin.sdu_id, user.getRealName());
                }
                token = JwtUtil.generateToken(user.getUid());
                return Response.success(new SDULoginResponse(false, token, user));
            } else {
                // 需要验证码或后续步骤
                return Response.success(new SDULoginResponse(true, token, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return Response.failure(500,"内部错误: 无法启动SDU登录流程"+e.getMessage());
        }
    }

    // 第二步：提交验证码与 token，返回用户信息
    public Response sduLoginVerify(SduVerifyRequest sduLoginData) {
        if (sduLoginData == null || sduLoginData.getToken() == null || sduLoginData.getCode() == null) return Response.failure(400,"缺少 token 或验证码");
        SduSession session = sduTemp.get(sduLoginData.getToken());
        if (session == null) return Response.failure(400,"token 无效或已过期");

        try {
            String ticket = session.ticket;
            SduLogin sduLogin = session.sduLogin;

            if (ticket == null) {
                if (sduLogin == null) return Response.failure(500, "会话信息不完整");
                try {
                    ticket = sduLogin.login(ServicedeskLogin.GATE_WAY,sduLoginData.getCode());
                } catch (Exception ex) {
                    return Response.failure(400,"获取 ticket 失败: " + ex.getMessage());
                }
            }

            if (ticket == null) return Response.failure(500,"未能获取 ticket");

            String cookie = ServicedeskLogin.fetchServicedeskCookie(ticket);
            Map<String,String> userInfo = ServicedeskLogin.fetchStudentInfo(cookie, sduLogin.sdu_id);
            

            User user = MAPPER.user.getUserBySid(sduLogin.sdu_id);
            //如果已经注册过，则直接返回数据，否则注册
            if (user == null) {
                user = User.fromExternalInfo(userInfo);
                user = MAPPER.user.register(sduLogin.sdu_id, user.getRealName());
            }

            // 清理临时缓存
            sduTemp.remove(sduLoginData.getToken());

            @Getter
            @Setter
            class Ticket {
                private String token;
                private User user;
                Ticket(User user) {
                    this.user = user;
                    this.token = JwtUtil.generateToken(user.getUid());
                }
            }

            return Response.success(new Ticket(user));
        } catch (Exception e) {
            return Response.failure(500,"SDU 登录校验失败");
        }
    }

    // //统一认证登入
    // public Response login(SDULoginData sduLogin) {
    //     if (!sduLogin.checkSid()) return Response.failure(400,"错误的学号格式");
    //     //        if (!sduLogin.checkPassword()) return Response.failure(400,"请正确填写密码");
        
    //     //没有统一认证
    //     User user = MAPPER.user.getUserBySid(sduLogin.getSid());

    //     //有统一认证
    //     //User user = sduLogin.login();

    //     if (user == null) {
    //         return Response.failure(400,"学号或密码错误");
    //     }

    //     @Getter
    //     @Setter
    //     class Ticket {
    //         private String token;
    //         private User user;
    //         Ticket(User user) {
    //             this.user = user;
    //             this.token = JwtUtil.generateToken(user.getUid());
    //         }
    //     }

    //     return Response.success(new Ticket(user));
    // }

    //更改用户信息
    public Response updateUserInfo(int uid,User user) {
        user.setUid(uid);
        MAPPER.user.updateUserInfo(user);
        return Response.ok();
    }

    //更改头像(web端)
    public Response updateAvatar(int uid, MultipartFile avatar) {
        FileResource image = FileUtil.uploadImage(avatar,uid, FileUsageTypeEnum.Avatar);
        MAPPER.user.updateUserAvatar(uid,image.getId());
        return Response.success(image.getURL());
    }

    //更改头像(小程序端)
    public Response updateAvatar(int uid,String url) {
        FileResource image = FileUtil.uploadImage(url,uid, FileUsageTypeEnum.Avatar);
        MAPPER.user.updateUserAvatar(uid,image.getId());
        return Response.success(image.getURL());
    }


    //获取人员列表
    public Response getUserList(User sduLoginDatauestUser,PageData pageData,OrganizationEnum org) {
        int totalNum = sduLoginDatauestUser.getAuthority() == 3
                        ?
                    MAPPER.user.getUserNum()
                        :
                    MAPPER.user.getUserInMyOrgOrInNoneOrgNum(sduLoginDatauestUser.getOrgName().id);
        if (totalNum == 0) {
            return Response.failure(CommonErr.NO_DATA);
        } else {
            pageData.setTotalNum(totalNum);
        }

        int totalPage = pageData.calculateTotalPage();
        if (pageData.getPage() > totalPage) {
            return Response.failure(404, "页数过大!总页数：" + totalPage);
        }

        pageData.calculateOffset();
        List<Map<String,Object>> returnList = sduLoginDatauestUser.getAuthority() == 3
                ?
            MAPPER.user.getAllUser(pageData)
                    .stream()
                    .map(user -> user.toReturnMap(org))
                    .toList()
                :
            MAPPER.user.getUserInMyOrgOrInNoneOrg(
                        sduLoginDatauestUser.getOrgName(),
                        pageData.getOffset(),
                        pageData.getNum()
                    )
                    .stream()
                    .map(user -> user.toReturnMap(org))
                    .toList();

        PageResultData<Map<String,Object>> pageResultData = new PageResultData<>(
                totalPage,
                totalNum,
                returnList,
                null
        );

        return Response.success(pageResultData.toReturnMapExceptClassificationList());
    }

    //搜索人员
    public Response searchUser(User sduLoginDatauestUser,UserSearchData userSearchData,OrganizationEnum org) {
        PageData pageData = userSearchData.getPageData();
        String searchSql = userSearchData.getSearchSql(sduLoginDatauestUser.getAuthority(),sduLoginDatauestUser.getOrgName());

        int totalNum = MAPPER.user.getUserSearchNum(searchSql);
        if (totalNum == 0) {
            return Response.failure(CommonErr.NO_DATA);
        } else {
            pageData.setTotalNum(totalNum);
        }

        int totalPage = pageData.calculateTotalPage();
        if (pageData.getPage() > totalPage) {
            return Response.failure(404, "页数过大!总页数：" + totalPage);
        }

        pageData.calculateOffset();
        PageResultData<Map<String,Object>> pageResultData = new PageResultData<>(
                totalPage,
                totalNum,
                MAPPER.user.searchUser(searchSql,pageData.getOffset(),pageData.getNum())
                        .stream()
                        .map(user -> user.toReturnMap(org))
                        .toList(),
                null
        );

        return Response.success(pageResultData.toReturnMapExceptClassificationList());
    }

    //封禁(删除)用户
    public Response deleteUser(int uid) {
        MAPPER.user.deleteUser(uid);
        return Response.ok();
    }

    //设置用户身份(暂时没有多社团，不考虑，后面加多社团时需要更改)
    public Response setUserAuthority(int uid, int authority, OrganizationEnum org) {
        if (authority < 0 || authority > 2) {
            return Response.failure(400,"无法设置用户权限为"+authority+"!");
        }
        int oldAuthority = MAPPER.user.getAuthorityByUid(uid);
        if (oldAuthority < 1) return Response.failure(400,"该用户已被封禁!");
        else if (oldAuthority > 2) return Response.failure(400,"你没有这等权限!");

        if (authority == 0) {  //设置权限为普通成员
            MAPPER.user.setUserAuthority(uid, 1, OrganizationEnum.None);
        } else if (authority == 1) {  //设置权限为唐社成员
            MAPPER.user.setUserAuthority(uid, 1, org);
        } else {  //设置为唐社管理
            MAPPER.user.setUserAuthority(uid,2, org);
        }

        return Response.ok();
    }

}
