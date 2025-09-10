package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import welfare.system.model.ENUM.ClassifyTypeEnum;
import welfare.system.model.dto.result.ArticleSimpleData;
import welfare.system.model.dto.result.ClassificationResultData;
import welfare.system.model.dto.result.ClassificationSimpleData;
import welfare.system.model.po.ArticleClassification;

import java.util.List;

@Mapper
public interface ArticleClassificationMapper extends BaseMapper<ArticleClassification> {

    /*
    * 校验
    * */

    //通过id查看该分类类型
    @Select("SELECT `classify_type` FROM `article_classification` WHERE id=#{id}")
    ClassifyTypeEnum getClassifyTypeById(int id);

    // 通过id查看分类名称
    @Select("SELECT `classify_name` FROM `article_classification` WHERE id=#{id}")
    String getClassifyNameById(int id);

    //检查分类是否存在且校验正确性
    @Select("SELECT EXISTS(SELECT 1 FROM `article_classification` WHERE id=#{id} AND `classify_type`=#{classifyType} LIMIT 1)")
    boolean checkClassifyType(int id,ClassifyTypeEnum classifyType);

    //检查许多分类是否存在且校验正确性
//    boolean checkClassifyListType(Integer[] idList,ClassifyTypeEnum classifyType);


    /*
    * 增删改 三个步骤中，有涉及coverId变化、会引起file_resource表改动的操作，都已经写到mysql中了，这里没必要再次重复
    * */


    /*
    * 添加
    * */

    //添加分类
    @Select("INSERT INTO `article_classification`(" +
                "`classify_name`," +
                "`classify_introduction`," +
                "`classify_cover`," +
                "`classify_type`," +
                "`classify_rank`," +
                "`upper_classify`" +
            ") " +
            "VALUE (" +
                "#{classifyName}," +
                "#{classifyIntroduction}," +
                "#{classifyCover}," +
                "#{classifyType}," +
                "(" +
                    "SELECT COALESCE(MAX(`classify_rank`), -1) " +
                    "FROM (" +
                        "SELECT `classify_rank` " +
                        "FROM `article_classification` " +
                        "WHERE `classify_type`=#{classifyType} AND `upper_classify`=#{upperClassify}" +
                    ") `A`" +
                ")+1," +
                "#{upperClassify}" +
            ");" +
            "SELECT LAST_INSERT_ID();"
    )
    int addClassification(ArticleClassification section);

    /*
    * 更改分类
    * */

    @Update("UPDATE `article_classification` " +
            "SET " +
                "`classify_name`=COALESCE(#{classifyName},`classify_name`)," +
                "`classify_introduction`=COALESCE(#{classifyIntroduction},`classify_introduction`)," +
                "`classify_cover`=COALESCE(#{classifyCover},`classify_cover`)," +
                "`upper_classify`=COALESCE(#{upperClassify},`upper_classify`)," +
                "`classify_rank`= CASE WHEN #{upperClassify} IS NOT NULL THEN " +
                "(" +
                    "SELECT COALESCE(MAX(`classify_rank`), -1) " +
                    "FROM (" +
                        "SELECT `classify_rank` " +
                        "FROM `article_classification` " +
                        "WHERE `classify_type`=#{classifyType} AND `upper_classify`=#{upperClassify}" +
                    ") A" +
                ")+1 ELSE `classify_rank` END " +
            "WHERE `id`=#{id}"
    )
    void updateClassify(ArticleClassification articleClassification);


    /*
    * 更改文章分类
    * */

    //更改achievement
    @Select("UPDATE `achievement` " +
            "SET " +
                "`project_id`=COALESCE(#{projectId},`project_id`)," +
                "`period_id`=COALESCE(#{periodId},`period_id`) " +
            "WHERE `id`=#{id};" +
            "SELECT ROW_COUNT()>0;"
    )
    boolean updateAchievement(ArticleSimpleData articleSimpleData);

    //更改activity
    @Select("UPDATE `activity` SET `project_id`=COALESCE(#{projectId},`project_id`) WHERE `id`=#{id};" +
            "SELECT ROW_COUNT()>0;"
    )
    boolean updateActivity(ArticleSimpleData articleSimpleData);


    /*
    * 查询
    * */

    //通过上层分类获取id列表
    @Select("SELECT `id` " +
            "FROM `article_classification` " +
            "WHERE `upper_classify`=#{upperId} AND `classify_type`=#{classifyType}"
    )
    List<Integer> getIdListByUpperClassify(int upperId, ClassifyTypeEnum classifyType);

    //通过id查询某一分类
    @Select("SELECT A.*,B.`file_name` AS coverURL " +
            "FROM `article_classification` A " +
            "LEFT JOIN `file_resource` B ON A.`classify_cover`=B.`id` " +
            "WHERE A.`id`=#{id}"
    )
    ClassificationResultData getClassifyById(int id);

    // 通过 id 列表查询有那些时间线
    List<ClassificationResultData> getClassifyByIdList(Integer[] idList);

    //获取某一类型的全部分类
    @Select("SELECT A.*,B.`file_name` AS coverURL " +
            "FROM `article_classification` A " +
            "LEFT JOIN `file_resource` B ON A.`classify_cover`=B.`id` " +
            "WHERE `classify_type`=#{classifyType} " +
            "ORDER BY `classify_rank` ASC"
    )
    List<ClassificationResultData> getClassifyListByType(ClassifyTypeEnum classifyType);

    //获取某一类型某一上层分类的全部分类
    @Select("SELECT A.*,B.`file_name` AS coverURL " +
            "FROM `article_classification` A " +
            "LEFT JOIN `file_resource` B ON A.`classify_cover`=B.`id` " +
            "WHERE `upper_classify`=#{upperId} AND `classify_type`=#{classifyType} " +
            "ORDER BY `classify_rank` ASC"
    )
    List<ClassificationResultData> getSectionListByTypeAndUpperClassify(int upperId, ClassifyTypeEnum classifyType);

    @Select("SELECT `article_num` FROM `article_classification` WHERE id=#{id}")
    int getArticleNumById(int id);

    /*
    * 删除
    * */

    //更新所有由于项目删除引起的变化
    void updateAllCausedByProjectChange(List<Integer> idList);

    //更新由于项目删除引起的变化
    void updateOneCausedByProjectChange(Integer id);

    //更新由于时间段删除引起的变化
    void updateOneCausedByPeriodChange(Integer id);

    //单一删除
    @Delete("DELETE FROM `article_classification` WHERE id=#{id} LIMIT 1")
    void deleteOneClassify(int id);

    //批量删除
    void deleteAllClassify(List<Integer> idList);


    /*
    * 特殊：查询全部
    * */

    @Select("SELECT `id`,`classify_name`,`classify_type`,`article_num`,`classify_rank`,`upper_classify` " +
            "FROM `article_classification` " +
            "WHERE `classify_type`='SECTION' OR `classify_type`='PROJECT' " +
            "ORDER BY `classify_type` ASC,`upper_classify` ASC,`classify_rank` ASC"
    )
    List<ClassificationSimpleData> getAllClassification();

}
