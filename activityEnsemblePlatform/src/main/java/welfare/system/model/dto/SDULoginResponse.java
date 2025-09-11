package welfare.system.model.dto;

import welfare.system.model.po.User;

public class SDULoginResponse {
    private boolean sent; // 是否发送了验证码/需要验证码
    private String token; // token
    private User user; // 用户信息

    public SDULoginResponse() {}
    public SDULoginResponse(boolean sent, String token,User user) {
        this.sent = sent;
        this.token = token;
        this.user = user;
    }

    public boolean isSent() { return sent; }
    public void setSent(boolean sent) { this.sent = sent; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
