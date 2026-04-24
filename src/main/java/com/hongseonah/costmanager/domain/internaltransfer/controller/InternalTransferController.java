package com.hongseonah.costmanager.domain.internaltransfer.controller;

import com.hongseonah.costmanager.common.response.ApiResponse;
import com.hongseonah.costmanager.domain.internaltransfer.dto.request.InternalTransferCreateRequest;
import com.hongseonah.costmanager.domain.internaltransfer.dto.request.InternalTransferUpdateRequest;
import com.hongseonah.costmanager.domain.internaltransfer.dto.response.InternalTransferResponse;
import com.hongseonah.costmanager.domain.internaltransfer.service.InternalTransferService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/internal-transfers")
public class InternalTransferController {

    private final InternalTransferService transferService;

    public InternalTransferController(InternalTransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    public ApiResponse<List<InternalTransferResponse>> findAll(@RequestParam(required = false) String month) {
        return ApiResponse.success(transferService.findAll(month));
    }

    @GetMapping("/{id}")
    public ApiResponse<InternalTransferResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(transferService.findById(id));
    }

    @PostMapping
    public ApiResponse<InternalTransferResponse> create(@Valid @RequestBody InternalTransferCreateRequest request) {
        return ApiResponse.success("내부대체가액을 등록했습니다.", transferService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<InternalTransferResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody InternalTransferUpdateRequest request) {
        return ApiResponse.success("내부대체가액을 수정했습니다.", transferService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        transferService.delete(id);
        return ApiResponse.<Void>success("내부대체가액을 삭제했습니다.", null);
    }
}
