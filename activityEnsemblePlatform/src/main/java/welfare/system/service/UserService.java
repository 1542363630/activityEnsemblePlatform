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
import welfare.system.model.dto.search.UserSearchData;
import welfare.system.model.po.FileResource;
import welfare.system.model.po.User;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;
import welfare.system.util.FileUtil;
import welfare.system.util.JwtUtil;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    //统一认证登入
    public Response login(SDULoginData sduLogin) {
        if (!sduLogin.checkSid()) return Response.failure(400,"错误的学号格式");
        //        if (!sduLogin.checkPassword()) return Response.failure(400,"请正确填写密码");
        
        //没有统一认证
        User user = MAPPER.user.getUserBySid(sduLogin.getSid());

        //有统一认证
        //User user = sduLogin.login();

        if (user == null) {
            return Response.failure(400,"学号或密码错误");
        }

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
    }

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
    public Response getUserList(User requestUser,PageData pageData,OrganizationEnum org) {
        int totalNum = requestUser.getAuthority() == 3
                        ?
                    MAPPER.user.getUserNum()
                        :
                    MAPPER.user.getUserInMyOrgOrInNoneOrgNum(requestUser.getOrgName().id);
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
        List<Map<String,Object>> returnList = requestUser.getAuthority() == 3
                ?
            MAPPER.user.getAllUser(pageData)
                    .stream()
                    .map(user -> user.toReturnMap(org))
                    .toList()
                :
            MAPPER.user.getUserInMyOrgOrInNoneOrg(
                        requestUser.getOrgName(),
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
    public Response searchUser(User requestUser,UserSearchData userSearchData,OrganizationEnum org) {
        PageData pageData = userSearchData.getPageData();
        String searchSql = userSearchData.getSearchSql(requestUser.getAuthority(),requestUser.getOrgName());

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
