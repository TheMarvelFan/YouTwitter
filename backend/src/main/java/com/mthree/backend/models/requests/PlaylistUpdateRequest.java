package com.mthree.backend.models.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaylistUpdateRequest {
    String name;
    String description;
}
