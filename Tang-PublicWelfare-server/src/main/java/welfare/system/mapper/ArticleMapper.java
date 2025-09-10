package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import welfare.system.model.dto.result.ArticleSimpleData;
import welfare.system.model.po.Article;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("UPDATE `file_resource` SET `status`=0 WHERE `id`=#{cover};" +
            "INSERT INTO `article`(`title`,`cover`,`introduction`,`content`,`post_uid`,`type`) " +
            "VALUES(#{title},#{cover},#{introduction},#{content},#{postUid},#{type.id});" +
            "SELECT LAST_INSERT_ID();"
    )
    int postArticle(Article article);

    @Delete("DELETE FROM `article` WHERE id=#{articleId}")
    void deleteArticleById(int articleId);

    @Select("SELECT * FROM `article` WHERE `id`=#{id}")
    Article selectById(int id);

    //获取全部文章 临时方法
    @Select("SELECT B.`id`,A.`title`,A.`type`,B.`project_id`,B.`period_id`,C.`classify_name`,C.`classify_introduction`,A.`launch_time` " +
            "FROM `article` A " +
            "JOIN `achievement` B ON A.`id`=B.`article_id` " +
            "LEFT JOIN `article_classification` C ON B.`period_id`=C.`id` " +
            "WHERE B.`status`<>2 " +

            "UNION ALL " +

            "SELECT B.`id`,A.`title`,A.`type`,B.`project_id`,B.`period_id`,C.`classify_name`,C.`classify_introduction`,A.`launch_time` " +
            "FROM `article` A " +
            "JOIN `train_resource` B ON A.`id`=B.`article_id` " +
            "LEFT JOIN `article_classification` C ON B.`period_id`=C.`id` " +
            "WHERE B.`status`=0 " +

            "UNION ALL " +

            "SELECT B.`id`,A.`title`,A.`type`,B.`project_id`,NULL AS `period_id`,NULL AS `classify_name`,NULL AS `classify_introduction`,A.`launch_time` " +
            "FROM `article` A " +
            "JOIN `activity` B " +
            "ON A.`id`=B.`article_id` " +
            "WHERE B.`status`=0 " +

            "ORDER BY `type` ASC,`launch_time` DESC"
    )
    List<ArticleSimpleData> getAllArticleSimpleData();

}
