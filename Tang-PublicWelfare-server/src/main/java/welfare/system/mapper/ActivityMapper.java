package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import welfare.system.model.po.Activity;
import welfare.system.model.po.ActivityInfo;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    //发布活动
    @Select("INSERT INTO `activity`(" +
                "`article_id`," +
                "`project_id`," +
                "`register_start_time`," +
                "`register_end_time`," +
                "status" +
            ") " +
            "VALUES(" +
                "#{articleId}," +
                "#{projectId}," +
                "#{registerStartTime}," +
                "#{registerEndTime}," +
                "#{status} " +
            ");" +
            "INSERT INTO `activity_info`(" +
                "`id`," +
                "`activity_address`," +
                "`volunteer_duration`," +
                "`activity_date`," +
                "`quota`," +
                "`register_num`, " +
                "`contact_way`" +
            ") " +
            "VALUES(" +
                "LAST_INSERT_ID()," +
                "#{activityAddress}," +
                "#{volunteerDuration}," +
                "#{activityDate}," +
                "#{quota}," +
                "#{registerNum}," +
                "#{contactWay}" +
            ");" +
            "SELECT LAST_INSERT_ID();"
    )
    int postActivity(ActivityInfo activityInfo);

    //获取个人活动列表(分页)
    @Select("SELECT " +
                "A.`activity_id`," +
                "C.`title`," +
                "D.`file_name` AS coverURL," +
                "C.`introduction`," +
                "B.`register_end_time`<CURRENT_DATE AS ongoing," +
                "E.`activity_address`," +
                "E.`activity_date`," +
                "F.`classify_name` " +
            "FROM `activity_register` A " +
            "JOIN `activity` B ON B.`id`=A.`activity_id` " +
            "JOIN `article` C ON C.`id`=B.`article_id` " +
            "JOIN `file_resource` D ON D.`id`=C.`cover` " +
            "JOIN `activity_info` E ON E.`id`=B.`id` " +
            "JOIN `article_classification` F ON F.`id`=B.`project_id` " +
            "WHERE A.`status`=0 AND A.`uid`=#{uid} AND B.`status`=0 "+
            "ORDER BY `register_time` DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}"
    )
    List<Map<String,Object>> selectActivityByUserId(Integer uid, Integer pageSize, Integer offset);

    //获取个人活动列表数据数
    @Select("SELECT COUNT(*) " +
            "FROM `activity_register` A " +
            "JOIN `activity` B " +
            "ON B.`id`=A.`activity_id` " +
            "WHERE A.`status`=0 AND A.`uid`=#{uid} AND B.`status`=0"
    )
    Integer selectNumberOfActivityByUid(Integer uid);

    //获取活动首页预览列表
    @Select("SELECT " +
                "A.`id`," +
                "A.`register_start_time` AS startTime," +
                "A.`register_end_time` AS endTime," +
                "A.`register_end_time`<CURRENT_DATE AS ongoing," +
                "C.`title`," +
                "D.`file_name` AS coverURL," +
                "C.`introduction`," +
                "B.`classify_name` AS projectName," +
                "A.`project_id` AS projectId," +
                "E.`activity_address`," +
                "E.`activity_date` " +
            "FROM `activity` AS A " +
            "JOIN `article_classification` B ON B.`id`=A.`project_id` " +
            "JOIN `article` C ON A.`article_id`=C.`id` " +
            "LEFT JOIN `file_resource` D ON C.`cover`=D.`id` " +
            "JOIN `activity_info` E ON E.`id`=A.`id` " +
            "WHERE A.`register_end_time`>CURRENT_DATE AND A.`status`=0 " +
            "ORDER BY C.`launch_time` DESC " +
            "LIMIT ${num}"
    )
    List<Map<String,Object>> selectPreviewActivity(int num);

    //获取报名未结束活动列表，按板块分类
    @Select("SELECT " +
                "A.`id`," +
                "A.`register_start_time` AS startTime," +
                "A.`register_end_time` AS endTime," +
                "C.`title`," +
                "D.`file_name` AS coverURL," +
                "C.`introduction`," +
                "B.`classify_name` AS projectName," +
                "A.`project_id` AS projectId," +
                "E.`activity_address`," +
                "E.`activity_date` " +
            "FROM `activity` AS A " +
            "JOIN `article_classification` B ON B.`id`=A.`project_id` " +
            "JOIN `article` C ON A.`article_id`=C.`id` " +
            "LEFT JOIN `file_resource` D ON C.`cover`=D.`id` " +
            "JOIN `activity_info` E ON E.`id`=A.`id` " +
            "WHERE A.`register_end_time`>current_date AND A.`status`=0 AND B.`upper_classify`=#{sectionId} " +
            "ORDER BY B.`classify_rank` ASC,C.`launch_time` DESC "
    )
    List<Map<String,Object>> selectActivityOngoingBySection(int sectionId);

