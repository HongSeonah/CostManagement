package com.hongseonah.costmanager.domain.project.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.common.response.PageResponse;
import com.hongseonah.costmanager.domain.project.dto.request.ProjectCreateRequest;
import com.hongseonah.costmanager.domain.project.dto.request.ProjectUpdateRequest;
import com.hongseonah.costmanager.domain.project.dto.response.ProjectResponse;
import com.hongseonah.costmanager.domain.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ApiResponse<PageResponse<ProjectResponse>> findAll(@RequestParam(required = false) Long businessUnitId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(projectService.findAll(businessUnitId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(projectService.findById(id));
    }

    @PostMapping
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ApiResponse.success("프로젝트를 등록했습니다.", projectService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProjectResponse> update(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequest request) {
        return ApiResponse.success("프로젝트를 수정했습니다.", projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ApiResponse.<Void>success("프로젝트를 삭제했습니다.", null);
    }
}
