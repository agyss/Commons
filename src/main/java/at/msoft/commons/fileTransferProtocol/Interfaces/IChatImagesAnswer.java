package at.msoft.commons.fileTransferProtocol.Interfaces;

import at.msoft.commons.fileTransferProtocol.UsernameImageTriple;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IChatImagesAnswer extends IFileTransferSpecifierObject {
    UsernameImageTriple[] getImages();
}
