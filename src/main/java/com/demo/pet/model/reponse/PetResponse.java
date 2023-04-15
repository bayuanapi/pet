package com.demo.pet.model.reponse;

import java.util.List;

import com.demo.pet.model.request.PetRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetResponse {

	private Long id;
	private Category category;
	private String name;
	private String[] photoUrls;
	private List<Tag> tags;
	private String status;
	private String code;
	private String type;
	private String message;

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class Category {
		private Long id;
		private String name;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class Tag {
		private Long id;
		private String name;
	}

}
