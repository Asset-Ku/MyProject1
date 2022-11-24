package com.example.demo.entity.dto;

import com.example.demo.entity.Message;
import com.example.demo.entity.Users;
import com.example.demo.entity.util.MessageHelper;
import lombok.Data;

@Data
public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private Users author;
    private String filename;
    private Long likes;
    private Boolean meLiked;

    public MessageDto(Message message, Long likes, Boolean meLiked) {
        this.id = message.getId();
        this.text = message.getText();
        this.tag = message.getTag();
        this.author = message.getAuthor();
        this.filename = message.getFilename();
        this.likes = likes;
        this.meLiked = meLiked;
    }

    public String getAuthorName() {
        return MessageHelper.getAuthorName(author);
    }
}
