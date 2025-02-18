package com.sardul3.hireboost.autoview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonProfile {
    private String name;
    private String profileUrl;
    private String jobTitle;
    private String company;
    private String education;
    private String location;
    private int connections;
    private int followers;
    private int mutualConnections;
    private String website;
    private boolean hasRecentPosts;
    private boolean isConnected;
    private double rankScore;

    public PersonProfile(String name, String profileUrl) {
        this.name = name;
        this.profileUrl = profileUrl;
    }
}


