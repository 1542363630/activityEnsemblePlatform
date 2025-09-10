package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import welfare.system.model.ENUM.OrganizationEnum;
import welfare.system.model.dto.page.PageData;
import welfare.system.model.po.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    //注册新用户
    @Select("INSERT INTO `user`(`sid`,`real_name`) VALUES(#{sid},#{realName});" +
            "SELECT * FROM `user` WHERE `sid`=#{sid}"
    )
    User register(String sid, String realName);

    //通过uid获取用户信息
    @Select("SELECT * FROM `user` WHERE `uid`=#{uid}")
    User getUserByUid(int uid);

    //通过sid获取用户信息
    @Select("SELECT * FROM `user` WHERE `sid`=#{sid}")
    User getUserBySid(String sid);

    //通过uid查询用户身份
    @Select("SELECT `authority` FROM `user` WHERE `uid`=#{uid}")
    int getAuthorityByUid(int uid);


    /*
    * 更新信息
    * */

    //更新用户信息
    @Update("UPDATE `user` " +
            "SET " +
                "`gender`=#{gender}," +
                "`qq_account`=#{qqAccount}," +
                "`phone_account`=#{phoneAccount}," +
                "`depart`=#{depart}," +
                "`major`=#{major} " +
            "WHERE `uid`=#{uid}"
    )
    void updateUserInfo(User user);

    //更新头像
    @Update("UPDATE `file_resource` SET `status`=1 WHERE `id`=(SELECT `avatar` FROM `user` WHERE `uid`=#{uid}) AND `id`<>0;" +
            "UPDATE `user` SET `avatar`=#{avatarId} WHERE `uid`=#{uid};" +
            "UPDATE `file_resource` SET `status`=0 WHERE `id`=#{avatarId};"
    )
    void updateUserAvatar(int uid,int avatarId);


    /*
    * 查询
    * */

    //查询用户总数
    @Select("SELECT COUNT(*) FROM `user`")
    int getUserNum();

    // 分页查找全部用户
    @Select("SELECT * FROM `user` ORDER BY `register_date` DESC LIMIT #{offset},#{num}")
    List<User> getAllUser(PageData pageData);

    //查询社团成员和普通成员数
    @Select("SELECT COUNT(*) FROM `user` WHERE (`org_name`=#{orgName} OR `org_name`=0) AND `authority`<>0")
    int getUserInMyOrgOrInNoneOrgNum(int orgName);

    //分页查找社团成员和普通成员
    @Select("SELECT * FROM `user` " +
            "WHERE (`org_name`=#{orgName.id} OR `org_name`=0) AND `authority`<>0 " +
            "ORDER BY `register_date` DESC " +
            "LIMIT #{offset},#{num}"
    )
    List<User> getUserInMyOrgOrInNoneOrg(OrganizationEnum orgName,int offset,int num);


    //搜索用户数
    @Select("SELECT COUNT(*) FROM `user` ${searchSql}")
    int getUserSearchNum(String searchSql);

    //搜索用户
    @Select("SELECT * FROM `user` ${searchSql} LIMIT #{offset},#{num}")
    List<User> searchUser(String searchSql,int offset,int num);

    /*
    * 管理
    * */

    //封禁用户(同时取消该用户活动记录)
    @Update("UPDATE `user` SET `authority`=0 WHERE `uid`=#{uid};" +
            "DELETE FROM `activity_register` WHERE `uid`=#{uid}")
    void deleteUser(int uid);

    //设置用户身份
    @Update("UPDATE `user` SET `org_name`=#{organization.id},`authority`=#{authority} WHERE `uid`=#{uid}")
    void setUserAuthority(int uid, int authority, OrganizationEnum organization);

}
