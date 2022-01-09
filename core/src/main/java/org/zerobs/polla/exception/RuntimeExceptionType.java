package org.zerobs.polla.exception;

import io.micronaut.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RuntimeExceptionType {
    //never change internal code of existing items
    EXISTING_USER("error.existing.user", 4001),
    EMPTY_USERNAME("error.empty.username", 4002),
    EXISTING_USERNAME("error.existing.username", 4003),
    EMPTY_YEAR_OF_BIRTH("error.empty.year.of.birth", 4004),
    TOO_YOUNG_USER("error.too.young.user", 4005),
    TOO_OLD_USER("error.too.old.user", 4006),
    EMPTY_GENDER("error.empty.gender", 4007),
    EMPTY_USER("error.empty.user", 4008),
    EMPTY_POLL("error.empty.poll", 4009),
    EMPTY_POLL_TITLE("error.empty.poll.title", 4010),
    SMALL_POLL_TITLE("error.small.poll.title", 4011),
    LARGE_POLL_TITLE("error.large.poll.title", 4012),
    LARGE_POLL_DESCRIPTION("error.large.poll.description", 4013),
    EXPIRED_POLL("error.expired.poll", 4014),
    SMALL_PUBLISH_DATE("error.small.publish.date", 4015),
    INVALID_LOCATION_ID("error.invalid.location.id", 4016),
    SMALL_OPTIONS("error.small.options", 4017),
    LARGE_OPTIONS("error.large.options", 4018),
    INVALID_TAG_ID("error.invalid.tag.id", 4019),
    EMPTY_TAG_NAME("error.empty.tag.name", 4020),
    EMPTY_TAG_NAME_INITIALS("error.empty.tag.name.initials", 4021),
    SMALL_TAG_NAME_INITIALS("error.small.tag.name.initials", 4022),
    LARGE_TAGS("error.large.tags", 4023),
    INVALID_USER("error.invalid.user", 4024, HttpStatus.NOT_FOUND),
    NOT_CREATED_USER("error.not.created.user", 4025, HttpStatus.FORBIDDEN),
    FAILED_APPLICATION_INIT("error.failed.application.init", 4026, HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_BATCH_SAVE("error.failed.batch.save", 4027, HttpStatus.INTERNAL_SERVER_ERROR);

    private final String messageTextPropertyId;
    private final int internalCode;
    private final HttpStatus httpStatus;

    RuntimeExceptionType(String messageTextPropertyId, int internalCode) {
        this(messageTextPropertyId, internalCode, HttpStatus.BAD_REQUEST);
    }
}