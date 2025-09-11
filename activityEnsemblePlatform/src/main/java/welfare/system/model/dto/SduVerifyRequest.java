package welfare.system.model.dto;

public class SduVerifyRequest {
    private String token; // 临时 token
    private String code; // 验证码

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
