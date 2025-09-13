package welfare.system.model.ENUM;

/*
* 将来有新的组织，在此添加，注意设置可访问权限
* 同时在数据库user表中的org_name字段添加对应枚举
* */

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(force = true)
public enum OrganizationEnum {
    None,
    Tang((short) 1,
        new String[] {
                "/tang-org/train-resource",
                "/user/tang-org"
        },
        new String[] {
                "/tang-org",
                "/upload"
    }),
    OTHER((short) 2,
            new String[]{},
            new String[]{}
    );

    //存储在数据库中的id
    public final short id;

    //普通成员能访问的接口
    public final String[] FOR_MEMBER;

    //管理员能访问的接口
    public final String[] FOR_ADMIN;

    public static OrganizationEnum getOrg(int orgId) {
        return switch (orgId) {
            case 0 -> None;
            case 1 -> Tang;
            case 2 -> OTHER;
            default -> throw new IllegalStateException("Unexpected value: " + orgId + ". 请检查数据库!");
        };
    }

}
