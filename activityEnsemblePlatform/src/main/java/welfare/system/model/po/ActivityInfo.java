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
    private Integer contactImageId; // 联系方式图片ID
    private String studentOrganizeName;

    @Override
    public String check() {
        if (activityAddress == null) return "请填写活动地址!";
        if (activityDate == null) return "请填写活动日期!";
        if (contactImageId == null) return "请上传联系方式图片!";
        // 验证contactImageId是否有效
        if (welfare.system.model.CONSTANT.MAPPER.file.getFileById(contactImageId) == null) {
            return "联系方式图片无效!";
        }
        return super.check();
    }

    @Override
    public void setContentAsText() {
        super.setContentAsText();
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
        returnMap.put("contactImageId",contactImageId);
        returnMap.put("studentOrganizeName",studentOrganizeName);
        return returnMap;
    }
}
