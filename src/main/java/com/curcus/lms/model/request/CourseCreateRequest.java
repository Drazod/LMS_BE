package com.curcus.lms.model.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
public class CourseCreateRequest {
	@NotNull(message = "course thumbnail cannot be null")
    private MultipartFile courseThumbnail;
	@NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title must be less than 255 characters")
	private String title;
	@Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;
	@NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price must be a positive number")
    private Long price;
    @NotNull(message = "Instructor ID is mandatory")
    private Long instructorId;
    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;
	public CourseCreateRequest(String title, String description, Long price, Long instructorId,
			Long categoryId) {
		super();
		this.title = title;
		this.description = description;
		this.price = price;
		this.instructorId = instructorId;
		this.categoryId = categoryId;
	}
}
