package at.msoft.commons.filetransferprotocol.interfaces;

import at.msoft.commons.filetransferprotocol.UsernameImageTriple;

/**
 * Created by Andreas on 28.11.2016.
 */
public interface IChatImagesAnswer extends IFileTransferSpecifierObject {
    UsernameImageTriple[] getImages();
}
