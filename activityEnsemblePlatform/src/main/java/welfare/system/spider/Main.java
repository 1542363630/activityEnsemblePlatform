package welfare.system.spider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

public class Main {

    /**
     * 调用登入接口主要过程
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        Scanner scanner = new Scanner(System.in);

        // ---第一步--- //

        /* 向用户获取用户名、密码 */
        System.out.println("统一认证账号:");
        String sdu_uid = scanner.next();
        System.out.println("统一认证密码:");
        String sdu_password = scanner.next();

        // ---第二步--- //

        /* 通过爬虫向统一认证登入获取跳转ticket */
        /* 这一步可以在后端完成，但考虑到用户隐私（统一认证密码）不应该上传到服务器，建议将这一步放在前端 */
        /* 这里登入的是山东大学数据中心 */
        SduLogin sduLogin = new SduLogin(sdu_uid, sdu_password);
        String ticket = sduLogin.login(ServicedeskLogin.GATE_WAY);  // 通过爬虫获取统一认证跳转ticket

        // ---第三步--- //

        /* 将拿到的跳转ticket传给后端的爬虫接口获取cookie */
        String cookie = ServicedeskLogin.fetchServicedeskCookie(ticket);
        System.out.println(cookie);

        // ---第四步--- //

        /* 使用cookie爬取用户信息 */
        Map<String, String> userInfo = ServicedeskLogin.fetchStudentInfo(cookie, sdu_uid);
        System.out.println("学号：" + userInfo.get("ID_NUMBER"));
        System.out.println("姓名：" + userInfo.get("USER_NAME"));
        System.out.println("性别：" + userInfo.get("USER_SEX"));
        System.out.println("学院名称：" + userInfo.get("UNIT_NAME"));
        System.out.println("身份类型：" + userInfo.get("ID_TYPE"));
        System.out.println("邮箱：" + userInfo.get("EMAIL"));




        /* 现在登入本科生院获取信息 */
        System.out.println();
        ticket = sduLogin.login(BkzhjxLogin.GATE_WAY);
        cookie = BkzhjxLogin.fetchBkzhjxCookie(ticket);
        userInfo = BkzhjxLogin.getUserInfo(cookie);
        System.out.println("学号：" + userInfo.get("casId"));
        System.out.println("姓名：" + userInfo.get("name"));
        System.out.println("性别：" + userInfo.get("gender"));
        System.out.println("院系：" + userInfo.get("depart"));
        System.out.println("专业：" + userInfo.get("major"));

        scanner.close();
    }
}
