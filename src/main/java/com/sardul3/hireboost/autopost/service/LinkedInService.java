package com.sardul3.hireboost.autopost.service;

import org.springframework.stereotype.Service;

@Service
public class LinkedInService {

    public void postToLinkedIn(String post) {
        System.out.println("📢 Posted to LinkedIn: " + post);
    }
}
