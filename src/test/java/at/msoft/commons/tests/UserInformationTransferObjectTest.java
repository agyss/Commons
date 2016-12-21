package at.msoft.commons.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Random;

import at.msoft.commons.fileTransferProtocol.UserInformationTransferObject;

/**
 * Created by Andreas on 20.12.2016.
 */
class UserInformationTransferObjectTest {
    UserInformationTransferObject uitObject = null;
    byte[] imageRawData = null;

    @BeforeClass
    void beforeAll() {
        imageRawData = new byte[1920 * 1080];
        Random r = new Random();
        r.nextBytes(imageRawData);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("profilePicture");
            fos.write(imageRawData);
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

    @AfterClass
    void afterAll() {
        File file = new File("profilePicture");
        if(file.exists())
        {
            file.delete();
        }

    }

    @Before
    void initialize() {
        uitObject = new UserInformationTransferObject("user1", "AUT", "profilePicture", new Date(1995, 2, 7));
    }

    private UserInformationTransferObject storeAndLoadUITObject()
    {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        ObjectInputStream ois = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(uitObject);
            ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

            return (UserInformationTransferObject) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(oos != null)
            {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (baos != null)
            {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ois != null)
            {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Test
    void getUserName() {
        assertThat("str")
    }

    @Test
    void getCountry() {

    }

    @Test
    void getBirthDate() {

    }

    @Test
    void getImage() {

    }

}