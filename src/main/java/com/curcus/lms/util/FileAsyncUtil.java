package com.curcus.lms.util;

import java.io.IOException;

import com.curcus.lms.model.entity.Course;
import com.curcus.lms.model.entity.User;
import com.curcus.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.*;
import com.curcus.lms.constants.ContentType;
import com.curcus.lms.exception.InvalidFileTypeException;
import com.curcus.lms.exception.NotFoundException;
import com.curcus.lms.model.entity.Content;
import com.curcus.lms.repository.ContentRepository;
import com.curcus.lms.repository.CourseRepository;
import com.curcus.lms.repository.SectionRepository;
import com.curcus.lms.service.CloudinaryService;
import com.curcus.lms.validation.FileValidation;
@Component
public class FileAsyncUtil {
	@Autowired
    protected CloudinaryService cloudinaryService;
	@Autowired
    protected SectionRepository sectionRepository;
    @Autowired
    protected CourseRepository courseRepository;
    @Autowired
    protected ContentRepository contentRepository;
	@Autowired
	protected UserRepository userRepository;
	@Async
	public void uploadFileAsync(Long contentId, byte[] file) {
	    String url = null;
	    try {
			url=cloudinaryService.uploadFile(file);
	    } catch (IOException e) {
	        // Handle the exception
	    	System.out.println("cloudinary server error");
	    	throw new RuntimeException(e);
	    }
	    
	    updateContentUrl(contentId, url);
	}
	@Async
	public void updateFileAsync(Long contentId, byte[] file, String publicId) {
		try {
			cloudinaryService.updateFile(publicId, file);
		} catch (IOException e) {
			// Handle the exception
			System.out.println("cloudinary server error");
			throw new RuntimeException(e);
		}
	}
	@Async
	public void uploadImageAsync(Long courseId, MultipartFile file) {
		ContentType contentType = getContentType(file);
		String url = null;
		try {
			switch (contentType) {
				case IMAGE:
					url = cloudinaryService.uploadImage(file);
					break;
				default:
					throw new InvalidFileTypeException("Unsupported file type, content for section must be: " + FileValidation.ALLOWED_IMAGE_TYPES);
			}
		} catch (IOException e) {
			// Handle the exception
			System.out.println("cloudinary server error");
			throw new RuntimeException(e);
		}

		updateCourseThumbnail(courseId, url);
	}

	public void updateCourseThumbnail(Long courseId, String url) {
		Course course = courseRepository.findById(courseId).orElseThrow(
				() -> new NotFoundException("Course not found with id " + courseId));
		course.setCourseThumbnail(url);
		courseRepository.save(course);
	}

	@Async
	public void uploadAvatarAsync(Long userId, MultipartFile file) {
		ContentType contentType = getContentType(file);
		String url = null;
		try {
			switch (contentType) {
				case IMAGE:
					url = cloudinaryService.uploadImage(file);
					break;
				default:
					throw new InvalidFileTypeException("Unsupported file type, content for section must be: " + FileValidation.ALLOWED_IMAGE_TYPES);
			}
		} catch (IOException e) {
			// Handle the exception
			System.out.println("cloudinary server error");
			throw new RuntimeException(e);
		}

		updateAvatar(userId, url);
	}
	public void updateAvatar(Long userId, String url) {
		User user = userRepository.findById(userId).orElseThrow(
				() -> new NotFoundException("Course not found with id " + userId));
		user.setAvtUrl(url);
		userRepository.save(user);
	}

	public void updateContentUrl(Long contentId, String url) {
	    Content content = contentRepository.findById(contentId).orElseThrow(
	            () -> new NotFoundException("Content not found with id " + contentId));
	    content.setContent(url);
	    contentRepository.save(content);
	}
	public ContentType getContentType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType.startsWith("video")) {
            return ContentType.VIDEO;
        } else if (contentType.startsWith("image")) {
            return ContentType.IMAGE;
        } else if (contentType.equals("application/pdf") ||
                contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
         return ContentType.DOCUMENT;
        } else {
            return ContentType.UNKNOWN;
        }
    }
	public void validContent(MultipartFile file) {
		ContentType contentType = getContentType(file);
		switch (contentType) {
	        case VIDEO:
	            FileValidation.validateVideoType(file.getOriginalFilename());
	            break;
	        case DOCUMENT:
	        	FileValidation.validateFileType(file.getOriginalFilename());
	            break;
	        default:
	            throw new InvalidFileTypeException("Unsupported file type, content for section must be: "+ FileValidation.ALLOWED_VIDEO_TYPES+FileValidation.ALLOWED_FILE_TYPES);
		}
	}
	public void validImage(MultipartFile file) {
		ContentType contentType = getContentType(file);
		switch (contentType) {
			case IMAGE:
				break;
			default:
				throw new InvalidFileTypeException("Unsupported file type, content for section must be: "+ FileValidation.ALLOWED_IMAGE_TYPES);
		}
	}
	
}
