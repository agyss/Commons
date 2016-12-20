package at.msoft.commons.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import at.msoft.commons.fileTransferProtocol.FileTransferObject;
import at.msoft.commons.fileTransferProtocol.Interfaces.IChatImagesAnswer;
import at.msoft.commons.fileTransferProtocol.Interfaces.IChatImagesRequest;
import at.msoft.commons.fileTransferProtocol.Interfaces.IFileTransferSpecifierObject;
import at.msoft.commons.fileTransferProtocol.Interfaces.IGalleryImageRequest;
import at.msoft.commons.fileTransferProtocol.Interfaces.IImageForSharing;
import at.msoft.commons.fileTransferProtocol.Interfaces.IImageForUser;
import at.msoft.commons.fileTransferProtocol.Interfaces.IImageRequestAnswer;
import at.msoft.commons.fileTransferProtocol.TransferType;
import at.msoft.commons.fileTransferProtocol.UserImageTriple;

/**
 * Created by Andreas on 29.11.2016.
 */
class FileTransferObjectTest {
    private static File[] files;
    private static byte[][] pictures;
    private File[] tempFiles;
    private String[] senders = new String[] {"Jakob", "Hans", "Peter", "Martin", "Thomas", "Ehrenfried", "Justus", "Mark", "Tobias", "Dominik"};
    private String[] receivers = new String[] {"Elisabeth", "Susanna", "Pia", "Bettina", "Jana", "Elena", "Nicole", "Sabrina", "Marlene", "Edith"};

