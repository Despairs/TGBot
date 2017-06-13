/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.despairs.telegram.bot.producer;

import com.despairs.telegram.bot.utils.FileUtils;
import com.despairs.telegram.bot.model.MessageType;
import com.despairs.telegram.bot.model.TGMessage;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EKovtunenko
 */
public class VkWallpostProducer implements MessageProducer {

    private static final String FILENAME_PATTERN = "VKWallpostCount_%s.txt";
    private static final String VIDEO_LINK_PATTERN = "https://vk.com/video%d_%d";

    private final VkApiClient vk = new VkApiClient(new HttpTransportClient());

    private final String person;

    public VkWallpostProducer(String person) {
        this.person = person;
    }

    @Override
    public List<TGMessage> produce() throws Exception {
        List<TGMessage> ret = new ArrayList<>();
        GetResponse lastPost = getPostsResponse(1);
        Integer currentCount = lastPost.getCount();
        Integer isPinned = 0;
        if (!lastPost.getItems().isEmpty() && lastPost.getItems().get(0).getIsPinned() != null) {
            isPinned = lastPost.getItems().get(0).getIsPinned();
        }
        int lastPostNumber = 0;
        String s = FileUtils.read(String.format(FILENAME_PATTERN, person));
        if (!s.isEmpty()) {
            lastPostNumber = Integer.parseInt(s);
        }
        int count = currentCount - lastPostNumber;
        if (count > 0) {
            GetResponse response = getPostsResponse(count, isPinned);
            if (response.getItems() != null) {
                response.getItems().stream().forEach(post -> {
                    if (post.getCopyHistory() != null) {
                        post.getCopyHistory().stream().forEach(p -> {
                            ret.add(convertWallpost(p));
                        });
                    } else {
                        ret.add(convertWallpost(post));
                    }
                });
                FileUtils.write(String.valueOf(currentCount), String.format(FILENAME_PATTERN, person), false);
            }
        }
        return ret;
    }

    private TGMessage convertWallpost(Wallpost post) {
        TGMessage ret = new TGMessage();
        ret.setType(MessageType.TEXT);
        ret.setText(post.getText());
        if (post.getAttachments() != null) {
            post.getAttachments().stream().forEach(attach -> {
                switch (attach.getType()) {
                    case LINK:
                        ret.setLink(attach.getLink().getUrl());
                        break;
                    case PHOTO:
                        if (ret.getText() != null && ret.getText().length() > 200) {
                            TGMessage ref = new TGMessage();
                            ref.setType(MessageType.PHOTO);
                            ref.setLink(getBestPhotoSize(attach.getPhoto()));
                            ret.setRef(ref);
                        } else {
                            ret.setLink(getBestPhotoSize(attach.getPhoto()));
                            ret.setType(MessageType.PHOTO);
                        }
                        break;
                    case DOC:
                        if (ret.getText() != null && ret.getText().length() > 200) {
                            TGMessage ref = new TGMessage();
                            ref.setType(MessageType.DOCUMENT);
                            ret.setLink(attach.getDoc().getUrl());
                            ret.setRef(ref);
                        } else {
                            ret.setLink(attach.getDoc().getUrl());
                            ret.setType(MessageType.DOCUMENT);
                        }
                        break;
                    case VIDEO:
                        ret.setLink(String.format(VIDEO_LINK_PATTERN, post.getOwnerId(), attach.getVideo().getId()));
                        break;
                }
            });
        } else {
            ret.setType(MessageType.TEXT);
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

    private GetResponse getPostsResponse(int count) throws ApiException, ClientException {
        return getPostsResponse(count, 0);
    }

    private GetResponse getPostsResponse(int count, int offset) throws ApiException, ClientException {
        return vk.wall().get()
                .domain(person)
                .offset(offset)
                .count(count)
                .execute();
    }

}
