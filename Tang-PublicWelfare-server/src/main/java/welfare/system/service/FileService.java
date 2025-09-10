package welfare.system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import welfare.system.model.ENUM.FileUsageTypeEnum;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.util.FileUtil;

@Service
public class FileService {

    public Response uploadImg(User user, MultipartFile photoFile, FileUsageTypeEnum fileUsageType) {
        return Response.success(FileUtil.uploadImage(photoFile,user.getUid(),fileUsageType));
    }

    public Response uploadVideo(User user, MultipartFile videoFile) {
        return Response.success(FileUtil.uploadVideo(videoFile,user.getUid()));
    }

    public Response uploadAudio(User user, MultipartFile audioFile) {
        return Response.success(FileUtil.uploadAudio(audioFile,user.getUid()));
    }

}
