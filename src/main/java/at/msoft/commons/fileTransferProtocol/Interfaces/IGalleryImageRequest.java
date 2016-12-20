package at.msoft.commons.fileTransferProtocol.Interfaces;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IGalleryImageRequest extends IFileTransferSpecifierObject {
    int getAmountRequested();
    String getRequester();
}