    @BeforeAll
    static void initialize() {
        Random r = new Random();
        pictures = new byte[10][640 * 800];
        String[] names = new String[pictures.length];
        files = new File[pictures.length];
        FileOutputStream fos = null;

        for (int i = 0; i < pictures.length; i++) {
            r.nextBytes(pictures[i]);
            names[i] = "picture" + i;
            files[i] = new File(names[i]);
            try {
                fos = new FileOutputStream(files[i]);
                fos.write(pictures[i]);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @AfterAll
    static void cleanUp() {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @BeforeEach
    void beforeEach() {
        tempFiles = new File[files.length];
        for (int i = 0; i < tempFiles.length; i++) {
            String name = "temp" + files[i].getName();
            tempFiles[i] = new File(name);
        }
    }

    @AfterEach
    void afterEach() {
        for (File x : tempFiles) {
            if (x.exists()) {
                x.delete();
            }
        }
    }

    private void writeFTO(FileTransferObject fto, File target)
    {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(target));
            oos.writeObject(fto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(oos != null)
            {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private FileTransferObject readFTO(File source)
    {
        FileTransferObject returnObject = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(source));
            returnObject = (FileTransferObject) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(ois != null)
            {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return returnObject;
    }

    private FileTransferObject getFileTransferObject(TransferType transferType)
    {
        FileTransferObject fto = null;
        switch (transferType)
        {
            case ANSWER_IMAGES_CHAT:
                fto =  FileTransferObject.getAnswerImagesForChatFTO(new String[]{files[0].getAbsolutePath(), files[1].getAbsolutePath()}, new String[]{senders[0], senders[1]});
                break;
            case ANSWER_IMAGES_GALLERY:
                fto = FileTransferObject.getAnswerImagesForGalleryFTO(new String[]{files[2].getAbsolutePath(), files[3].getAbsolutePath()}, new String[]{senders[2], senders[3]});
                break;
            case REQUEST_IMAGES_CHAT:
                fto = FileTransferObject.getRequestImagesForChatFTO(senders[4]);
                break;
            case REQUEST_IMAGES_GALLERY:
                fto = FileTransferObject.getRequestImagesForGalleryFTO(senders[5], 2);
                break;
            case SEND_IMAGE_FOR_SHARING:
                fto = FileTransferObject.getShareImageFTO(files[4].getAbsolutePath(), senders[6]);
                break;
            case SEND_IMAGE_TO_USER:
                fto = FileTransferObject.getImageToUserFTO(files[5].getAbsolutePath(), receivers[0], senders[7]);
                break;
            case TRANSFER_FINISHED:
                fto = FileTransferObject.getTransferFinishedFTO();
                break;
        }

        return fto;
    }

    @Test
    void getTransferType() {
        int i = 0;
        //Send image for sharing
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_FOR_SHARING);
        assertEquals(TransferType.SEND_IMAGE_FOR_SHARING, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.SEND_IMAGE_FOR_SHARING, fto.getTransferType());

        //request images for the gallery
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_GALLERY);
        assertEquals(TransferType.REQUEST_IMAGES_GALLERY, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.REQUEST_IMAGES_GALLERY, fto.getTransferType());

        //answer images for the gallery
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_GALLERY);
        assertEquals(TransferType.ANSWER_IMAGES_GALLERY, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.ANSWER_IMAGES_GALLERY, fto.getTransferType());

        //send image to user
        fto = getFileTransferObject(TransferType.SEND_IMAGE_TO_USER);
        assertEquals(TransferType.SEND_IMAGE_TO_USER, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.SEND_IMAGE_TO_USER, fto.getTransferType());

        //request images for the chat
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_CHAT);
        assertEquals(TransferType.REQUEST_IMAGES_CHAT, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.REQUEST_IMAGES_CHAT, fto.getTransferType());

        //answer a chat images request
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_CHAT);
        assertEquals(TransferType.ANSWER_IMAGES_CHAT, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.ANSWER_IMAGES_CHAT, fto.getTransferType());

        fto = getFileTransferObject(TransferType.TRANSFER_FINISHED);
        assertEquals(TransferType.TRANSFER_FINISHED, fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertEquals(TransferType.TRANSFER_FINISHED, fto.getTransferType());
    }

    @Test
    void getSpecifiedObject() {
        int i = 0;
        //Send image for sharing
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_FOR_SHARING);
        assertNull(fto.getSpecifiedObject());
        writeFTO(fto, tempFiles[i]);
        assertNull(fto.getSpecifiedObject());
        fto = readFTO(tempFiles[i++]);
        assertTrue(fto.getSpecifiedObject() instanceof IImageForSharing);

        //request images for the gallery
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_GALLERY);
        assertNull(fto.getSpecifiedObject());
        writeFTO(fto, tempFiles[i]);
        assertNull(fto.getSpecifiedObject());
        fto = readFTO(tempFiles[i++]);
        assertTrue(fto.getSpecifiedObject() instanceof IGalleryImageRequest);

        //answer the image request
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_GALLERY);
        assertNull(fto.getSpecifiedObject());
        writeFTO(fto, tempFiles[i]);
        assertNull(fto.getSpecifiedObject());
        fto = readFTO(tempFiles[i++]);
        assertTrue(fto.getSpecifiedObject() instanceof IImageRequestAnswer);

        //send image to user
        fto = getFileTransferObject(TransferType.SEND_IMAGE_TO_USER);
        assertNull(fto.getSpecifiedObject());
        writeFTO(fto, tempFiles[i]);
        assertNull(fto.getSpecifiedObject());
        fto = readFTO(tempFiles[i++]);
        assertTrue(fto.getSpecifiedObject() instanceof IImageForUser);

