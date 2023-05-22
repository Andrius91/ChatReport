package icu.yogurt.common.model;

import icu.yogurt.common.model.enums.PunishStatus;
import icu.yogurt.common.model.enums.PunishType;
import icu.yogurt.common.model.enums.PunishUnit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Punishment {

    private Integer punishmentId;
    private Report report;
    private PunishType type;
    private Staff staff;
    private String target;
    private String reason;
    private PunishStatus status;
    private Integer timeValue;
    private PunishUnit timeUnit;

}
