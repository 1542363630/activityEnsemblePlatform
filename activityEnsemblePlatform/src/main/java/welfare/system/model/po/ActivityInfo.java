package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.util.HtmlHandleUtil;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("activity_info")
public class ActivityInfo extends Activity {
    private Integer id;

    private String activityAddress;
    private Double volunteerDuration = 0.0;
    private String activityDate;
    private Integer quota = 1;
    private Integer registerNum = 0;
    private String contactWay;
    private String studentOrganizeName;  

    @Override
    public String check() {
        if (activityAddress == null) return "请填写活动地址!";
        if (activityDate == null) return "请填写活动日期!";
        if (contactWay == null || contactWay.isBlank()) return "请填写联系方式!";
        String htmlCheckResult = HtmlHandleUtil.checkHTML(contactWay, HtmlHandleUtil.HTML_TYPE.CONTACT_WAY);
        if (htmlCheckResult != null) return htmlCheckResult;
        return super.check();
    }

    @Override
    public void setContentAsText() {
        super.setContentAsText();
        HtmlHandleUtil.escapeFromHTML(contactWay);
    }

    //禁止用于循环内部!
    public Map<String, Object> toReturnMap() {
        Map<String,Object> returnMap = super.toReturnMap();
        returnMap.put("id",id);
        returnMap.put("activityAddress",activityAddress);
        returnMap.put("volunteerDuration",volunteerDuration);
        returnMap.put("activityDate",activityDate);
        returnMap.put("quota",quota);
        returnMap.put("registerNum",registerNum);
        returnMap.put("studentOrganizeName",studentOrganizeName);
        return returnMap;
    }
}
