package icu.yogurt.chatreport.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserModel {
    private final String username;
    private final String uuid;
    private final String creationDate;
}
