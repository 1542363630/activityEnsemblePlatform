package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import welfare.system.model.po.News;

import java.util.List;
import java.util.Map;

@Mapper
public interface NewsMapper extends BaseMapper<News> {

    //发布新闻
    @Select("INSERT INTO `news`(`article_id`,`status`) VALUES(#{articleId},#{status});" +
            "SELECT LAST_INSERT_ID();"
    )
    int uploadNews(News news);

    //获取指定id的新闻
    @Select("SELECT * FROM `news` A JOIN `article` B ON A.`article_id`=B.`id` WHERE A.`id`=#{id}")
    News getNewsById(int id);

    //获取所有未删除新闻条数
    @Select("SELECT COUNT(*) FROM `news` WHERE `status`=1 OR `status`=0")
    Integer numberOfAllNews();

    //获取所有未删除新闻（分页查询）
    @Select("SELECT " +
                "A.`id`," +
                "B.`title`," +
                "B.`introduction`," +
                "B.`launch_time`," +
                "C.`file_name` AS coverURL " +
            "FROM `news` A " +
            "JOIN `article` B ON A.`article_id`=B.`id` " +
            "LEFT JOIN `file_resource` C ON B.`cover`=C.`id` " +
            "WHERE A.`status`<>2 " +
            "ORDER BY B.`launch_time` DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}"
    )
    List<Map<String,Object>> selectNewsByPage(Integer pageSize,Integer offset);

    //获取新闻（按关键词）
    @Select("SELECT " +
                "A.`id`," +
                "B.`title`," +
                "B.`introduction`," +
                "B.`launch_time`," +
                "C.`file_name` AS coverURL " +
            "FROM `news` A " +
            "JOIN `article` B ON A.`article_id`=B.`id` " +
            "LEFT JOIN `file_resource` C ON B.`cover`=C.`id` " +
            "WHERE A.`status`<>2 " +
            "AND ((B.`title` like concat('%',#{keyword},'%'))) " +
            "ORDER BY B.`launch_time` DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}"
    )
    List<Map<String,Object>> selectNewsByKeyword(String keyword,Integer pageSize,Integer offset);

    //获取新闻数量（按关键词）
    @Select("SELECT " +
            "COUNT(A.id)" +
            "FROM `news` A " +
            "JOIN `article` B ON A.`article_id`=B.`id` " +
            "LEFT JOIN `file_resource` C ON B.`cover`=C.`id` " +
            "WHERE A.`status`<>2 " +
            "AND ((B.`title` like concat('%',#{keyword},'%'))) "
    )
    Integer selectNumberByKeyword(String keyword);

    //获取轮播新闻
    @Select("SELECT " +
                "A.`id`," +
                "B.`title`," +
                "B.`introduction`," +
                "B.`launch_time`," +
                "C.`file_name` AS coverURL " +
            "FROM `news` A " +
            "JOIN `article` B ON A.`article_id`=B.`id` " +
            "LEFT JOIN `file_resource` C ON B.`cover`=C.`id` " +
            "WHERE A.`status` = 1 " +
            "ORDER BY B.`launch_time` DESC " +
            "LIMIT #{num}"
    )
    List<Map<String,Object>> selectTopNews(int num);

    //获取最近的新闻id列
    @Select("SELECT C.`id`,C.`title`,C.`introduction`,C.`launch_time`,D.`file_name` AS coverURL " +
            "FROM (" +
                "SELECT B.`id`, B.`title`, B.`introduction`, B.`launch_time`, B.`cover` " +
                "FROM `news` A " +
                "JOIN `article` B ON A.`article_id` = B.`id` " +
                "WHERE A.`status` = 0 OR A.`status` = 1 " +
                "ORDER BY ABS(DATEDIFF(B.`launch_time`, #{date})) ASC " +
                "LIMIT #{num} " +
            ") AS C " +
            "LEFT JOIN `file_resource` D ON C.`cover`=D.`id` "
    )
    List<Map<String,Object>> selectRecommendNews(String date,int num);

    //根据新闻 id 删除新闻(假删)
    @Update("UPDATE `news` SET `status` = 2 where id=#{newsId}")
    void deleteNewsById(Integer newsId);

    //根据id直接删除新闻
    @Delete("DELETE FROM `news` WHERE `id`=#{id}")
    void removeNewsById(int id);


}
