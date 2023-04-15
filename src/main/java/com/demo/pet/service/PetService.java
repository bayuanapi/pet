package com.demo.pet.service;

import org.springframework.web.multipart.MultipartFile;

import com.demo.pet.model.reponse.BaseResponse;
import com.demo.pet.model.request.PetRequest;
import com.demo.pet.model.request.Status;

public interface PetService {

	BaseResponse addPet(PetRequest request);

	BaseResponse getPetById(String id);

	BaseResponse deletePet(String id);

	BaseResponse updatePet(PetRequest request);

	BaseResponse findByStatus(Status status);

	BaseResponse updatePetForm(String id, String name, Status status);

	BaseResponse uploadImage(String id, MultipartFile file, String additionalMetadata);

}
