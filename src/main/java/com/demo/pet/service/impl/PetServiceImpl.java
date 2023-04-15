package com.demo.pet.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.demo.pet.model.reponse.BaseResponse;
import com.demo.pet.model.reponse.PetResponse;
import com.demo.pet.model.request.PetRequest;
import com.demo.pet.model.request.Status;
import com.demo.pet.service.ApiService;
import com.demo.pet.service.PetService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PetServiceImpl implements PetService {

	@Autowired
	private ApiService apiService;

	@Override
	public BaseResponse addPet(PetRequest request) {
		PetResponse response = apiService.createPet(request);
		Map<String, Long> map = new HashMap<String, Long>();
		map.put("id", response.getId());
		return BaseResponse.createSuccessResponse(map);
	}

	@Override
	public BaseResponse getPetById(String id) {
		PetResponse response = apiService.getPetById(id);
		if ("1".equals(response.getCode())) {
			return BaseResponse.createFailedResponse(1, response.getMessage());
		}
		return BaseResponse.createSuccessResponse(response);
	}

	@Override
	public BaseResponse deletePet(String id) {
		PetResponse response = apiService.deletePet(id);
		if (Objects.isNull(response)) {
			return BaseResponse.createFailedResponse(1, "Data Not Found");
		}
		return BaseResponse.createSuccessResponse();
	}

	@Override
	public BaseResponse updatePet(PetRequest request) {
		return BaseResponse.createSuccessResponse(apiService.updatePet(request));
	}

	@Override
	public BaseResponse findByStatus(Status status) {
		return BaseResponse.createSuccessResponse(apiService.findByStatus(status.name()));
	}

	@Override
	public BaseResponse updatePetForm(String id, String name, Status status) {
		PetResponse response = apiService.updatePetForm(id, name, status);
		if (!"200".equals(response.getCode())) {
			return BaseResponse.createFailedResponse(1, response.getMessage());
		}
		return BaseResponse.createSuccessResponse(response);
	}

	@Override
	public BaseResponse uploadImage(String id, MultipartFile file, String additionalMetadata) {
		return BaseResponse.createSuccessResponse(apiService.uploadImage(id, file, additionalMetadata));
	}

}
