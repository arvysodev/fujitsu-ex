package ee.trialtask.delivery.fee.exception;

public class ForbiddenVehicleUsageException extends RuntimeException {

    public ForbiddenVehicleUsageException(String message) {
        super(message);
    }
}
