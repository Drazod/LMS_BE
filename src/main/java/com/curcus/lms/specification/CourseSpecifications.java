package com.curcus.lms.specification;
import com.curcus.lms.model.entity.CourseStatus;
import org.springframework.data.jpa.domain.Specification;
import com.curcus.lms.model.entity.Course;
public class CourseSpecifications {
	public static Specification<Course> hasInstructorId(Long instructorId) {
        return (root, query, criteriaBuilder) ->
                instructorId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("instructor").get("id"), instructorId);
    }

    public static Specification<Course> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Course> hasTitleLike(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null || title.isBlank()) {
                return criteriaBuilder.conjunction();
            } else {
                String titlePattern = "%" + title.toLowerCase() + "%";
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), titlePattern);
            }
        };
    }

    public static Specification<Course> hasPriceGreaterThanOrEqualTo(Long price) {
        return (root, query, criteriaBuilder) ->
                price == null ? criteriaBuilder.conjunction() : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price);
    }

    public static Specification<Course> hasPriceLowerThanOrEqualTo(Long price) {
        return (root, query, criteriaBuilder) ->
                price == null ? criteriaBuilder.conjunction() : criteriaBuilder.lessThanOrEqualTo(root.get("price"), price);
    }
    public static Specification<Course> hasStatus(CourseStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("status"), status);
    }
    public static Specification<Course> isFree(Boolean isFree) {
        return (root, query, criteriaBuilder) -> {
            if (isFree == null) {
                return criteriaBuilder.conjunction();
            } else if (isFree) {
                return criteriaBuilder.equal(root.get("price"), 0);
            } else {
                return criteriaBuilder.notEqual(root.get("price"), 0);
            }
        };
    }
}
