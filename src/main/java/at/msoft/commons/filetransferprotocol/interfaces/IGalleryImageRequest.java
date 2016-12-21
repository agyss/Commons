package at.msoft.commons.filetransferprotocol.interfaces;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IGalleryImageRequest extends IFileTransferSpecifierObject {
    int getAmountRequested();
    String getRequester();
}
