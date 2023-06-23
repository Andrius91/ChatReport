package icu.yogurt.chatreport.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class CRCommand {
    private final String command;
    private final String usage;
    private final String permission;
    private final List<String> aliases;
}
