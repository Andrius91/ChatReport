package icu.yogurt.chatreport.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class Staff {

    private final Integer staffId;
    private final String username;
}
