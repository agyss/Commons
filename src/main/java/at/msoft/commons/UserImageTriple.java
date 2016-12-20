package at.msoft.commons;

/**
 * Created by Andreas on 28.11.2016.
 */
public class UserImageTriple extends Triple<String, String, byte[]> {

    public UserImageTriple(String creator, String imageName, byte[] imageData) {
        super(creator, imageName, imageData);
    }

    public String getImageCreator()
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
