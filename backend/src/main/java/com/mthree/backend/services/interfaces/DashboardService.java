package com.mthree.backend.services.interfaces;

import org.springframework.data.domain.Page;

import com.mthree.backend.models.Dashboard;
import com.mthree.backend.models.Video;

public interface DashboardService {
    Page<Video> getVideos(int page, int size, String sort);

    Dashboard getUserStats();
}
