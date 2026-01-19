package com.hostel.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "facilities")
public class Facility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;
    
    @Column(unique = true, nullable = false)
    private String facilityName;
    
    @ManyToMany(mappedBy = "facilities")
    private Set<Hostel> hostels;
    
   
    public Facility() {}
    
    public Facility(String facilityName) {
        this.facilityName = facilityName;
    }
    
   
    public Long getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public Set<Hostel> getHostels() {
        return hostels;
    }
    
    public void setHostels(Set<Hostel> hostels) {
        this.hostels = hostels;
    }
}
