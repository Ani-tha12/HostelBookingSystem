package com.hostel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hostels")
public class Hostel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hostelId;
    
    @Column(nullable = false)
    private String hostelName;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String address;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private Boolean approved = false;
    
    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    private List<Room> rooms;
    
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    
    @ManyToMany
    @JoinTable(
        name = "hostel_facilities",
        joinColumns = @JoinColumn(name = "hostel_id"),
        inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities;
    
    // Constructors
    public Hostel() {}
    
    public Hostel(String hostelName, String city, String address, User owner) {
        this.hostelName = hostelName;
        this.city = city;
        this.address = address;
        this.owner = owner;
    }
    
    // Getters and Setters
    public Long getHostelId() {
        return hostelId;
    }
    
    public void setHostelId(Long hostelId) {
        this.hostelId = hostelId;
    }
    
    public String getHostelName() {
        return hostelName;
    }
    
    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getApproved() {
        return approved;
    }
    
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<Room> getRooms() {
        return rooms;
    }
    
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    public Set<Facility> getFacilities() {
        return facilities;
    }
    
    public void setFacilities(Set<Facility> facilities) {
        this.facilities = facilities;
    }
}