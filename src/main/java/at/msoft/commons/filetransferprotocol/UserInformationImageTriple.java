package at.msoft.commons.filetransferprotocol;

/**
 * Created by Andreas on 21.12.2016.
 */
public class UserInformationImageTriple extends Triple<UserInformationTransferObject, String, byte[]> {

    public UserInformationImageTriple(UserInformationTransferObject userInformations, String imageName, byte[] imageData) {
        super(userInformations, imageName, imageData);
    }

    public UserInformationTransferObject getImageCreatorInformations()
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
