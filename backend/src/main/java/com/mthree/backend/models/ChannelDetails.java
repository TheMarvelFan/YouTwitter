package com.mthree.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelDetails {
    long subscribersCount;
    long subscribedToCount;
    boolean isSubscribed;
    String avatar;
    String coverImage;
    String email;
    String userName;
    String fullName;
}
