package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import welfare.system.model.dto.page.ArticlePageData;
import welfare.system.model.po.Achievement;

import java.util.List;
import java.util.Map;

@Mapper
public interface AchievementMapper extends BaseMapper<Achievement> {

    // 发布历史成就
    int uploadAchievement(Achievement achievement);

    // 通过 项目 和 时间 分页查询未删除历史成就
    List<Map<String,String>> getAchievementsByProjectAndPeriod(ArticlePageData projectPeriodData);

    // 通过 板块 和 时间 分页查询
    List<Map<String,String>> getAchievementsByProjectsAndPeriod(ArticlePageData projectPeriodData);

    // 通过 板块 分页查询
    List<Map<String,String>> getAchievementsByProjects(ArticlePageData projectPeriodData);

    // 通过 项目 分页查询未删除历史成就
    List<Map<String,String>> getAchievementsByProject(ArticlePageData projectPeriodData);

    // 通过 时间 分页查询未删除历史成就
    List<Map<String,String>> getAchievementsByPeriod(ArticlePageData projectPeriodData);

    // 查询所有未删除历史成就
    List<Map<String,String>> getAchievements(ArticlePageData articlePageData);

    // 获取未删除历史成就总数(status != 2)
    int getAchieveNum(Integer projectId, Integer[] period);

    // 有 projectId 列表
    int getAchieveNumByProjects(Integer[] project, Integer[] period);


    // get top achievements（status = 1）
    List<Map<String,Object>> getTopAchievements();


    // 通过id查找未删除历史成就
    Achievement selectById(int id);



    // 删除历史成就(make status = 2)
    @Update("UPDATE `achievement` SET `status` = 2 where id=#{id}")
    void deleteAchieveById(Integer id);


    //清除历史成就数据
    void removeAchieveById(int id);



    // 展示活动
    void displayAchievement(Integer[] idList);

    // 不展示活动
    void nonDisplayAchievement(Integer[] idList);



    // 搜索成就数
    @Select("SELECT COUNT(*) ${searchSql}")
    int getAchieveSearchNum(String searchSql);

    // 搜索成就
    @Select("SELECT * ${searchSql} LIMIT #{offset},#{num}")
    List<Achievement> searchAchieve(String searchSql, int offset, int num);

}
