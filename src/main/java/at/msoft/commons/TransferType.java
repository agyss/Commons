package at.msoft.commons;

import java.io.Serializable;

/**
 * Created by Andreas on 29.11.2016.
 */
public enum TransferType implements Serializable {
    SEND_IMAGE_FOR_SHARING,
    SEND_IMAGE_TO_USER,
    REQUEST_IMAGES_GALLERY,
    ANSWER_IMAGES_GALLERY,
    REQUEST_IMAGES_CHAT,
    ANSWER_IMAGES_CHAT,
    TRANSFER_FINISHED
}
