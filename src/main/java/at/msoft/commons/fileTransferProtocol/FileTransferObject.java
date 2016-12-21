package at.msoft.commons.fileTransferProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import at.msoft.commons.fileTransferProtocol.Interfaces.IChatImagesAnswer;
import at.msoft.commons.fileTransferProtocol.Interfaces.IImageForSharing;
import at.msoft.commons.fileTransferProtocol.Interfaces.IImageForUser;
import at.msoft.commons.fileTransferProtocol.Interfaces.IChatImagesRequest;
import at.msoft.commons.fileTransferProtocol.Interfaces.IFileTransferSpecifierObject;
import at.msoft.commons.fileTransferProtocol.Interfaces.IGalleryImageRequest;
import at.msoft.commons.fileTransferProtocol.Interfaces.IImageRequestAnswer;

public final class FileTransferObject implements Serializable {
    private static final long serialVersionUID = -6494199107223929245L;

    private IFileTransferSpecifierObject specifiedObject = null;
    private TransferType transferType;
    private String[] senders, filePaths, fileNames;
    private UserInformationTransferObject[] senderInformation;
    private String receiver;
    private int amount;
    private byte[][] data;

    private FileTransferObject(int amount, String[] fullFilePaths, String receiver, String[] senders, UserInformationTransferObject[] senderInformation,
            TransferType transferType) {
        this.amount = amount;
        this.filePaths = fullFilePaths;
        this.receiver = receiver;
        this.senders = senders;
        this.transferType = transferType;
        this.senderInformation = senderInformation;
        fileNames = fullFilePaths != null ? new String[fullFilePaths.length] : new String[0];

        File file;
        for (int i = 0; i < fileNames.length; i++) {
            String x = fullFilePaths[i];
            file = new File(x);
            fileNames[i] = file.getName();
        }

        data = new byte[fileNames.length][];
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public IFileTransferSpecifierObject getSpecifiedObject() {
        return specifiedObject;
    }

    public static FileTransferObject getShareImageFTO(String fullFileSource,
            String sender) {
        return new FileTransferObject(0, new String[]{fullFileSource}, null, new String[]{sender}, null, TransferType.SEND_IMAGE_FOR_SHARING);
    }

    public static FileTransferObject getImageToUserFTO(String fullFileSource, String receiver, String sender) {
        return new FileTransferObject(0, new String[]{fullFileSource}, receiver, new String[]{sender}, null, TransferType.SEND_IMAGE_TO_USER);
    }

    public static FileTransferObject getRequestImagesForGalleryFTO(String sender, int amount) {
        return new FileTransferObject(amount, null, null, new String[]{sender}, null, TransferType.REQUEST_IMAGES_GALLERY);
    }

    public static FileTransferObject getRequestImagesForChatFTO(String sender) {
        return new FileTransferObject(0, null, null, new String[]{sender}, null, TransferType.REQUEST_IMAGES_CHAT);
    }

    public static FileTransferObject getAnswerImagesForChatFTO(String[] filepaths, String[] senders) {
        return new FileTransferObject(0, filepaths, null, senders, null, TransferType.ANSWER_IMAGES_CHAT);
    }

    public static FileTransferObject getAnswerImagesForGalleryFTO(String[] filepaths, UserInformationTransferObject[] senders) {
        return new FileTransferObject(0, filepaths, null, null, senders, TransferType.ANSWER_IMAGES_GALLERY);
    }

    public static FileTransferObject getTransferFinishedFTO() {
        return new FileTransferObject(0, null, null, null, null, TransferType.TRANSFER_FINISHED);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(transferType);
        if (transferType != TransferType.TRANSFER_FINISHED) {
            if (transferType != TransferType.ANSWER_IMAGES_GALLERY) {
                oos.writeObject(senders);
            } else {
                oos.writeObject(senderInformation);
            }

            switch (transferType) {
                case SEND_IMAGE_FOR_SHARING:
                    writeImageForSharing(oos);
                    break;
                case SEND_IMAGE_TO_USER:
                    writeImageToUser(oos);
                    break;
                case REQUEST_IMAGES_GALLERY:
                    oos.writeInt(amount);
                    break;
                case ANSWER_IMAGES_GALLERY:
                    writeAnswerImageGallery(oos);
                    break;
                case REQUEST_IMAGES_CHAT:
                    //everything needed is sent
                    break;
                case ANSWER_IMAGES_CHAT:
                    writeAnswerImageChat(oos);
                    break;
                case TRANSFER_FINISHED:
                    //can't happen - just here to mention all enum types
                    break;
            }
        }
    }

    private void writeAnswerImageGallery(ObjectOutputStream oos) throws IOException {
        sendMultipleImages(oos);
    }

    private void writeAnswerImageChat(ObjectOutputStream oos) throws IOException {
        sendMultipleImages(oos);
    }

    private void sendMultipleImages(ObjectOutputStream oos) throws IOException {
        int imageSize;
        File file;

        for (int i = 0; i < fileNames.length; i++) {
            file = new File(filePaths[i]);
            imageSize = (int) file.length();
            data[i] = new byte[imageSize];
            readFile(file, data[i]);
        }

        oos.writeObject(fileNames);
        oos.writeObject(data);
    }

    private void readFile(File file, byte[] buffer) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            is.read(buffer);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void writeImageToUser(ObjectOutputStream oos) throws IOException {
        oos.writeObject(receiver);
        sendMultipleImages(oos);
    }

    private void writeImageForSharing(ObjectOutputStream oos) throws IOException {
        sendMultipleImages(oos);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        transferType = (TransferType) ois.readObject();
        if (transferType != TransferType.TRANSFER_FINISHED) {
            if (transferType != TransferType.ANSWER_IMAGES_GALLERY) {
                senders = (String[]) ois.readObject();
            } else {
                senderInformation = (UserInformationTransferObject[]) ois.readObject();
            }

            switch (transferType) {
                case SEND_IMAGE_FOR_SHARING:
                    receiveImageForSharing(ois);
                    break;
                case SEND_IMAGE_TO_USER:
                    receiveImageForUser(ois);
                    break;
                case REQUEST_IMAGES_GALLERY:
                    receiveImagesForGalleryRequest(ois);
                    break;
                case ANSWER_IMAGES_GALLERY:
                    receiveAnswerImagesForGallery(ois);
                    break;
                case REQUEST_IMAGES_CHAT:
                    receiveImagesChatRequest();
                    break;
                case ANSWER_IMAGES_CHAT:
                    receiveImagesChatAnswer(ois);
                    break;
                case TRANSFER_FINISHED:
                    //can't happen - just here to mention all enum types
                    break;
            }
        }
    }

    private void receiveImagesChatRequest() {
        specifiedObject = new IChatImagesRequest() {
            @Override
            public String getRequester() {
                return senders[0];
            }
        };
    }

    private void receiveImagesChatAnswer(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        receiveMultipleImages(ois);
        specifiedObject = new IChatImagesAnswer() {
            @Override
            public UserImageTriple[] getImages() {
                return wrapImagesInTriples();
            }
        };
    }

    private UserImageTriple[] wrapImagesInTriples() {
        UserImageTriple[] triples = new UserImageTriple[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            triples[i] = new UserImageTriple(senderInformation[i], fileNames[i], data[i]);
        }

        return triples;
    }

    private void receiveAnswerImagesForGallery(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        receiveMultipleImages(ois);
        specifiedObject = new IImageRequestAnswer() {
            @Override
            public UserImageTriple[] getImages() {
                return wrapImagesInTriples();
            }
        };
    }

    private void receiveImagesForGalleryRequest(ObjectInputStream ois) throws IOException {
        amount = ois.readInt();
        specifiedObject = new IGalleryImageRequest() {
            @Override
            public int getAmountRequested() {
                return amount;
            }

            @Override
            public String getRequester() {
                return senders[0];
            }
        };
    }

    private void receiveImageForUser(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        receiver = (String) ois.readObject();
        receiveMultipleImages(ois);
        specifiedObject = new IImageForUser() {
            @Override
            public String getSender() {
                return senders[0];
            }

            @Override
            public String getReceiver() {
                return receiver;
            }

            @Override
            public String getFileName() {
                return fileNames[0];
            }

            @Override
            public byte[] getImageBytes() {
                return data[0];
            }
        };
    }

    private void receiveImageForSharing(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        receiveMultipleImages(ois);
        specifiedObject = new IImageForSharing() {
            @Override
            public String getSender() {
                return senders[0];
            }

            @Override
            public String getFileName() {
                return fileNames[0];
            }

            @Override
            public byte[] getImageBytes() {
                return data[0];
            }
        };
    }

    private void receiveMultipleImages(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        fileNames = (String[]) ois.readObject();
        data = (byte[][]) ois.readObject();
    }
}

