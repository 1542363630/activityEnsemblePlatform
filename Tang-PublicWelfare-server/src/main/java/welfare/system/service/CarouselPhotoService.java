package welfare.system.service;

import org.springframework.stereotype.Service;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.po.FileResource;
import welfare.system.model.vo.CommonErr;
import welfare.system.model.vo.Response;

@Service
public class CarouselPhotoService {

    public Response getPosterShowOnTop() {
        return Response.success(
                MAPPER.file.getCarouselPhoto()
                        .stream()
                        .map(FileResource::toReturnMap)
        );
    }

    public Response addPosterShowOnTop(Integer id,Integer last) {
        if (id == null || last == null || last < 1 || id <= 1) {
            return Response.failure(CommonErr.POST_CHECK_FAILED.setMsg("请正确上传图片!"));
        }
        //取出lastId的图片的下一个图片id
        Integer next = MAPPER.file.getNextCarouselPhotoId(last);
        if (next == null) {
            return Response.failure(CommonErr.POST_CHECK_FAILED.setMsg("请正确上传图片!"));
        }
        //更新链表关系
        MAPPER.file.addCarouselPhoto(id,last,next);
        return Response.ok();
    }

    public Response deletePosterShowOnTop(Integer id) {
        if (id == null || id <= 1) {
            return Response.failure(CommonErr.POST_CHECK_FAILED.setMsg("请正确选择需要删除的图片!"));
        }
        //取出id的图片的下一个图片id
        Integer next = MAPPER.file.getNextCarouselPhotoId(id);
        if (next == null) {
            return Response.failure(CommonErr.POST_CHECK_FAILED.setMsg("请正确选择需要删除的图片!"));
        }
        //更新链表关系
        MAPPER.file.deleteCarouselPhoto(id,next);
        return Response.ok();
    }

}
