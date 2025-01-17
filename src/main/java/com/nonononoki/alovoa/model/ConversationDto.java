package com.nonononoki.alovoa.model;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nonononoki.alovoa.component.TextEncryptorConverter;
import com.nonononoki.alovoa.entity.User;
import com.nonononoki.alovoa.entity.user.Conversation;
import com.nonononoki.alovoa.entity.user.Message;

import lombok.Data;

@Data
public class ConversationDto {

	@JsonIgnore
	private long id;
	
	private Date lastUpdated;
	private String userName;
	private String userProfilePicture;
	private Message lastMessage;
	private String userIdEncoded;
	private boolean read;

	public static ConversationDto conversationToDto(Conversation c, User currentUser,
			TextEncryptorConverter textEncryptor)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
		ConversationDto dto = new ConversationDto();
		dto.setId(c.getId());
		dto.setLastUpdated(c.getLastUpdated());
		if(c.getMessages() != null && !c.getMessages().isEmpty()) {
			dto.setLastMessage(c.getMessages().get(c.getMessages().size()-1));
		}
		User u = c.getPartner(currentUser);
		dto.setUserName(u.getFirstName());
		if (u.getProfilePicture() != null) {
			dto.setUserProfilePicture(u.getProfilePicture().getData());
		}
		dto.setUserIdEncoded(UserDto.encodeId(u.getId(), textEncryptor));
		return dto;
	}
}
