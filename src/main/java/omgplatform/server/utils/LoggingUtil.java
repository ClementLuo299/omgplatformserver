package omgplatform.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Centralized logging utility for the OMG Platform Server.
 * Provides consistent logging methods across the application.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Component
public class LoggingUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Log an informational message
     *
     * @param message the message to log
     */
    public static void info(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.info("[{}] INFO: {}", timestamp, message);
    }
    
    /**
     * Log an informational message with additional context
     *
     * @param message the message to log
     * @param context additional context information
     */
    public static void info(String message, Map<String, Object> context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.info("[{}] INFO: {} | Context: {}", timestamp, message, context);
    }
    
    /**
     * Log a warning message
     *
     * @param message the warning message to log
     */
    public static void warn(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.warn("[{}] WARN: {}", timestamp, message);
    }
    
    /**
     * Log a warning message with additional context
     *
     * @param message the warning message to log
     * @param context additional context information
     */
    public static void warn(String message, Map<String, Object> context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.warn("[{}] WARN: {} | Context: {}", timestamp, message, context);
    }
    
    /**
     * Log an error message
     *
     * @param message the error message to log
     */
    public static void error(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.error("[{}] ERROR: {}", timestamp, message);
    }
    
    /**
     * Log an error message with exception details
     *
     * @param message the error message to log
     * @param throwable the exception that occurred
     */
    public static void error(String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.error("[{}] ERROR: {} | Exception: {}", timestamp, message, throwable.getMessage(), throwable);
    }
    
    /**
     * Log an error message with additional context and exception
     *
     * @param message the error message to log
     * @param context additional context information
     * @param throwable the exception that occurred
     */
    public static void error(String message, Map<String, Object> context, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.error("[{}] ERROR: {} | Context: {} | Exception: {}", 
                    timestamp, message, context, throwable.getMessage(), throwable);
    }
    
    /**
     * Log a debug message (only shown when debug logging is enabled)
     *
     * @param message the debug message to log
     */
    public static void debug(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] DEBUG: {}", timestamp, message);
    }
    
    /**
     * Log a debug message with additional context
     *
     * @param message the debug message to log
     * @param context additional context information
     */
    public static void debug(String message, Map<String, Object> context) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] DEBUG: {} | Context: {}", timestamp, message, context);
    }
    
    /**
     * Log a trace message (only shown when trace logging is enabled)
     *
     * @param message the trace message to log
     */
    public static void trace(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.trace("[{}] TRACE: {}", timestamp, message);
    }
    
    /**
     * Log method entry
     *
     * @param methodName the name of the method being entered
     */
    public static void methodEntry(String methodName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] METHOD ENTRY: {}", timestamp, methodName);
    }
    
    /**
     * Log method entry with parameters
     *
     * @param methodName the name of the method being entered
     * @param parameters the parameters passed to the method
     */
    public static void methodEntry(String methodName, Map<String, Object> parameters) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] METHOD ENTRY: {} | Parameters: {}", timestamp, methodName, parameters);
    }
    
    /**
     * Log method exit
     *
     * @param methodName the name of the method being exited
     */
    public static void methodExit(String methodName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] METHOD EXIT: {}", timestamp, methodName);
    }
    
    /**
     * Log method exit with return value
     *
     * @param methodName the name of the method being exited
     * @param returnValue the value being returned from the method
     */
    public static void methodExit(String methodName, Object returnValue) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] METHOD EXIT: {} | Return: {}", timestamp, methodName, returnValue);
    }
    
    /**
     * Log API request
     *
     * @param method the HTTP method
     * @param endpoint the API endpoint
     * @param requestId unique identifier for the request
     */
    public static void apiRequest(String method, String endpoint, String requestId) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.info("[{}] API REQUEST: {} {} | RequestId: {}", timestamp, method, endpoint, requestId);
    }
    
    /**
     * Log API response
     *
     * @param method the HTTP method
     * @param endpoint the API endpoint
     * @param statusCode the HTTP status code
     * @param requestId unique identifier for the request
     * @param responseTime response time in milliseconds
     */
    public static void apiResponse(String method, String endpoint, int statusCode, String requestId, long responseTime) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.info("[{}] API RESPONSE: {} {} | Status: {} | RequestId: {} | ResponseTime: {}ms", 
                    timestamp, method, endpoint, statusCode, requestId, responseTime);
    }
    
    /**
     * Log user authentication event
     *
     * @param username the username
     * @param event the authentication event (LOGIN, LOGOUT, REGISTER, etc.)
     * @param success whether the operation was successful
     */
    public static void authEvent(String username, String event, boolean success) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String status = success ? "SUCCESS" : "FAILED";
        logger.info("[{}] AUTH EVENT: {} | User: {} | Status: {}", timestamp, event, username, status);
    }
    
    /**
     * Log database operation
     *
     * @param operation the database operation (SELECT, INSERT, UPDATE, DELETE)
     * @param table the table name
     * @param duration the operation duration in milliseconds
     */
    public static void dbOperation(String operation, String table, long duration) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.debug("[{}] DB OPERATION: {} | Table: {} | Duration: {}ms", timestamp, operation, table, duration);
    }
    
    /**
     * Log WebSocket event
     *
     * @param event the WebSocket event (CONNECT, DISCONNECT, MESSAGE)
     * @param sessionId the WebSocket session ID
     * @param details additional details about the event
     */
    public static void websocketEvent(String event, String sessionId, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        logger.info("[{}] WEBSOCKET: {} | SessionId: {} | Details: {}", timestamp, event, sessionId, details);
    }
} 