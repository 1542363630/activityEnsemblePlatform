package welfare.system.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import welfare.system.model.ENUM.FileUsageTypeEnum;
import welfare.system.model.po.User;
import welfare.system.model.vo.Response;
import welfare.system.service.FileService;

@RestController
@RequestMapping("/upload")
public class FileController {

    @Resource
    FileService fileService;

    @PostMapping("/photo")
    public Response uploadPhoto(HttpServletRequest request, MultipartFile photoFile) {
        return fileService.uploadImg((User) request.getAttribute("user"),photoFile,FileUsageTypeEnum.Article);
    }

    @PostMapping("/cover")
    public Response uploadCover(HttpServletRequest request, MultipartFile photoFile) {
        return fileService.uploadImg((User) request.getAttribute("user"),photoFile,FileUsageTypeEnum.Cover);
    }

    @PostMapping("/classify-cover")
    public Response uploadClassifyCover(HttpServletRequest request, MultipartFile photoFile) {
        return fileService.uploadImg((User) request.getAttribute("user"),photoFile,FileUsageTypeEnum.CLASSIFY_COVER);
    }

    @PostMapping("/carousel-photo")
    public Response uploadCarouselPhoto(HttpServletRequest request, MultipartFile photoFile) {
        return fileService.uploadImg((User) request.getAttribute("user"),photoFile,FileUsageTypeEnum.CAROUSEL_PHOTO);
    }

    @PostMapping("/video")
    public Response uploadVideo(HttpServletRequest request, MultipartFile videoFile) {
        return fileService.uploadVideo((User) request.getAttribute("user"),videoFile);
    }

    @PostMapping("/audio")
    public Response uploadAudio(HttpServletRequest request, MultipartFile audioFile) {
        return fileService.uploadAudio((User) request.getAttribute("user"),audioFile);
    }

}
