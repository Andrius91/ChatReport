package icu.yogurt.chatreport.common.model;

import icu.yogurt.chatreport.common.model.enums.PunishStatus;
import icu.yogurt.chatreport.common.model.enums.PunishType;
import icu.yogurt.chatreport.common.model.enums.PunishUnit;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Punishment {

    private Integer punishmentId;
    private ChatReport chatReport;
    private String date;
    private PunishType type;
    private Staff staff;
    private String target;
    private String reason;
    private PunishStatus status;
    private Integer timeValue;
    private PunishUnit timeUnit;

}
