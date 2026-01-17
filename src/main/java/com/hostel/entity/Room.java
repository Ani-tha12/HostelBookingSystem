package com.hostel.entity;

import com.hostel.enums.RoomType;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    
    @ManyToOne
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;
    
    @Column(nullable = false)
    private Integer totalBeds;
    
    @Column(nullable = false)
    private Integer availableBeds;
    
    @Column(nullable = false)
    private Double pricePerNight;
    
    private String description;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    
    // Constructors
    public Room() {}
    
    public Room(Hostel hostel, RoomType roomType, Integer totalBeds, Double pricePerNight) {
        this.hostel = hostel;
        this.roomType = roomType;
        this.totalBeds = totalBeds;
        this.availableBeds = totalBeds;
        this.pricePerNight = pricePerNight;
    }
    
    // Getters and Setters
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public Hostel getHostel() {
        return hostel;
    }
    
    public void setHostel(Hostel hostel) {
        this.hostel = hostel;
    }
    
    public RoomType getRoomType() {
        return roomType;
    }
    
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }
    
    public Integer getTotalBeds() {
        return totalBeds;
    }
    
    public void setTotalBeds(Integer totalBeds) {
        this.totalBeds = totalBeds;
    }
    
    public Integer getAvailableBeds() {
        return availableBeds;
    }
    
    public void setAvailableBeds(Integer availableBeds) {
        this.availableBeds = availableBeds;
    }
    
    public Double getPricePerNight() {
        return pricePerNight;
    }
    
    public void setPricePerNight(Double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
