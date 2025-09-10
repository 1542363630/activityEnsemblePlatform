package welfare.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.FileTypeEnum;
import welfare.system.model.ENUM.FileUsageTypeEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@TableName("file_resource")
public class FileResource {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private FileTypeEnum fileType = FileTypeEnum.IMAGE;
    private String fileName;
    private Date uploadTime;
    private int uploadUid;
    private FileUsageTypeEnum usageType;
    private int status = 1;

    public FileResource(FileTypeEnum fileType, String imageName, int uploadUid, FileUsageTypeEnum usageType) {
        this.fileType = fileType;
        this.fileName = imageName;
        this.uploadUid = uploadUid;
        this.usageType = usageType;
    }

    public Map<String,Object> toReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("id",id);
        returnMap.put("URL",getURL());
        returnMap.put("uploadTime",uploadTime);
        returnMap.put("uploadUid",uploadUid);
        return returnMap;
    }

    public Map<String,Object> toImportantReturnMap() {
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("id",id);
        returnMap.put("fileName",fileName);
        returnMap.put("fileType",fileType);
        returnMap.put("uploadTime",uploadTime);
        return returnMap;
    }

    @SuppressWarnings("unused")
    public void setUsageType(int typeId) {
        usageType = FileUsageTypeEnum.getType(typeId);
    }

    public String getURL() {
        return VALUE.web_path + fileType.web_path + fileName;
    }

    @JsonIgnore
    public String getLocalPath() {
        return fileType.local_path + fileName;
    }

}
