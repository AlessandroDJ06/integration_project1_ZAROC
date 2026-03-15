package game.integration_project1_zaroc.dao;

public class ZarocDaoException extends RuntimeException {
    public ZarocDaoException(String message) {
        super(message);
    }
    public ZarocDaoException(String message, Exception cause) {
        super(message, cause);
    }
}
