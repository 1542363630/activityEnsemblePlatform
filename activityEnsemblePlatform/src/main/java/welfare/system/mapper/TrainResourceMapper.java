package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import welfare.system.model.dto.page.ArticlePageData;
import welfare.system.model.po.TrainResource;

import java.util.List;
import java.util.Map;

@Mapper
public interface TrainResourceMapper extends BaseMapper<TrainResource> {

    @Select("INSERT INTO `train_resource`(`article_id`,`project_id`,`period_id`) " +
            "VALUES(#{articleId},#{projectId},#{periodId});" +
            "SELECT LAST_INSERT_ID();"
    )
    int uploadTrainResource(TrainResource trainResource);

    TrainResource selectById(int id);

    // 根据 项目——时间线 联表分页查询
    List<Map<String, String>> getTrainByProjectAndPeriod(ArticlePageData articlePageData);

    // 按 项目——时间线 获取总数据量
    int getTrainNum(Integer projectId, Integer[] period);

    // 按 板块——时间线 获取总数据量
    int getTrainNumByProjects(Integer[] project, Integer[] period);

    //根据id清除培训资源数据
    @Delete("DELETE FROM `train_resource` WHERE `id`=#{id}")
    void removeTrainResourceById(int id);

    // 假删(make status = 1)
    @Update("UPDATE `train_resource` SET `status` = 1 where id=#{id}")
    void deleteTrainResourceById(Integer id);

    // 搜索成就数
    @Select("SELECT COUNT(*) ${searchSql}")
    int getTrainSearchNum(String searchSql);

    //搜索成就
    @Select("SELECT * ${searchSql} LIMIT #{offset},#{num}")
    List<TrainResource> searchTrain(String searchSql, int offset, int num);
}
