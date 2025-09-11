package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.Gender;
import welfare.system.model.ENUM.OrganizationEnum;
import welfare.system.util.CheckUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer uid;

    private String sid;
    private Integer avatar = 0;
    private String realName;
    private OrganizationEnum orgName = OrganizationEnum.None;
    private Gender gender;
    private String qqAccount;
    private String phoneAccount;
    private String depart;
    private String major;

    /*
    * 权限说明：
    * 0: 被删除(封禁)
    * 1: 普通学生
    * 2: 学生组织管理员
    * 3: 平台管理员
    * */
    private int authority = 1;
    private Date registerDate;

    public Map<String,Object> toReturnMap(OrganizationEnum org) {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("uid",uid);
        returnMap.put("sid",sid);
        returnMap.put("realName",realName);
        returnMap.put("orgName",orgName);
        returnMap.put("gender",gender);
        returnMap.put("qqAccount",qqAccount);
        returnMap.put("phoneAccount",phoneAccount);
        returnMap.put("depart",depart);
        returnMap.put("major",major);
        returnMap.put("authority",authority);
        if (authority == 0) {
            returnMap.put("authorityInOrg",-1);
        } else {
            returnMap.put("authorityInOrg",orgName == org ? authority : 0);  //映射为普通成员0，社团成员1，社团管理员2，超级管理员3
        }
        returnMap.put("registerDate",registerDate);
        return returnMap;
    }

    @SuppressWarnings("unused")
    public String getAvatarUrl() {
        return VALUE.web_path + VALUE.img_web + MAPPER.file.getFileNameById(avatar);
    }

    @SuppressWarnings("unused")
    public void setPhoneAccount(String phoneAccount) {
        if (phoneAccount == null || CheckUtil.checkPhone(phoneAccount)) {
            this.phoneAccount = phoneAccount;
        } else {
            throw new RuntimeException("电话格式错误!");
        }
    }

    @SuppressWarnings("unused")
    public void setOrgName(int orgId) {
        this.orgName = OrganizationEnum.getOrg(orgId);
    }

    public boolean hasOrg() {
        return orgName != OrganizationEnum.None;
    }

    @JsonIgnore
    public boolean isDelete() {
        return authority == 0;
    }

    @JsonIgnore
    public boolean isAdmin() {
        return authority >= 2;
    }

    @JsonIgnore
    public boolean isSuperAdmin() {
        return authority == 3;
    }

    // 根据对接系统返回的 userInfo 字段构建 User
    public static User fromExternalInfo(Map<String, String> userInfo) {
        if (userInfo == null) return null;
        User u = new User();
        String sidVal = userInfo.getOrDefault("ID_NUMBER", userInfo.getOrDefault("casId", userInfo.get("sid")));
        String nameVal = userInfo.getOrDefault("USER_NAME", userInfo.getOrDefault("name", null));
        String genderVal = userInfo.getOrDefault("USER_SEX", userInfo.getOrDefault("gender", "UNKNOWN"));
        String departVal = userInfo.getOrDefault("UNIT_NAME", userInfo.getOrDefault("depart", null));
        String majorVal = userInfo.getOrDefault("major", null);

        u.setSid(sidVal);
        u.setRealName(nameVal);
        Gender g = switch (genderVal.toUpperCase()) {
            case "MALE", "男" -> Gender.MALE;
            case "FEMALE", "女" -> Gender.FEMALE;
            default -> Gender.UNKNOWN;
        };
        u.setGender(g);
        u.setDepart(departVal);
        u.setMajor(majorVal);
        return u;
    }

}
