package com.hongseonah.costmanager.domain.internaltransfer.service;

import com.hongseonah.costmanager.domain.internaltransfer.dto.request.InternalTransferCreateRequest;
import com.hongseonah.costmanager.domain.internaltransfer.dto.request.InternalTransferUpdateRequest;
import com.hongseonah.costmanager.domain.internaltransfer.dto.response.InternalTransferResponse;
import java.util.List;

public interface InternalTransferService {

    List<InternalTransferResponse> findAll(String month);
    InternalTransferResponse findById(Long id);
    InternalTransferResponse create(InternalTransferCreateRequest request);
    InternalTransferResponse update(Long id, InternalTransferUpdateRequest request);
    void delete(Long id);
}
