package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
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
    private Integer contactImageId = 0;

    @Override
    public String check() {
        if (activityAddress == null)
            return "请填写活动地址!";
        if (activityDate == null)
            return "请填写活动日期!";
        if (contactWay == null || contactWay.isBlank())
            return "请填写联系方式!";
        String htmlCheckResult = HtmlHandleUtil.checkHTML(contactWay, HtmlHandleUtil.HTML_TYPE.CONTACT_WAY);
        if (htmlCheckResult != null)
            return htmlCheckResult;
        if( quota < 1)
            return "请填写正确的活动名额!";
        return super.check();
    }

    @Override
    public void setContentAsText() {
        super.setContentAsText();
        HtmlHandleUtil.escapeFromHTML(contactWay);
    }

    // //禁止用于循环内部!
    public Map<String, Object> toReturnMap() {
        Map<String, Object> returnMap = super.toReturnMap();
        returnMap.put("id", id);
        returnMap.put("activityAddress", activityAddress);
        returnMap.put("volunteerDuration", volunteerDuration);
        returnMap.put("activityDate", activityDate);
        returnMap.put("quota", quota);
        returnMap.put("registerNum", registerNum);
        returnMap.put("contactImageURL", VALUE.web_path + VALUE.img_web + MAPPER.file.getFileNameById(contactImageId));

        return returnMap;
    }
}
