package welfare.system.util;

/*
* 上传文件工具类
* */

import org.springframework.web.multipart.MultipartFile;
import welfare.system.core.exception.CommonErrException;
import welfare.system.model.CONSTANT.MAPPER;
import welfare.system.model.CONSTANT.VALUE;
import welfare.system.model.ENUM.FileTypeEnum;
import welfare.system.model.ENUM.FileUsageTypeEnum;
import welfare.system.model.po.FileResource;
import welfare.system.model.vo.CommonErr;

import java.io.*;
import java.net.URL;
import java.util.*;

public class FileUtil {

    //图片后缀限制范围
    private static final String[] IMAGE_SUFFIX = {
            ".png",
            ".PNG",
            ".jpg",
            ".JPG",
            ".jpeg",
            ".JPEG",
            ".gif",
            ".GIF",
            ".bmp",
            ".BMP"
    };
    //图片大小最大限制(5MB)
    private static final long IMAGE_MAX_SIZE = 5242880L;

    //视频后缀限制范围
    private static final String[] VIDEO_SUFFIX = {
            ".avi",
            ".AVI",
            ".mp4",
            ".MP4",
            ".mkv",
            ".MKV",
            ".wmv",
            ".WMV"
    };
    //视频大小最大限制(100MB)
    private static final long VIDEO_MAX_SIZE = 104857600L;

    private static final String[] AUDIO_SUFFIX = {
            ".mp3",
            ".MP3",
            ".wav",
            ".WAV",
            ".m4a",
            ".M4A",
            ".flac",
            ".FLAC",
            ".Ogg",
            ".ogg"
    };
    //音频大小最大限制(10MB)
    private static final long AUDIO_MAX_SIZE = 10485760L;



