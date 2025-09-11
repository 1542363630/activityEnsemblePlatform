package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.ArticleTypeEnum;
import welfare.system.util.HtmlHandleUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Integer id;

    protected String title;
    protected Integer cover = 1;
    protected String introduction;
    protected String content;
    protected Date launchTime;
    protected Integer postUid;
    private ArticleTypeEnum type;

    //检查文章是否存在问题
    public String check() {
        if (title == null || title.isBlank()) return "标题不可为空!";
        if (title.length() > 35) return "标题不可超过35字!";

        if (introduction == null || introduction.isBlank()) introduction = content.substring(0,30);
        else if (introduction.length() > 200) return "简介不可超过200字!";

        return HtmlHandleUtil.checkHTML(content, HtmlHandleUtil.HTML_TYPE.CONTENT);
    }

    //输入数据库时使用
    public void setContentAsText() {
        content = HtmlHandleUtil.escapeFromHTML(content);
    }

    //输出到前端使用
    public void setContentAsHTML() {
        content = HtmlHandleUtil.escapeToHTML(content);
    }

    //禁止用于循环内部!
    public Map<String, Object> toReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("title",title);
        returnMap.put("coverURL", VALUE.web_path + VALUE.img_web + MAPPER.file.getFileNameById(cover));
        //将text转义为html
        if (content != null) {
            setContentAsHTML();
        }
        returnMap.put("content",content);
        returnMap.put("launchTime",launchTime);
        returnMap.put("postUser", MAPPER.user.getUserByUid(postUid).getRealName());
        return returnMap;
    }

    @SuppressWarnings("unused")
    public void setType(int typeId) {
        type = ArticleTypeEnum.getType(typeId);
    }

    public void setTypeEnum(ArticleTypeEnum type) {
        this.type = type;
    }

}
