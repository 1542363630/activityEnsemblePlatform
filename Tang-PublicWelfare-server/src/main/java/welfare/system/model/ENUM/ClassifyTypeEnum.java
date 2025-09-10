package welfare.system.model.ENUM;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ClassifyTypeEnum {
    ARTICLE,SECTION,PROJECT(SECTION),PERIOD,TOPIC,OTHER;

    private ClassifyTypeEnum upper;
}
