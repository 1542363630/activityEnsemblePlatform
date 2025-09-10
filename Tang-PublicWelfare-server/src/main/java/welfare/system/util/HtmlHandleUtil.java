package welfare.system.util;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.util.HtmlUtils;

public class HtmlHandleUtil {

    public static final String unsafeElement = "script,iframe,form,svg,math";
    public static final String fileElement = "img,video,audio";

    @AllArgsConstructor
    public enum HTML_TYPE {
        CONTENT("文章",10000),CONTACT_WAY("联系方式描述",500);
        final String name;
        final int maxLength;
    }

    public static String checkHTML(String content,HTML_TYPE htmlType) {
        if (content == null || content.isBlank()) return "内容不可为空!";

        Document htmlDocument;
        try {
            htmlDocument = Jsoup.parse(content);
        } catch (RuntimeException e) {
            return "文章编辑格式存在错误!";
        }

        //检查是否有危险的标签存在
        if (htmlDocument.select(unsafeElement).toArray().length > 0) {
            return "包含非法的文本!";
        }

        // 删除所有的 <文件> 标签
        for (Element element : htmlDocument.select(fileElement)) {
            element.remove();
        }
        // 获取所有文本
        String text = htmlDocument.body().text();
        // 计算文字数量
        if (text.length() > htmlType.maxLength) {
            return htmlType.name+"不可超过"+htmlType.maxLength+"字!";
        }

        return null;
    }

    public static String escapeFromHTML(String html) {
        return HtmlUtils.htmlEscape(html);
    }

    public static String escapeToHTML(String text) {
        return HtmlUtils.htmlUnescape(text);
    }
}
