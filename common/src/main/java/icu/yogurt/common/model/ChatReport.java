package icu.yogurt.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ChatReport {

    private final String reportId;
    private final String sender;
    private final String target;
    private final List<Message> messages;

}
