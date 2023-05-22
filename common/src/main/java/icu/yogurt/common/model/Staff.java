package icu.yogurt.common.model;

import icu.yogurt.common.model.enums.StaffRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class Staff {

    private final Integer staffId;
    private final String username;
    private final StaffRole role;
}
