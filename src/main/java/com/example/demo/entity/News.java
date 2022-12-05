package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

    public String status;

    private List<Articles> articles = new ArrayList<>();
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Articles {

        public String author;
        public String title;
        public String description;
        public String content;
        public Source source;
        public String url;
        public String urlToImage;
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Source  {
//            public Long id;
            public String name;
        }
    }

}
