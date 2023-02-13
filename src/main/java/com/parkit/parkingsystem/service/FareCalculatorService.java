package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
	
	private static final  double milliToHour = 3600000.0;

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }
        
        //Fix calcul Hour In and Out of Enter or Exit Parking
        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();
        
        
        //Fix calcul duration Parking
        double duration = outHour - inHour;
        double ratio = duration /milliToHour;
        //Define an rate for calcul
        double rate;
        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
               rate=Fare.CAR_RATE_PER_HOUR;
                break;
            }
            case BIKE: {
                rate= Fare.BIKE_RATE_PER_HOUR;
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
        
        //Calcul Of ticket Price 
        double result= rate*ratio;
        
        //Arround Price and Set it in ticket
		ticket.setPrice(result);
        
        
    }
}