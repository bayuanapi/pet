package com.demo.pet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.demo.pet.model.reponse.BaseResponse;
import com.demo.pet.model.request.PetRequest;
import com.demo.pet.model.request.Status;
import com.demo.pet.service.PetService;

@RestController
@RequestMapping("/pet")
public class PetController {

	@Autowired
	private PetService petService;

	@PostMapping("")
	public BaseResponse addPet(@RequestBody PetRequest request) {
		return petService.addPet(request);
	}

	@PutMapping("")
	public BaseResponse updatePet(@RequestBody PetRequest request) {
		return petService.updatePet(request);
	}

	@GetMapping("/{id}")
	public BaseResponse getPet(@PathVariable("id") String id) {
		return petService.getPetById(id);
	}

	@DeleteMapping("/{id}")
	public BaseResponse deletePet(@PathVariable("id") String id) {
		return petService.deletePet(id);
	}

	@GetMapping("/findByStatus")
	public BaseResponse findByStatus(@RequestParam(value = "status") Status status) {
		return petService.findByStatus(status);
	}

	@PostMapping(path = "/update/{id}", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public BaseResponse updatePetForm(@PathVariable(value = "id") String id,
			@RequestParam(value = "status", required = false) Status status,
			@RequestParam(value = "name", required = false) String name) {
		return petService.updatePetForm(id, name, status);
	}

	@PostMapping("/upload-image/{id}")
	public BaseResponse uploadImage(@RequestParam("file") MultipartFile file, @PathVariable(value = "id") String id,
			@RequestParam(value = "additionalMetadata", required = false) String additionalMetadata) {
		return petService.uploadImage(id, file, additionalMetadata);
	}

}
