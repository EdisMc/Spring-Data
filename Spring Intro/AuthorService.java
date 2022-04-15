package com.example.springintro.services;

import com.example.springintro.models.entities.Author;

import java.io.IOException;

public interface AuthorService {
    void seedAuthors() throws IOException;

    Author getRandomAuthor();
}
