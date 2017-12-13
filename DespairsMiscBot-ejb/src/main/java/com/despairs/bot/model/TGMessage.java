/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.bot.model;

import java.util.Objects;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;

/**
 *
 * @author EKovtunenko
 */
public class TGMessage {

    private String link;
    private String text;
    private MessageType type;
    private TGMessage ref;
    private ReplyKeyboard keyboard;
    private ParseMode parseMode;
    private String chatId;
    private Integer replyTo;

    public TGMessage(MessageType type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public TGMessage getRef() {
        return ref;
    }

    public void setRef(TGMessage ref) {
        this.ref = ref;
    }

    public ReplyKeyboard getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(ReplyKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public ParseMode getParseMode() {
        return parseMode;
    }

    public void setParseMode(ParseMode parseMode) {
        this.parseMode = parseMode;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public Integer getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Integer replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.link);
        hash = 23 * hash + Objects.hashCode(this.text);
        hash = 23 * hash + Objects.hashCode(this.type);
        hash = 23 * hash + Objects.hashCode(this.ref);
        hash = 23 * hash + Objects.hashCode(this.keyboard);
        hash = 23 * hash + Objects.hashCode(this.parseMode);
        hash = 23 * hash + Objects.hashCode(this.chatId);
        hash = 23 * hash + Objects.hashCode(this.replyTo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TGMessage other = (TGMessage) obj;
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.chatId, other.chatId)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.ref, other.ref)) {
            return false;
        }
        if (!Objects.equals(this.keyboard, other.keyboard)) {
            return false;
        }
        if (this.parseMode != other.parseMode) {
            return false;
        }
        if (!Objects.equals(this.replyTo, other.replyTo)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TGMessage{" + "link=" + link + ", text=" + text + ", type=" + type + ", ref=" + ref + ", keyboard=" + keyboard + ", parseMode=" + parseMode + ", chatId=" + chatId + ", replyTo=" + replyTo + '}';
    }

}
