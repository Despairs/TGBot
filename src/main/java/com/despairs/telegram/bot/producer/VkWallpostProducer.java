/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.producer;

import com.despairs.telegram.bot.db.repo.ProcessedReferenceRepository;
import com.despairs.telegram.bot.db.repo.SettingsRepository;
import com.despairs.telegram.bot.db.repo.impl.ProcessedReferenceRepositoryImpl;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.Settings;
import com.despairs.telegram.bot.model.TGMessage;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public class VkWallpostProducer implements MessageProducer {

    private final String producerId;

    private static final String VIDEO_LINK_PATTERN = "https://vk.com/video%d_%d";

    private final VkApiClient vk = new VkApiClient(new HttpTransportClient());

    private final String person;
    private final UserActor actor;

    private final SettingsRepository settings = SettingsRepositoryImpl.getInstance();
    private final ProcessedReferenceRepository references = ProcessedReferenceRepositoryImpl.getInstance();

    public VkWallpostProducer(String person) throws SQLException {
        this.person = person;
        producerId = "VK_" + person;
        actor = new UserActor(settings.getValueN(Settings.VK_USER_ID).intValue(), settings.getValueV(Settings.VK_TOKEN));
    }

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        GetResponse lastPost = getPostsResponse();
        Integer currentCount = lastPost.getCount();
        Integer isPinned = 0;
        if (!lastPost.getItems().isEmpty() && lastPost.getItems().get(0).getIsPinned() != null) {
            isPinned = lastPost.getItems().get(0).getIsPinned();
        }
        int lastPostNumber = 0;
        String lastReference = references.getLastReference(producerId);
        if (lastReference != null && !lastReference.isEmpty()) {
            lastPostNumber = Integer.parseInt(lastReference);
        }
        int count = currentCount - lastPostNumber;
        if (count > 0) {
            GetResponse response = getPostsResponse(count, isPinned);
            if (response.getItems() != null) {
                response.getItems().forEach(post -> {
                    if (post.getCopyHistory() != null) {
                        post.getCopyHistory().forEach(p -> ret.addAll(convertWallpost(p)));
                    } else {
                        ret.addAll(convertWallpost(post));
                    }
                });
                references.storeReference(String.valueOf(currentCount), producerId);
            }
        }
        return ret;
    }

    private List<TGMessage> convertWallpost(Wallpost post) {
        List<TGMessage> ret = new ArrayList<>();
        TGMessage baseMessage = new TGMessage(MessageType.TEXT);
        baseMessage.setText(post.getText());
        if (post.getAttachments() != null) {
            int attachCount = post.getAttachments().size();
            post.getAttachments().forEach(attach -> {
                switch (attach.getType()) {
                    case LINK:
                        if (attachCount > 1) {
                            TGMessage linkMessage = new TGMessage(MessageType.TEXT);
                            linkMessage.setLink(attach.getLink().getUrl());
                            linkMessage.setRef(baseMessage);
                            ret.add(linkMessage);
                        } else {
                            baseMessage.setLink(attach.getLink().getUrl());
                            ret.add(baseMessage);
                        }
                        break;
                    case PHOTO:
                        if (attachCount > 1 || (baseMessage.getText() != null && baseMessage.getText().length() > 200)) {
                            TGMessage photoMessage = new TGMessage(MessageType.PHOTO);
                            photoMessage.setLink(getBestPhotoSize(attach.getPhoto()));
                            photoMessage.setRef(baseMessage);
                            ret.add(photoMessage);
                        } else {
                            baseMessage.setLink(getBestPhotoSize(attach.getPhoto()));
                            baseMessage.setType(MessageType.PHOTO);
                            ret.add(baseMessage);
                        }
                        break;
                    case DOC:
                        if (attachCount > 1 || (baseMessage.getText() != null && baseMessage.getText().length() > 200)) {
                            TGMessage docMessage = new TGMessage(MessageType.DOCUMENT);
                            docMessage.setLink(attach.getDoc().getUrl());
                            docMessage.setRef(baseMessage);
                            ret.add(docMessage);
                        } else {
                            baseMessage.setLink(attach.getDoc().getUrl());
                            baseMessage.setType(MessageType.DOCUMENT);
                            ret.add(baseMessage);
                        }
                        break;
                    case VIDEO:
                        if (attachCount > 1) {
                            TGMessage videoMessage = new TGMessage(MessageType.VIDEO);
                            videoMessage.setLink(String.format(VIDEO_LINK_PATTERN, post.getOwnerId(), attach.getVideo().getId()));
                            videoMessage.setRef(baseMessage);
                            ret.add(videoMessage);
                        } else {
                            baseMessage.setLink(String.format(VIDEO_LINK_PATTERN, post.getOwnerId(), attach.getVideo().getId()));
                            ret.add(baseMessage);
                        }
                        break;
                }
            });
        } else {
            baseMessage.setType(MessageType.TEXT);
            ret.add(baseMessage);
        }
        return ret;
    }

    private String getBestPhotoSize(Photo photo) {
        String ret = photo.getPhoto604();
        if (photo.getPhoto1280() != null) {
            ret = photo.getPhoto1280();
        } else if (photo.getPhoto807() != null) {
            ret = photo.getPhoto807();
        }
        return ret;
    }

    private GetResponse getPostsResponse() throws ApiException, ClientException {
        return getPostsResponse(1, 0);
    }

    private GetResponse getPostsResponse(int count, int offset) throws ApiException, ClientException {
        return vk.wall().get(actor)
                .domain(person)
                .offset(offset)
                .count(count)
                .execute();
    }

}
