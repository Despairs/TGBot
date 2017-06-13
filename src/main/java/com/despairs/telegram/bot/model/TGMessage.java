/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.model;

/**
 *
 * @author EKovtunenko
 */
public class TGMessage {

    private String link;
    private String text;
    private MessageType type;
    private TGMessage ref;

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

    @Override
    public String toString() {
        return "Message{" + "link=" + link + ", text=" + text + ", type=" + type + ", ref=" + ref + '}';
    }

}
