package com.example.demo.entity.util;

import com.example.demo.entity.Users;

public abstract class MessageHelper {
    public static String getAuthorName(Users author){
        return author != null ? author.getUsername() : "<none>";
    }
}
