package com.hongseonah.costmanager.domain.project.service;

import com.hongseonah.costmanager.domain.project.dto.request.ProjectCreateRequest;
import com.hongseonah.costmanager.domain.project.dto.request.ProjectUpdateRequest;
import com.hongseonah.costmanager.domain.project.dto.response.ProjectResponse;
import com.hongseonah.costmanager.common.response.PageResponse;
import java.util.List;

public interface ProjectService {

    PageResponse<ProjectResponse> findAll(Long businessUnitId, int page, int size);

    ProjectResponse findById(Long id);

    ProjectResponse create(ProjectCreateRequest request);

    ProjectResponse update(Long id, ProjectUpdateRequest request);

    void delete(Long id);
}
