package at.msoft.commons.filetransferprotocol;

/**
 * Created by Andreas on 28.11.2016.
 */
public class UsernameImageTriple extends Triple<String, String, byte[]> {

    public UsernameImageTriple(String userName, String imageName, byte[] imageData) {
        super(userName, imageName, imageData);
    }

    public String getImageCreatorName()
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
