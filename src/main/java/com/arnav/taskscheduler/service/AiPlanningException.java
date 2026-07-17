package com.arnav.taskscheduler.service;

// Wraps failures from the Claude API call or from parsing its response,
// so controllers can catch this specifically and return a clean error
// instead of a raw 500 with a stack trace.
public class AiPlanningException extends RuntimeException {

    public AiPlanningException(String message) {
        super(message);
    }

    public AiPlanningException(String message, Throwable cause) {
        super(message, cause);
    }
}
