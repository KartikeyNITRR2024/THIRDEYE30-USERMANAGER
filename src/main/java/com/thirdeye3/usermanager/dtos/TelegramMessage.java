package com.thirdeye3.usermanager.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TelegramMessage {
	private String chatId;
	private String chatName;
	private List<String> chats = new ArrayList<>();
}