    //根据原文件名生成唯一不重复文件名
    private static String generateNewFileName(String originalFileName) {
        return UUID.randomUUID() + originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /*
    * 上传文件的方法
    * 为了防止需求上的分歧
    * 因此不合并
    * */

    //上传图片，返回图片id(web端)
    public static FileResource uploadImage(MultipartFile imageFile, int uid, FileUsageTypeEnum usageType) {
        //判断文件不为null
        if (imageFile == null) throw new RuntimeException("不可上传空文件!");
        //限制图片大小(5MB)
        if (imageFile.getSize() > IMAGE_MAX_SIZE) throw new RuntimeException("文件过大!");
        //获取原文件名
        String originalImgName = imageFile.getOriginalFilename();

        //判断原文件格式正确性
        if (originalImgName != null && Arrays.stream(IMAGE_SUFFIX).anyMatch(originalImgName::endsWith)) {
            //生成新文件名(随机uuid+原文件后缀)
            String newImgName = generateNewFileName(originalImgName);
            //生成新文件路径
            String photoLocalPath = VALUE.img_local + newImgName;
            File newPhotoPath = new File(photoLocalPath);

            //判断文件父目录是否存在
            if (!newPhotoPath.getParentFile().exists() && !newPhotoPath.getParentFile().mkdirs()) {
                throw new RuntimeException("服务器好像开小差了，请再试试!");
            }

            //保存文件
            try {
                imageFile.transferTo(newPhotoPath);
            } catch (IllegalStateException | IOException e) {
                throw new RuntimeException("服务器好像开小差了，请再试试!");
            }

            //将图片名存入数据库
            FileResource image = new FileResource(FileTypeEnum.IMAGE,newImgName,uid,usageType);
            image.setId(MAPPER.file.uploadFile(image));
            return image;
        }
        else throw new CommonErrException(CommonErr.FILE_FORMAT_ERROR.setMsg("图片格式错误!图片格式只能为jpg, jpeg, png, bmp等!"));
    }

    //上传图片，返回图片id(小程序端)
    public static FileResource uploadImage(String imageURL, int uid, FileUsageTypeEnum usageType) {
        try {
            //判断原文件格式正确性
            if (imageURL != null && Arrays.stream(IMAGE_SUFFIX).anyMatch(imageURL::endsWith)) {
                // 开启连接
                URL url = new URL(imageURL);
                InputStream inputStream = url.openStream();

                //生成新文件名(随机uuid+原文件后缀)
                String newImgName = generateNewFileName(imageURL);
                //生成新文件路径
                String photoLocalPath = VALUE.img_local + newImgName;

                //判断文件父目录是否存在
                File newPhotoPath = new File(photoLocalPath);
                if (!newPhotoPath.getParentFile().exists() && !newPhotoPath.getParentFile().mkdirs()) {
                    throw new RuntimeException("服务器好像开小差了，请再试试!");
                }

                // 打开输出流
                FileOutputStream outputStream = new FileOutputStream(photoLocalPath);
                //写入数据
                byte[] buffer = new byte[2048];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                //关闭输入输出流
                inputStream.close();
                outputStream.close();

                //将图片名存入数据库
                FileResource image = new FileResource(FileTypeEnum.IMAGE,newImgName,uid,usageType);
                image.setId(MAPPER.file.uploadFile(image));

                return image;

            }
            else throw new CommonErrException(CommonErr.FILE_FORMAT_ERROR.setMsg("图片格式错误!图片格式只能为jpg, jpeg, png, bmp!"));
        } catch (IOException e) {
            throw new RuntimeException("获取图片失败!");
        }
    }

    //上传视频，返回视频id
    public static FileResource uploadVideo(MultipartFile videoFile, int uid) {
        //判断文件不为null
        if (videoFile == null) throw new RuntimeException("不可上传空文件!");
        //限制视频大小(100MB)
        if (videoFile.getSize() > VIDEO_MAX_SIZE) throw new RuntimeException("文件过大!");
        //获取原文件名
        String originalVideoName = videoFile.getOriginalFilename();

        //判断原文件格式正确性
        if (originalVideoName != null && Arrays.stream(VIDEO_SUFFIX).anyMatch(originalVideoName::endsWith)) {
            //生成新文件名(随机uuid+原文件后缀)
            String newVideoName = generateNewFileName(originalVideoName);
            //生成新文件路径
            String videoLocalPath = VALUE.video_local + newVideoName;
            File newVideoPath = new File(videoLocalPath);

            //判断文件父目录是否存在
            if (!newVideoPath.getParentFile().exists() && !newVideoPath.getParentFile().mkdirs()) {
                throw new RuntimeException("服务器好像开小差了，请再试试!");
            }

            //保存文件
            try {
                videoFile.transferTo(newVideoPath);
            } catch (IllegalStateException | IOException e) {
                throw new RuntimeException("服务器好像开小差了，请再试试!");
            }

            //将视频名存入数据库
            FileResource video = new FileResource(FileTypeEnum.VIDEO, newVideoName,uid,FileUsageTypeEnum.Article);
            video.setId(MAPPER.file.uploadFile(video));
            return video;
        }
        else throw new CommonErrException(CommonErr.FILE_FORMAT_ERROR.setMsg("视频格式错误!视频格式只能为wmv, mp4, mkv!"));
    }

    //上传音频，返回id
    public static FileResource uploadAudio(MultipartFile audioFile, int uid) {
        //判断文件不为null
        if (audioFile == null) throw new RuntimeException("不可上传空文件!");
        //限制视频大小(10MB)
        if (audioFile.getSize() > AUDIO_MAX_SIZE) throw new RuntimeException("文件过大!");
        //获取原文件名
        String originalAudioName = audioFile.getOriginalFilename();

        //判断原文件格式正确性
        if (originalAudioName != null && Arrays.stream(AUDIO_SUFFIX).anyMatch(originalAudioName::endsWith)) {
            //生成新文件名(随机uuid+原文件后缀)
            String newAudioName = generateNewFileName(originalAudioName);
            //生成新文件路径
            String audioLocalPath = VALUE.audio_local + newAudioName;
            File newAudioPath = new File(audioLocalPath);

            //判断文件父目录是否存在
            if (!newAudioPath.getParentFile().exists() && !newAudioPath.getParentFile().mkdirs()) {
                throw new RuntimeException("服务器好像开小差了，请再试试!");
            }

            //保存文件
            try {
                audioFile.transferTo(newAudioPath);
            } catch (IllegalStateException | IOException e) {
                throw new RuntimeException("服务器好像开小差了，请再试试!");
            }

            //将视频名存入数据库
            FileResource audio = new FileResource(FileTypeEnum.AUDIO, newAudioName,uid,FileUsageTypeEnum.Article);
            audio.setId(MAPPER.file.uploadFile(audio));
            return audio;
        }
        else throw new CommonErrException(CommonErr.FILE_FORMAT_ERROR.setMsg("音频格式错误!音频格式只能为mp3, wav, m4a!"));
    }

    //记录文件引用
    public static void recordQuote(int articleId, Integer[] fileIdList) {
        if (fileIdList != null && fileIdList.length != 0) {
            MAPPER.file.recordFileQuote(articleId,fileIdList);
        }
    }

    //删除文件
    public static List<Map<String,Object>> removeFile(Date date) {
        List<FileResource> deleteList = MAPPER.file.getFileByStatusBeforeDate(1,date);
        deleteList.addAll(MAPPER.file.getFileByStatusBeforeDate(2,date));

        if (deleteList.isEmpty()) {
            return null;
        }

        else {
            List<Integer> deleteSuccessIdList = new ArrayList<>();
            List<Integer> deleteUnSuccessIdList = new ArrayList<>();
            List<Map<String,Object>> deleteUnSuccessList = new ArrayList<>();
            for (FileResource file : deleteList) {
                File deletingFile = new File(file.getLocalPath());

                //删除文件
                if (deletingFile.delete()) {
                    //成功删除，加入到成功删除列表
                    deleteSuccessIdList.add(file.getId());
                }
                else {
                    //未成功删除，加入到未成功删除列表
                    if (file.getStatus() == 1) {
                        deleteUnSuccessIdList.add(file.getId());
                    }
                    deleteUnSuccessList.add(file.toImportantReturnMap());
                }
            }
            //将已经成功删除的文件的记录删除
            if (!deleteSuccessIdList.isEmpty()) {
                MAPPER.file.removeAllUnusedData(ArrayUtil.listToString(deleteSuccessIdList));
            }
            //将未成功删除的文件的记录标记保存
            if (!deleteUnSuccessIdList.isEmpty()) {
                MAPPER.file.signAllUndeletedData(ArrayUtil.listToString(deleteUnSuccessIdList));
            }
            //返回未删除成功的文件列表，以便管理员查看
            return deleteUnSuccessList;
        }
    }

}
