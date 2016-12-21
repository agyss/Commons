package at.msoft.commons.fileTransferProtocol;

/**
 * Created by Andreas on 28.11.2016.
 */
public class UserImageTriple extends Triple<UserInformationTransferObject, String, byte[]> {

    public UserImageTriple(UserInformationTransferObject uito, String imageName, byte[] imageData) {
        super(uito, imageName, imageData);
    }

    public UserInformationTransferObject getImageCreatorInformation()
    {
        return super.getFirst();
    }

    public String getImageName()
    {
        return super.getSecond();
    }

    public byte[] getImageData()
    {
        return super.getThird();
    }

}
