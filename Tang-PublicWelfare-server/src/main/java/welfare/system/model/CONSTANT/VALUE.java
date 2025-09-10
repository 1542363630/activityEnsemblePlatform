package welfare.system.model.CONSTANT;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VALUE implements InitializingBean {

    //用于提供yml文件中的各种value
    @Override
    public void afterPropertiesSet() {
        web_path = WEB_PATH;

        default_avatar = DEFAULT_AVATAR_NAME;
        default_photo = DEFAULT_PHOTO_NAME;

        img_web = IMAGE_WEB_PATH;
        img_local = IMAGE_LOCAL_PATH;

        video_web = VIDEO_WEB_PATH;
        video_local = VIDEO_LOCAL_PATH;

        audio_web = AUDIO_WEB_PATH;
        audio_local = AUDIO_LOCAL_PATH;
    }

    @Value("${path.web-path}")
    protected String WEB_PATH;
    public static String web_path;  //网址

    @Value("${path.default-avatar-name}")
    protected String DEFAULT_AVATAR_NAME;
    public static String default_avatar;  //默认头像名

    @Value("${path.default-photo-name}")
    protected String DEFAULT_PHOTO_NAME;
    public static String default_photo;  //默认图片名

    @Value("${path.image-web-path}")
    protected String IMAGE_WEB_PATH;
    public static String img_web;

    @Value("${path.image-local-path}")
    protected String IMAGE_LOCAL_PATH;
    public static String img_local;

    @Value("${path.video-web-path}")
    protected String VIDEO_WEB_PATH;
    public static String video_web;

    @Value("${path.video-local-path}")
    protected String VIDEO_LOCAL_PATH;
    public static String video_local;

    @Value("${path.audio-web-path}")
    protected String AUDIO_WEB_PATH;
    public static String audio_web;

    @Value("${path.audio-local-path}")
    protected String AUDIO_LOCAL_PATH;
    public static String audio_local;

}
