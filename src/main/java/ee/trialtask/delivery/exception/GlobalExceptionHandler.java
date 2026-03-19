package ee.trialtask.delivery.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WeatherObservationNotFoundException.class)
    public ProblemDetail handleWeatherObservationNotFound(
            WeatherObservationNotFoundException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.NOT_FOUND,
                "Weather Observation Not Found",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(WeatherDataFetchException.class)
    public ProblemDetail handleWeatherDataFetch(
            WeatherDataFetchException exception,
            HttpServletRequest request
    ) {
        log.error("Failed to fetch weather data", exception);

        return problem(
                HttpStatus.BAD_GATEWAY,
                "Weather Data Fetch Failed",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(ForbiddenVehicleUsageException.class)
    public ProblemDetail handleForbiddenVehicleUsage(
            ForbiddenVehicleUsageException exception,
            HttpServletRequest request
    ) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Usage of selected vehicle type is forbidden",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception occurred", exception);

        return problem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Unexpected error occurred.",
                request
        );
    }

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create("https://delivery-fee-calculator/problems/" + toSlug(title)));
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    private String toSlug(String title) {
        return title.toLowerCase().replace(' ', '-');
    }
}
