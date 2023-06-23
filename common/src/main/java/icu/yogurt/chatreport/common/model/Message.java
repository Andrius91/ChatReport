package icu.yogurt.chatreport.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class Message {

    private final String message;
    private final String server;
    private final String sender;
    private final String date;


    public LocalDateTime getLocalDateTime(){
        return LocalDateTime.parse(date);
    }

    public static String nowDate(){
        return LocalDateTime.now(ZoneOffset.UTC).toString();
    }
}
