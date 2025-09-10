package welfare.system.model.ENUM;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import welfare.system.model.CONSTANT.VALUE;

@NoArgsConstructor
@AllArgsConstructor
public enum FileTypeEnum {
    IMAGE(VALUE.img_web,VALUE.img_local),
    VIDEO(VALUE.video_web,VALUE.video_local),
    AUDIO(VALUE.audio_web,VALUE.audio_local),
    OTHER;

    public String web_path,local_path;
}
