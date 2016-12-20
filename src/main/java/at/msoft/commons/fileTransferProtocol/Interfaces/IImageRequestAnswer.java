package at.msoft.commons.fileTransferProtocol.Interfaces;

import at.msoft.commons.fileTransferProtocol.UserImageTriple;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IImageRequestAnswer extends IFileTransferSpecifierObject {
    UserImageTriple[] getImages();
}
