package at.msoft.commons.fileTransferProtocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Andreas on 08.12.2016.
 */
public class UserInformationTransferObject implements Serializable {
    private static final long serialVersionUID = -3882419439652850531L;
    String userName, country, profilePicturePath;
    Date birthDate;
    byte[] imageData = null;

    public UserInformationTransferObject(String userName, String country, String profilePicturePath, Date birthDate) {
        this.userName = userName;
        this.country = country;
        this.profilePicturePath = profilePicturePath;
        this.birthDate = birthDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getCountry() {
        return country;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public byte[] getImage() {
        return imageData;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        userName = (String) ois.readObject();
        country = (String) ois.readObject();
        birthDate = (Date) ois.readObject();
        imageData = (byte[]) ois.readObject();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(userName);
        oos.writeObject(country);
        oos.writeObject(birthDate);

        int imageSize;
        File file = new File(profilePicturePath);
        imageSize = (int) file.length();
        imageData = new byte[imageSize];
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            fis.read(imageData);
        } finally {
            if(fis != null)
            {
                fis.close();
            }
        }

        oos.writeObject(imageData);
    }

}
