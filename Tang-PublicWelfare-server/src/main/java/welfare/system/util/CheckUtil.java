package welfare.system.util;

import java.util.regex.Pattern;

/*
* 检查字符串格式工具类
* */

public class CheckUtil {

    //检查是否可能是uid搜索
    private static final String sidSearchFormat = "^[0-9]{1,12}$";
    public static boolean checkSidSearch(String searchText) {
        return Pattern.matches(sidSearchFormat,searchText);
    }

    //uid格式检查
    private static final String sidFormat = "^[0-9]{12}$";
    public static boolean checkSid(String sid) {
        return Pattern.matches(sidFormat,sid);
    }

    //邮箱格式检查
    private static final String emailFormat = "^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$";
    public static boolean checkEmail(String email) {
        return Pattern.matches(emailFormat,email);
    }

    //电话格式检查
    private static final String phoneFormat = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    public static boolean checkPhone(String phone) {
        return Pattern.matches(phoneFormat, phone);
    }

}
