package welfare.system.model.dto;

import java.util.Map;

public class UserInfoResponse {
    private Map<String,String> info;

    public UserInfoResponse() {}
    public UserInfoResponse(Map<String,String> info) { this.info = info; }

    public Map<String,String> getInfo() { return info; }
    public void setInfo(Map<String,String> info) { this.info = info; }
}
