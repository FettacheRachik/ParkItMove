package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * 
 * Service Calculate Fare ticket
 *
 */
public class FareCalculatorService {
	private boolean isReccuringUser;//Variable For calculate if reccurent user
	private static final  double milliToHour = 3600000.0;
	
	
	public FareCalculatorService() {
		
	}
	
	//Getter and Setter
	
	public boolean isReccuringUser() {
		return isReccuringUser;
	}

	public void setReccuringUser(boolean isReccuringUser) {
		this.isReccuringUser = isReccuringUser;
	}
	/**
	 * Calculate Fare ticket
	 * @param ticket
	 */

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
        
        double price;
        
        //Free for under 30 min
        if (ratio <= 0.5) {
			ticket.setPrice(0);
		}
        
        else {
			if (isReccuringUser==false) {

				price= rate*ratio;
				ticket.setPrice(price);
			}else {
				price =rate*ratio*Fare.RATE_RECCURENT;
				ticket.setPrice(price);
			}
        
        }
    }
}