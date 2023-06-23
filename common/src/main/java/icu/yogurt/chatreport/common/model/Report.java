package icu.yogurt.chatreport.common.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Report {
    private String sender;
    private String target;
    private String reason;
    private String server;
}
