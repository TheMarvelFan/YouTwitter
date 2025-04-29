package com.mthree.backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mthree.backend.models.Dashboard;
import com.mthree.backend.models.Video;
import com.mthree.backend.services.interfaces.DashboardService;
import com.mthree.backend.utils.ErrorType;
import com.mthree.backend.utils.ResponseType;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ResponseType<Dashboard>> getAccountStats() {
        Dashboard dashboard = dashboardService.getUserStats();

        if (dashboard == null) {
            throw new ErrorType(
                    404,
                    "Dashboard not found"
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Dashboard fetched successfully",
                        dashboard,
                        200
                )
        );
    }

    @GetMapping("/videos")
    public ResponseEntity<ResponseType<Page<Video>>> getVideos(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc", required = false) String sort
    ) {
        Page<Video> videos = dashboardService.getVideos(page, size, sort);

        if (videos == null) {
            return ResponseEntity.ok(
                    new ResponseType<>(
                            "Videos fetched successfully",
                            null,
                            200
                    )
            );
        }

        return ResponseEntity.ok(
                new ResponseType<>(
                        "Videos fetched successfully",
                        videos,
                        200
                )
        );
    }
}
