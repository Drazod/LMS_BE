package com.curcus.lms.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSearchOptions {
	public static final List<String> SORT_OPTIONS = Arrays.asList("title", "price", "instructorId", "categoryId", "avgRating", "createdAt");
    public static final List<String> DIRECTION_OPTIONS = Arrays.asList("asc", "desc");
    public static final Map<String, Object> HINTS_MAP;
    
    static {
        HINTS_MAP = new HashMap<>();
        HINTS_MAP.put("sortOptions", SORT_OPTIONS);
        HINTS_MAP.put("directionOptions", DIRECTION_OPTIONS);
    }
}
