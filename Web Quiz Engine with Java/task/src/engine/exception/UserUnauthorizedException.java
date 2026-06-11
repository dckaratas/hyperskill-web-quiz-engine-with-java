package engine.exception;

public class UserUnauthorizedException extends RuntimeException{
    public UserUnauthorizedException() {
        super("User Unauthorized For This Action");
    }
}
