package at.msoft.commons.filetransferprotocol.interfaces;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IImageForUser extends IFileTransferSpecifierObject {
    String getSender();
    String getReceiver();
    String getFileName();
    byte[] getImageBytes();
}
