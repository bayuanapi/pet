package com.demo.pet.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PetRequest {
	
	private Long id;
	private Category category;
	private String name;
	private String[] photoUrls;
	private List<Tag> tags;
	private String status;
	
	
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
