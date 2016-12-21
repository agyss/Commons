package at.msoft.commons.filetransferprotocol.interfaces;

import at.msoft.commons.filetransferprotocol.UserInformationImageTriple;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IImageRequestAnswer extends IFileTransferSpecifierObject {
    UserInformationImageTriple[] getImages();
}
