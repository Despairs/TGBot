/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.despairs.telegram.bot.db.repo.impl.ProcessedReferenceRepositoryImpl;
import com.despairs.telegram.bot.db.repo.impl.SettingsRepositoryImpl;
import com.despairs.telegram.bot.db.repo.impl.UserRepositoryImpl;
import java.sql.SQLException;
import java.util.Random;
import static org.junit.Assert.*;

/**
 *
 * @author EKovtunenko
 */
public class DBTest {

//    @Test
    public void test() throws SQLException {
        assertEquals("val", SettingsRepositoryImpl.getInstance().getValueV("TEST"));

//        assertFalse(ProcessedReferenceRepositoryImpl.getInstance().isReferenceStored("100500", "TEST_PRODUCER"));
        ProcessedReferenceRepositoryImpl.getInstance().createReference("100500", "TEST_PRODUCER");
        assertTrue(ProcessedReferenceRepositoryImpl.getInstance().isReferenceStored("100500", "TEST_PRODUCER"));

//        assertFalse(UserRepositoryImpl.getInstance().isUserRegistered("123"));
        Integer id = new Random().nextInt();
        UserRepositoryImpl.getInstance().registerUser(id, "test", "redmineId");
        assertTrue(UserRepositoryImpl.getInstance().isUserRegistered(id));
        System.out.println(UserRepositoryImpl.getInstance().getUser(id));
    }

}
