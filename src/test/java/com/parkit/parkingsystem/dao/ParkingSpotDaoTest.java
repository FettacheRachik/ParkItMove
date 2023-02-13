package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDaoTest {
	
    private static ParkingSpotDAO parkingSpotDAO;

    @Mock
    private static DataBaseTestConfig dataBaseTestConfig;
    @Mock
    private static Connection con;
    @Mock
    private static PreparedStatement ps;
    @Mock
    private static ResultSet rs;
    

    @BeforeEach
    private void setUp() throws Exception {

        parkingSpotDAO = new ParkingSpotDAO();
       
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        //con = dataBaseTestConfig.getConnection();
        when(dataBaseTestConfig.getConnection()).thenReturn(con);

    }
    
    
    @Test
    void getNextAvailableSlot() throws SQLException{
        when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(2);

        assertEquals(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR), 2);
    }
    
    @Test
    void testUpdateParking() throws Exception {
        ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, true);
        when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        
        assertEquals(parkingSpotDAO.updateParking(parkingSpot), true);
    }
    
   
}
    
    /*@Test(expected = SQLException.class)
    public void testCreateWithPreparedStmntException() throws SQLException {

         //mock
         when(mockConn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException());


        try {
            UserDAO instance = new UserDAO(mockDataSource);
            instance.create(new User());
        } catch (SQLException se) {
            //verify and assert
            verify(mockConn, times(1)).prepareStatement(anyString(), anyInt());
            verify(mockPreparedStmnt, times(0)).setString(anyInt(), anyString());
            verify(mockPreparedStmnt, times(0)).execute();
            verify(mockConn, times(0)).commit();
            verify(mockResultSet, times(0)).next();
            verify(mockResultSet, times(0)).getInt(Fields.GENERATED_KEYS);
            throw se;
        }

    }*/
    
   /* @Test
    public void testGetNextAvalaibleWithPreparedStatementException () throws SQLException {
    	int actualResponse = 2;
        
       
            when(con.prepareStatement(anyString())).thenReturn(ps);
            
            
            assertThrows(SQLException.class ,()-> ps.executeQuery(null)); 
            //Assertions.assertThrows(IllegalArgumentException.class, () -> m.div(5, 0));
    }
    
    @Test
    void exceptionTesting() {
     Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
      throw new IllegalArgumentException("a message");
     });
     assertEquals("a message", exception.getMessage());
    }*/
    