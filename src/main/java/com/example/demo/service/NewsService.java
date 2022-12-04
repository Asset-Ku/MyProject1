package com.example.demo.service;


import com.example.demo.entity.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class NewsService {
    @Value("${newsurl}")
    private String newsUrl;

    @Autowired
    private RestTemplate restTemplate;

    public News getSomeNews(){
    return restTemplate.getForObject(newsUrl, News.class);
    }
}
