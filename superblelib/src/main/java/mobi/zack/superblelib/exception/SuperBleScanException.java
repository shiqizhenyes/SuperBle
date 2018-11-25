package mobi.zack.superblelib.exception;

import java.util.UUID;

import mobi.zack.superblelib.constant.C;

public class SuperBleScanException extends SuperBleBaseException {

    private UUID superBleScanExceptionUUID = UUID.fromString(C.SBS_UUID);

    public SuperBleScanException() {
        this.exceptionUUID = superBleScanExceptionUUID;
    }

    public SuperBleScanException(String description) {
        this();
        this.description = description;
    }

    @Override
    public String toString() {
        return "SuperBleScanException{" +
                "superBleScanExceptionUUID=" + superBleScanExceptionUUID +
                ", exceptionCode=" + exceptionCode +
                ", description='" + description + '\'' +
                '}';
    }
}
