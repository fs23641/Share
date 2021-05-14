package com.example.share;

public class Request {
    String request, description, author, helper, id;
    public Request(String request, String description, String author, String helper, String id) {
        this.request = request;
        this.description = description;
        this.author = author;
        this.helper = helper;
        this.id=id;
    }
}
