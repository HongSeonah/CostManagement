package com.hongseonah.costmanager.domain.project.service;

import com.hongseonah.costmanager.domain.project.dto.request.ProjectCreateRequest;
import com.hongseonah.costmanager.domain.project.dto.request.ProjectUpdateRequest;
import com.hongseonah.costmanager.domain.project.dto.response.ProjectResponse;
import java.util.List;

public interface ProjectService {

    List<ProjectResponse> findAll(Long businessUnitId);

    ProjectResponse findById(Long id);

    ProjectResponse create(ProjectCreateRequest request);

    ProjectResponse update(Long id, ProjectUpdateRequest request);

    void delete(Long id);
}
