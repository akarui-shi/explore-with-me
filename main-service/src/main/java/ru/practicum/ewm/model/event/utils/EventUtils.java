package ru.practicum.ewm.model.event.utils;

public final class EventUtils {
    private EventUtils() {
    }

    public static final String EVENT_ANNOTATION_EMPTY_ERROR_MESSAGE = "Annotation can't be empty";
    public static final int EVENT_ANNOTATION_MIN_LENGTH = 20;
    public static final int EVENT_ANNOTATION_MAX_LENGTH = 2000;
    public static final String EVENT_ANNOTATION_LENGTH_ERROR_MESSAGE =
            "Annotation must be between" + EVENT_ANNOTATION_MIN_LENGTH + " and " + EVENT_ANNOTATION_MAX_LENGTH + " symbols";

    public static final String EVENT_CATEGORY_EMPTY_ERROR_MESSAGE = "Category can't be empty";
    public static final String EVENT_DESCRIPTION_EMPTY_ERROR_MESSAGE = "Description can't be empty";
    public static final int EVENT_DESCRIPTION_MIN_LENGTH = 20;
    public static final int EVENT_DESCRIPTION_MAX_LENGTH = 7000;
    public static final String EVENT_DESCRIPTION_LENGTH_ERROR_MESSAGE =
            "Description length must be between" + EVENT_DESCRIPTION_MIN_LENGTH + " and " + EVENT_DESCRIPTION_MAX_LENGTH + " symbols";


    public static final String EVENT_DATE_PAST_ERROR_MESSAGE = "Event date must be in future";
    public static final String EVENT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String EVENT_DATE_EMPTY_ERROR_MESSAGE = "Event date can't be empty";

    public static final String EVENT_LOCATION_EMPTY_ERROR_MESSAGE = "Event location can't be empty";

    public static final String EVENT_PARTICIPANT_LIMIT_NEGATIVE_ERROR_MESSAGE = "Event participant limit can't be negative";

    public static final String EVENT_TITLE_EMPTY_ERROR_MESSAGE = "Event title can't be empty";
    public static final int EVENT_TITLE_MIN_LENGTH = 3;
    public static final int EVENT_TITLE_MAX_LENGTH = 120;
    public static final String EVENT_TITLE_LENGTH_ERROR_MESSAGE =
            "Title length must be between" + EVENT_TITLE_MIN_LENGTH + " and " + EVENT_TITLE_MAX_LENGTH + " symbols";
}
