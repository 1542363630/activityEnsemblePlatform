package welfare.system.model.dto;

import kong.unirest.Unirest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.po.User;
import welfare.system.util.CheckUtil;

@Getter
@Setter
@NoArgsConstructor
public class SDULoginData {
    String sid;
    String password;

    //检查uid是否正确填写
    public boolean checkSid() {
        return CheckUtil.checkSid(sid);
    }

    //检查password是否正确填写
    public boolean checkPassword() {
        return password != null && !password.isEmpty();
    }

    //统一认证登录
    public User login() {

        if (!checkPassword()) {
            User user_test = MAPPER.user.getUserBySid(sid);
            if (user_test == null) {
                return MAPPER.user.register(sid, sid);
            } else {
                return user_test;
            }
        }

        // 获取ticket
        String ticket = Unirest.post("https://pass.sdu.edu.cn/cas/restlet/tickets")
                .body("username=" + sid + "&password=" + password)
                .asString()
                .getBody();

        //如果ticket不以TGT开头，说明登入失败
        if (!ticket.startsWith("TGT")) {
            return null;
        }

        //先尝试从数据库获取成员信息
        User user = MAPPER.user.getUserBySid(sid);

        //如果已经注册过，则直接返回数据，否则注册
        if (user != null) {
            return user;
        }

        // 获取sTicket
        String sTicket = Unirest.post("https://pass.sdu.edu.cn/cas/restlet/tickets/" + ticket)
                .body("service=https://service.sdu.edu.cn/tp_up/view?m=up")
                .asString()
                .getBody();

        if (!sTicket.startsWith("ST")) {
            return null;
        }

        // 获取姓名和学号
        String validationResult = Unirest.get("https://pass.sdu.edu.cn/cas/serviceValidate")
                .queryString("ticket", sTicket)
                .queryString("service", "https://service.sdu.edu.cn/tp_up/view?m=up")
                .asString()
                .getBody();

        Document document = Jsoup.parse(validationResult);
        String name = document.getElementsByTag("cas:USER_NAME").text();//获取学生姓名

        return MAPPER.user.register(sid,name);
    }

}
