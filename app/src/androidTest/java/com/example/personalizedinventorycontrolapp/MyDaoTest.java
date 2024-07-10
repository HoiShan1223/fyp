//package com.example.personalizedinventorycontrolapp;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.*;
//
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//@RunWith(AndroidJUnit4.class)
//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//@DatabaseSetup("classpath:testData.xml")
//public class MyDaoTest {
//
//    @Autowired
//    private MyDao myDao;
//
//    @Test
//    public void testGetMyData() {
//        List<MyData> myDataList = myDao.getMyData();
//        assertEquals(2, myDataList.size());
//        assertEquals("Test Data 1", myDataList.get(0).getName());
//        assertEquals("Test Data 2", myDataList.get(1).getName());
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        DatabaseOperation.DELETE_ALL.execute(getConnection(), getDataSet());
//    }
//
//    private IDatabaseConnection getConnection() throws Exception {
//        // return a connection to your test database
//    }
//
//    private IDataSet getDataSet() throws Exception {
//        // return the test data XML file as a data set
//    }
//}