        //request images for the chat
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_CHAT);
        assertNull(fto.getSpecifiedObject());
        writeFTO(fto, tempFiles[i]);
        assertNull(fto.getSpecifiedObject());
        fto = readFTO(tempFiles[i++]);
        assertTrue(fto.getSpecifiedObject() instanceof IChatImagesRequest);

        //answer a chat images request
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_CHAT);
        assertNull(fto.getSpecifiedObject());
        writeFTO(fto, tempFiles[i]);
        assertNull(fto.getSpecifiedObject());
        fto = readFTO(tempFiles[i++]);
        assertTrue(fto.getSpecifiedObject() instanceof IChatImagesAnswer);
    }

    @Test
    void getShareImageFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_FOR_SHARING);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IImageForSharing sharingImage = (IImageForSharing) fto.getSpecifiedObject();
        assertNotNull(sharingImage);
        assertEquals(files[4].getName() ,sharingImage.getFileName());
        assertEquals(files[4].length(), sharingImage.getImageBytes().length);
        assertArrayEquals(pictures[4], sharingImage.getImageBytes());
        assertEquals(senders[6], sharingImage.getSender());
    }

    @Test
    void getImageToUserFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_TO_USER);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IImageForUser imageForUser = (IImageForUser) fto.getSpecifiedObject();
        assertNotNull(imageForUser);

        // TODO: 30.11.2016 go on here
        assertEquals(files[5].getName() ,imageForUser.getFileName());
        assertEquals(files[5].length(), imageForUser.getImageBytes().length);
        assertEquals(receivers[0], imageForUser.getReceiver());
        assertEquals(senders[7], imageForUser.getSender());
        assertArrayEquals(pictures[5], imageForUser.getImageBytes());
    }

    @Test
    void getRequestImagesForGalleryFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.REQUEST_IMAGES_GALLERY);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IGalleryImageRequest galleryImageRequest = (IGalleryImageRequest) fto.getSpecifiedObject();
        assertNotNull(galleryImageRequest);
        assertEquals(senders[5], galleryImageRequest.getRequester());
        assertEquals(2, galleryImageRequest.getAmountRequested());
    }

    @Test
    void getRequestImagesForChatFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.REQUEST_IMAGES_CHAT);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IChatImagesRequest chatImagesRequest = (IChatImagesRequest) fto.getSpecifiedObject();
        assertNotNull(chatImagesRequest);
        assertEquals(senders[4], chatImagesRequest.getRequester());
    }

    @Test
    void getAnswerImagesForChatFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.ANSWER_IMAGES_CHAT);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IChatImagesAnswer answerImages = (IChatImagesAnswer) fto.getSpecifiedObject();
        assertNotNull(answerImages);
        assertEquals(2, answerImages.getImages().length);

        UserImageTriple[] imageTriples = answerImages.getImages();
        assertArrayEquals(pictures[0], imageTriples[0].getImageData());
        assertEquals(files[0].length(), imageTriples[0].getImageData().length);
        assertEquals(files[0].getName(), imageTriples[0].getImageName());
        assertEquals(senders[0], imageTriples[0].getImageCreator());

        assertArrayEquals(pictures[1], imageTriples[1].getImageData());
        assertEquals(files[1].length(), imageTriples[1].getImageData().length);
        assertEquals(files[1].getName(), imageTriples[1].getImageName());
        assertEquals(senders[1], imageTriples[1].getImageCreator());
    }

    @Test
    void getAnswerImagesForGalleryFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.ANSWER_IMAGES_GALLERY);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IImageRequestAnswer imagesForGallery = (IImageRequestAnswer) fto.getSpecifiedObject();
        assertNotNull(imagesForGallery);
        assertEquals(2, imagesForGallery.getImages().length);

        UserImageTriple[] imageTriples = imagesForGallery.getImages();
        assertArrayEquals(pictures[2], imageTriples[0].getImageData());
        assertEquals(files[2].length(), imageTriples[0].getImageData().length);
        assertEquals(files[2].getName(), imageTriples[0].getImageName());
        assertEquals(senders[2], imageTriples[0].getImageCreator());

        assertArrayEquals(pictures[3], imageTriples[1].getImageData());
        assertEquals(files[3].length(), imageTriples[1].getImageData().length);
        assertEquals(files[3].getName(), imageTriples[1].getImageName());
        assertEquals(senders[3], imageTriples[1].getImageCreator());
    }

    @Test
    void getTransferFinishedFTO()
    {
        FileTransferObject fto = getFileTransferObject(TransferType.TRANSFER_FINISHED);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IFileTransferSpecifierObject specifierObject = fto.getSpecifiedObject();
        assertNull(specifierObject);
    }
}