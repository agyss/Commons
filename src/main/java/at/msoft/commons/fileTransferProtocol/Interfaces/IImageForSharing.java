package at.msoft.commons.fileTransferProtocol.Interfaces;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IImageForSharing extends IFileTransferSpecifierObject {
    String getSender();
    String getFileName();
    byte[] getImageBytes();
}
