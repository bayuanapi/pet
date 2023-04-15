package com.demo.pet.service;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.demo.pet.model.helper.RestTemplateResponseErrorHandler;
import com.demo.pet.model.reponse.PetResponse;
import com.demo.pet.model.request.PetRequest;
import com.demo.pet.model.request.Status;

@Service
public class ApiService {

	@Autowired
	private RestTemplate restTemplate;
	private final String apiPet;
	private final String apiPetById;
	private final String apiPetDelete;
	private final String apiPetUpdate;
	private final String apiFindByStatus;
	private final String apiUpdateForm;
	private final String apiUploadImage;

	@PostConstruct
	public void init() {
		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
	}

	public ApiService(@Value("${api.pet.create}") String urlApiPet,
			@Value("${api.pet.findById}") String urlApiFindPetById, @Value("${api.pet.delete}") String urlApiDeletePet,
			@Value("${api.pet.update}") String urlApiUpdate,
			@Value("${api.pet.findByStatus}") String urlApiFindByStatus,
			@Value("${api.pet.updateForm}") String urlApiUpdateForm,
			@Value("${api.pet.uploadImage}") String urlApiUploadImage) {
		this.apiUploadImage = urlApiUploadImage;
		this.apiUpdateForm = urlApiUpdateForm;
		this.apiPetDelete = urlApiDeletePet;
		this.apiPetById = urlApiFindPetById;
		this.apiPet = urlApiPet;
		this.apiPetUpdate = urlApiUpdate;
		this.apiFindByStatus = urlApiFindByStatus;
	}

	public PetResponse createPet(PetRequest request) {
		HttpEntity<PetRequest> entity = new HttpEntity<>(request);
		String url = apiPet;
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
		return restTemplate.postForEntity(uriComponents.toUri(), entity, PetResponse.class).getBody();
	}

	public PetResponse getPetById(String id) {
		String url = apiPetById + id;
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
		return restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null, PetResponse.class).getBody();
	}

	public PetResponse deletePet(String id) {
		String url = apiPetDelete + id;
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
		return restTemplate.exchange(uriComponents.toUri(), HttpMethod.DELETE, null, PetResponse.class).getBody();

	}

	public PetResponse updatePet(PetRequest request) {
		HttpEntity<PetRequest> entity = new HttpEntity<>(request);
		String url = apiPetUpdate;
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
		return restTemplate.exchange(uriComponents.toUri(), HttpMethod.PUT, entity, PetResponse.class).getBody();
	}

	public List<PetResponse> findByStatus(String status) {
		String url = apiFindByStatus;
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParam("status", status);
		UriComponents uriComponents = uriComponentsBuilder.build();
		return restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null,
				new ParameterizedTypeReference<List<PetResponse>>() {
				}).getBody();
	}

	public PetResponse updatePetForm(String id, String name, Status status) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		if (Objects.nonNull(name)) {
			map.add("name", name);
		}
		if (Objects.nonNull(status)) {
			map.add("status", status.name());
		}
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		String url = apiUpdateForm + id;
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);
		return restTemplate.postForEntity(uriComponents.toUri(), request, PetResponse.class).getBody();
	}

	public PetResponse uploadImage(String id, MultipartFile file, String additionalMetadata) {
		String url = apiUploadImage.replace("{id}", id);
		Resource invoicesResource = file.getResource();
		LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
		parts.add("file", invoicesResource);
		if (Objects.nonNull(additionalMetadata)) {
			parts.add("additionalMetadata", additionalMetadata);
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
		UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build(true);

		HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parts, httpHeaders);
		return restTemplate.postForEntity(uriComponents.toUri(), httpEntity, PetResponse.class).getBody();
	}

}
