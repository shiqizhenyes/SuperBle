package mobi.zack.superblelib.exception;

import java.util.UUID;

import mobi.zack.superblelib.constant.C;

public class SuperBleBaseException extends Exception {

    protected UUID exceptionUUID;
    protected int exceptionCode = C.DEFAULT_EXCEPTION_CODE;
    protected String description;

    public SuperBleBaseException() {

    }

    public SuperBleBaseException(int exceptionCode, String description) {
        this.exceptionCode = exceptionCode;
        this.description = description;
    }

    public SuperBleBaseException(UUID exceptionUUID,int exceptionCode, String description) {
        this.exceptionUUID = exceptionUUID;
        this.exceptionCode = exceptionCode;
        this.description = description;
    }

    @Override
    public String toString() {
        return "SuperBleException{" +
                ", exceptionCode=" + exceptionCode +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