//    //获取报名未结束活动列表（分页）
//    @Select("SELECT * " +
//            "FROM `activity` A " +
//            "JOIN `activity_info` B " +
//            "ON A.`id` = B.`id` " +
//            "WHERE A.`register_end_time`>=current_date AND A.`status`=0 " +
//            "ORDER BY A.`id` DESC " +
//            "LIMIT #{pageSize} OFFSET #{offset} "
//    )
//    List<ActivityInfo> selectActivityOngoing(Integer pageSize,Integer offset);
//
//    @Select("SELECT COUNT(*) " +
//            "FROM `activity` A " +
//            "JOIN `activity_info` B " +
//            "ON A.`id` = B.`id` " +
//            "WHERE A.`register_end_time`>=current_date AND A.`status` = 0 "
//    )
//    Integer selectNumberOfActivityOngoing();
//
//
//    //获取报名已结束活动列表（分页）
//    @Select("SELECT * " +
//            "FROM `activity` A " +
//            "JOIN `activity_info` B " +
//            "ON A.`id` = B.`id` " +
//            "WHERE A.`register_end_time`>current_date AND A.`status`=0 " +
//            "ORDER BY A.`id` DESC " +
//            "LIMIT #{pageSize} OFFSET #{offset} "
//    )
//    List<ActivityInfo> selectActivityEnd(Integer pageSize,Integer offset);
//
//    @Select("SELECT COUNT(*) " +
//            "FROM `activity` A " +
//            "JOIN `activity_info` B " +
//            "ON A.`id` = B.`id` " +
//            "WHERE A.`register_end_time` > current_date AND A.status = 0 "
//    )
//    Integer selectNumberOfActivityEnd();

    //根据id查看活动
    @Select("SELECT * " +
            "FROM `activity` A " +
            "JOIN `article` B ON A.`article_id`=B.`id` " +
            "JOIN `activity_info` C ON A.`id`=C.`id` " +
            "WHERE A.`id`=#{id}"
    )
    ActivityInfo selectActivityById(Integer id);

    //获取活动筛选（按 关键词，是否进行中，项目类型 进行筛选）后的条数
    @Select("SELECT " +
            "count(*)" +
            "FROM `activity` " +
            "JOIN `article` ON `activity`.`article_id`=`article`.`id` " +
            "JOIN `activity_info` ON `activity_info`.`id`=`activity`.`id` " +
            "${searchSql} "
    )
    Integer selectNumberOfActivityConstrained(String searchSql);

    //活动筛选（按 关键词，是否进行中，项目类型 进行筛选）
    @Select("SELECT " +
                "`activity`.`id`," +
                "`activity`.`register_start_time` AS startTime," +
                "`activity`.`register_end_time` AS endTime," +
                "`article`.`title`," +
                "`file_resource`.`file_name` AS coverURL," +
                "`article`.`introduction`," +
                "`article_classification`.`classify_name` AS projectName," +
                "`activity`.`project_id` AS projectId," +
                "`activity_info`.`activity_address`," +
                "`activity_info`.`activity_date` " +
            "FROM `activity` " +
            "JOIN `article_classification` ON `article_classification`.`id`= `activity`.`project_id` " +
            "JOIN `article` ON `activity`.`article_id`=`article`.`id` " +
            "LEFT JOIN `file_resource` ON `article`.`cover`=`file_resource`.`id` " +
            "JOIN `activity_info` ON `activity_info`.`id`=`activity`.`id` " +
            " ${searchSql} " +
            "LIMIT #{pageSize} OFFSET #{offset} "
    )
    List<Map<String,Object>> selectActivityConstrained(String searchSql, Integer pageSize, Integer offset);

    //查看某个活动报名情况
    @Select("SELECT `status` FROM `activity_register` WHERE `uid`=#{uid} AND `activity_id`=#{id} LIMIT 1")
    Integer getRegisterStatus(int uid,int id);

    //申请参加活动
    @Insert("INSERT INTO `activity_register`(`uid`,`activity_id`) VALUES(#{uid},#{id})")
    void registerActivity(Integer uid,Integer id);

    //根据 id 删除活动
    @Update("UPDATE `activity` SET `status`=1 WHERE `id`=#{id}")
    void deleteActivityById(Integer id);

    //根据id清除活动记录
    @Delete("DELETE FROM `activity` WHERE `id`=#{id}")
    void removeActivityById(int id);


}
