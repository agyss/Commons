package at.msoft.commons.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @BeforeClass
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

    @AfterClass
    static void cleanUp() {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }

    @Before
    void beforeEach() {
        tempFiles = new File[files.length];
        for (int i = 0; i < tempFiles.length; i++) {
            String name = "temp" + files[i].getName();
            tempFiles[i] = new File(name);
        }
    }

    @After
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
        assertThat(TransferType.SEND_IMAGE_FOR_SHARING).isEqualTo(fto.getTransferType());

        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.SEND_IMAGE_FOR_SHARING).isEqualTo(fto.getTransferType());

        //request images for the gallery
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_GALLERY);
        assertThat(TransferType.REQUEST_IMAGES_GALLERY).isEqualTo(fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.REQUEST_IMAGES_GALLERY).isEqualTo(fto.getTransferType());

        //answer images for the gallery
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_GALLERY);
        assertThat(TransferType.ANSWER_IMAGES_GALLERY).isEqualTo(fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.ANSWER_IMAGES_GALLERY).isEqualTo(fto.getTransferType());

        //send image to user
        fto = getFileTransferObject(TransferType.SEND_IMAGE_TO_USER);
        assertThat(TransferType.SEND_IMAGE_TO_USER).isEqualTo(fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.SEND_IMAGE_TO_USER).isEqualTo(fto.getTransferType());

        //request images for the chat
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_CHAT);
        assertThat(TransferType.REQUEST_IMAGES_CHAT).isEqualTo(fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.REQUEST_IMAGES_CHAT).isEqualTo(fto.getTransferType());

        //answer a chat images request
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_CHAT);
        assertThat(TransferType.ANSWER_IMAGES_CHAT).isEqualTo(fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.ANSWER_IMAGES_CHAT).isEqualTo(fto.getTransferType());

        fto = getFileTransferObject(TransferType.TRANSFER_FINISHED);
        assertThat(TransferType.TRANSFER_FINISHED).isEqualTo(fto.getTransferType());
        writeFTO(fto, tempFiles[i]);
        fto = readFTO(tempFiles[i++]);
        assertThat(TransferType.TRANSFER_FINISHED).isEqualTo(fto.getTransferType());
    }

    @Test
    void getSpecifiedObject() {
        int i = 0;
        //Send image for sharing
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_FOR_SHARING);
        assertThat(fto.getSpecifiedObject()).isNull();
        writeFTO(fto, tempFiles[i]);
        assertThat(fto.getSpecifiedObject()).isNull();
        fto = readFTO(tempFiles[i++]);
        assertThat(fto.getSpecifiedObject() instanceof IImageForSharing).isTrue();

        //request images for the gallery
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_GALLERY);
        assertThat(fto.getSpecifiedObject()).isNull();
        writeFTO(fto, tempFiles[i]);
        assertThat(fto.getSpecifiedObject()).isNull();
        fto = readFTO(tempFiles[i++]);
        assertThat(fto.getSpecifiedObject() instanceof IGalleryImageRequest).isTrue();

        //answer the image request
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_GALLERY);
        assertThat(fto.getSpecifiedObject()).isNull();
        writeFTO(fto, tempFiles[i]);
        assertThat(fto.getSpecifiedObject()).isNull();
        fto = readFTO(tempFiles[i++]);
        assertThat(fto.getSpecifiedObject() instanceof IImageRequestAnswer).isTrue();

        //send image to user
        fto = getFileTransferObject(TransferType.SEND_IMAGE_TO_USER);
        assertThat(fto.getSpecifiedObject()).isNull();
        writeFTO(fto, tempFiles[i]);
        assertThat(fto.getSpecifiedObject()).isNull();
        fto = readFTO(tempFiles[i++]);
        assertThat(fto.getSpecifiedObject() instanceof IImageForUser).isTrue();

        //request images for the chat
        fto = getFileTransferObject(TransferType.REQUEST_IMAGES_CHAT);
        assertThat(fto.getSpecifiedObject()).isNull();
        writeFTO(fto, tempFiles[i]);
        assertThat(fto.getSpecifiedObject()).isNull();
        fto = readFTO(tempFiles[i++]);
        assertThat(fto.getSpecifiedObject() instanceof IChatImagesRequest).isTrue();

        //answer a chat images request
        fto = getFileTransferObject(TransferType.ANSWER_IMAGES_CHAT);
        assertThat(fto.getSpecifiedObject()).isNull();
        writeFTO(fto, tempFiles[i]);
        assertThat(fto.getSpecifiedObject()).isNull();
        fto = readFTO(tempFiles[i++]);
        assertThat(fto.getSpecifiedObject() instanceof IChatImagesAnswer).isTrue();
    }

    @Test
    void getShareImageFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_FOR_SHARING);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IImageForSharing sharingImage = (IImageForSharing) fto.getSpecifiedObject();
        assertThat(sharingImage).isNotNull();
        assertThat(files[4].getName()).isEqualTo(sharingImage.getFileName());
        assertThat(files[4].length()).isEqualTo(sharingImage.getImageBytes().length);
        assertThat(pictures[4]).isEqualTo(sharingImage.getImageBytes());
        assertThat(senders[6]).isEqualTo(sharingImage.getSender());
    }

    @Test
    void getImageToUserFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.SEND_IMAGE_TO_USER);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IImageForUser imageForUser = (IImageForUser) fto.getSpecifiedObject();
        assertThat(imageForUser).isNotNull();

        // TODO: 30.11.2016 go on here
        assertThat(files[5].getName()).isEqualTo(imageForUser.getFileName());
        assertThat(files[5].length()).isEqualTo(imageForUser.getImageBytes().length);
        assertThat(receivers[0]).isEqualTo(imageForUser.getReceiver());
        assertThat(senders[7]).isEqualTo(imageForUser.getSender());
        assertThat(pictures[5]).isEqualTo(imageForUser.getImageBytes());
    }

    @Test
    void getRequestImagesForGalleryFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.REQUEST_IMAGES_GALLERY);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IGalleryImageRequest galleryImageRequest = (IGalleryImageRequest) fto.getSpecifiedObject();
        assertThat(galleryImageRequest).isNotNull();
        assertThat(senders[5]).isEqualTo(galleryImageRequest.getRequester());
        assertThat(2).isEqualTo(galleryImageRequest.getAmountRequested());
    }

    @Test
    void getRequestImagesForChatFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.REQUEST_IMAGES_CHAT);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IChatImagesRequest chatImagesRequest = (IChatImagesRequest) fto.getSpecifiedObject();
        assertThat(chatImagesRequest).isNotNull();
        assertThat(senders[4]).isEqualTo(chatImagesRequest.getRequester());
    }

    @Test
    void getAnswerImagesForChatFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.ANSWER_IMAGES_CHAT);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IChatImagesAnswer answerImages = (IChatImagesAnswer) fto.getSpecifiedObject();
        assertThat(answerImages).isNotNull();
        assertThat(2).isEqualTo(answerImages.getImages().length);

        UserImageTriple[] imageTriples = answerImages.getImages();
        assertThat(pictures[0]).isEqualTo(imageTriples[0].getImageData());
        assertThat(files[0].length()).isEqualTo(imageTriples[0].getImageData().length);
        assertThat(files[0].getName()).isEqualTo(imageTriples[0].getImageName());
        assertThat(senders[0]).isEqualTo(imageTriples[0].getImageCreatorInformation());

        assertThat(pictures[1]).isEqualTo(imageTriples[1].getImageData());
        assertThat(files[1].length()).isEqualTo(imageTriples[1].getImageData().length);
        assertThat(files[1].getName()).isEqualTo(imageTriples[1].getImageName());
        assertThat(senders[1]).isEqualTo(imageTriples[1].getImageCreatorInformation());
    }

    @Test
    void getAnswerImagesForGalleryFTO() {
        FileTransferObject fto = getFileTransferObject(TransferType.ANSWER_IMAGES_GALLERY);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IImageRequestAnswer imagesForGallery = (IImageRequestAnswer) fto.getSpecifiedObject();
        assertThat(imagesForGallery).isNotNull();
        assertThat(2).isEqualTo(imagesForGallery.getImages().length);

        UserImageTriple[] imageTriples = imagesForGallery.getImages();
        assertThat(pictures[2]).isEqualTo(imageTriples[0].getImageData());
        assertThat(files[2].length()).isEqualTo(imageTriples[0].getImageData().length);
        assertThat(files[2].getName()).isEqualTo(imageTriples[0].getImageName());
        assertThat(senders[2]).isEqualTo(imageTriples[0].getImageCreatorInformation());

        assertThat(pictures[3]).isEqualTo(imageTriples[1].getImageData());
        assertThat(files[3].length()).isEqualTo(imageTriples[1].getImageData().length);
        assertThat(files[3].getName()).isEqualTo(imageTriples[1].getImageName());
        assertThat(senders[3]).isEqualTo(imageTriples[1].getImageCreatorInformation());
    }

    @Test
    void getTransferFinishedFTO()
    {
        FileTransferObject fto = getFileTransferObject(TransferType.TRANSFER_FINISHED);
        writeFTO(fto, tempFiles[0]);
        fto = readFTO(tempFiles[0]);
        IFileTransferSpecifierObject specifierObject = fto.getSpecifiedObject();
        assertThat(specifierObject).isNull();
    }
}