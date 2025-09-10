package welfare.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import welfare.system.model.po.FileResource;

import java.util.Date;
import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<FileResource> {

    //存入文件，返回文件id
    @Select("INSERT INTO `file_resource`(`file_type`,`file_name`,`upload_uid`,`usage_type`) " +
            "VALUES(#{fileType},#{fileName},#{uploadUid},#{usageType.id});" +
            "SELECT LAST_INSERT_ID()"
    )
    int uploadFile(FileResource fileResource);

    //通过文件id找到文件名
    @Select("SELECT `file_name` FROM `file_resource` WHERE `id`=#{id}")
    String getFileNameById(int id);

    // 通过文章id返回文件
    @Select("SELECT * FROM `file_resource` WHERE `id`=#{id}")
    FileResource getFileById(int id);


    // 记录文件引用情况
    void recordFileQuote(int articleId, Integer[] fileIdList);

    // 根据文章id找到对应的文件
    List<Integer> getFileIdByArticleId(int articleId);


    /*
    * 有关文件的查询
    * */

    //查询某段时间前某状态的文件
    @Select("SELECT * FROM `file_resource` WHERE `status`=#{status} AND `upload_time`<#{date}")
    List<FileResource> getFileByStatusBeforeDate(int status, Date date);

    /*
    * 删除
    * */

    //删除单条记录
    @Delete("DELETE FROM `file_resource` WHERE `id`=#{id}")
    void removeOneUnusedData(int id);

    //删除多条记录
    @Delete("DELETE FROM `file_resource` WHERE `id` IN ${idList}")
    void removeAllUnusedData(String idList);

    //标记多条记录为未删除成功
    @Update("UPDATE `file_resource` SET `status`=2 WHERE `id` IN ${idList}")
    void signAllUndeletedData(String idList);

    /*
    * 有关首页轮播图片接口
    * */

    //查看轮播图片
    @Select("SELECT `id`,`file_name`,`upload_time`,`upload_uid` " +
            "FROM `file_resource` " +
            "WHERE `id` IN (" +
                "WITH RECURSIVE A AS (" +
                    "SELECT `photo_id`,`next` FROM `carousel_photo` WHERE `photo_id`=1 " +

                    "UNION ALL " +

                    "SELECT B.`photo_id`,B.`next` FROM `carousel_photo` B JOIN A C ON B.`photo_id`=C.`next` WHERE B.`next`<>1" +
                ") " +
                "SELECT `next` FROM A" +
            ");"
    )
    List<FileResource> getCarouselPhoto();

    //获取该id轮播图片的下一张图片id
    @Select("SELECT `next` FROM `carousel_photo` WHERE `photo_id`=#{id}")
    Integer getNextCarouselPhotoId(int id);

    //上传新轮播图片
    @Insert("DELETE FROM `carousel_photo` WHERE `photo_id`=#{last};" +
            "INSERT INTO `carousel_photo`(`photo_id`,`next`) VALUES(#{last},#{id}),(#{id},#{next});"
    )
    void addCarouselPhoto(int id,int last,int next);

    @Delete("DELETE FROM `carousel_photo` WHERE `photo_id`=#{id};" +
            "UPDATE `carousel_photo` SET `next`=#{next} WHERE `next`=#{id};"
    )
    void deleteCarouselPhoto(int id,int next);

}