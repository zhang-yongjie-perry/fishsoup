package com.fishsoup.fishchat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChatUser implements Serializable {
    @Serial
    private static final long serialVersionUID = 4770584233728685071L;

    private String username;

    private String toUsername;

    private String content;

    private String time;
}
