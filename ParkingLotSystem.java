package SystemDesign;
import java.util.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingLotSystem {
    public static void main(String[] args) throws InterruptedException {

        ParkingLot parkingLot = ParkingLot.getInstance(2, 2);

        Vehicle car1 = VehicleFactory.createVehicle("car", "CAR123");
        Vehicle bike1 = VehicleFactory.createVehicle("bike", "BIKE456");

        System.out.println(parkingLot.parkVehicle(car1));
        System.out.println(parkingLot.parkVehicle(bike1));

        Thread.sleep(3000); 

        System.out.println(parkingLot.exitVehicle("CAR123"));
        System.out.println(parkingLot.exitVehicle("BIKE456"));
    }
}

//  Vehicle 
abstract class Vehicle {
    private String vehicleNumber;
    private LocalDateTime entryTime;

    public Vehicle(String vehicleNumber){
        this.vehicleNumber = vehicleNumber;
        this.entryTime = LocalDateTime.now();
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public LocalDateTime getEntryTime(){
        return entryTime;
    }

    public abstract int getHourlyRate();
}

class Car extends Vehicle {
    public Car(String vehicleNumber) {
        super(vehicleNumber);
    }

    @Override
    public int getHourlyRate(){
        return 20;
    }
}


class Bike extends Vehicle {
    public Bike(String vehicleNumber) {
        super(vehicleNumber);
    }

    @Override
    public int getHourlyRate(){
        return 10;
    }
}

// ParkingSpot 
class ParkingSpot {
    private int spotNumber;
    private boolean isOccupied;
    private Vehicle vehicle;

    public ParkingSpot(int spotNumber) {
        this.spotNumber = spotNumber;
        this.isOccupied = false;
    }

    public boolean isAvailable() {
        return !isOccupied;
    }

    public void parkVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.isOccupied = true;
    }

    public void removeVehicle() {
        this.vehicle = null;
        this.isOccupied = false;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public int getSpotNumber() {
        return spotNumber;
    }
}

// singleton
class ParkingLot {
    private static volatile ParkingLot instance;
    private final List<ParkingSpot> carSpots;
    private final List<ParkingSpot> bikeSpots;
    private final int carCapacity;
    private final int bikeCapacity;

    private ParkingLot(int carCapacity, int bikeCapacity){
        this.carCapacity = carCapacity;
        this.bikeCapacity = bikeCapacity;
        this.carSpots = new ArrayList<>();
        this.bikeSpots = new ArrayList<>();

        for(int i = 0; i < carCapacity; i++){
            carSpots.add(new ParkingSpot(i + 1));
        }

        for(int i = 0; i < bikeCapacity; i++){
            bikeSpots.add(new ParkingSpot(i + 1));
        }
    }

    public static synchronized ParkingLot getInstance(int carCapacity, int bikeCapacity){
        if(instance == null) {
            instance = new ParkingLot(carCapacity, bikeCapacity);
        }
        return instance;
    }

    // parking logic
    public synchronized String parkVehicle(Vehicle vehicle){
        List<ParkingSpot> spots = (vehicle instanceof Car) ? carSpots : bikeSpots;
        for(ParkingSpot spot : spots){
            if(spot.isAvailable()){
                spot.parkVehicle(vehicle);
                return "Vehicle " + vehicle.getVehicleNumber() + " parked at spot " + spot.getSpotNumber();
            }
        }
        return "Parking Lot Full for " + vehicle.getClass().getSimpleName();
    }

    // vehicle leave
    public synchronized String exitVehicle(String vehicleNumber){
        for(ParkingSpot spot : carSpots){
            if(!spot.isAvailable() && spot.getVehicle().getVehicleNumber().equals(vehicleNumber)) {
                return calculateFee(spot);
            }
        }

        for(ParkingSpot spot : bikeSpots){
            if(!spot.isAvailable() && spot.getVehicle().getVehicleNumber().equals(vehicleNumber)) {
                return calculateFee(spot);
            }
        }

        return "Vehicle not found in parking lot";
    }

    // calculate parking fee
    private String calculateFee(ParkingSpot spot){
        Vehicle vehicle = spot.getVehicle();
        LocalDateTime exitTime = LocalDateTime.now();
        long hours = Duration.between(vehicle.getEntryTime(), exitTime).toHours();
        int fee = (int) Math.max(1, hours) * vehicle.getHourlyRate();
        spot.removeVehicle();
        return "Vehicle " + vehicle.getVehicleNumber() + " exited. Parking Fee: ₹" + fee;
    }
}

// vehcile factor
class VehicleFactory{
    public static Vehicle createVehicle(String type, String vehicleNumber) {
        switch (type.toLowerCase()) {
            case "car":
                return new Car(vehicleNumber);
            case "bike":
                return new Bike(vehicleNumber);
            default:
                throw new IllegalArgumentException("Invalid vehicle type");
        }
    }
}
