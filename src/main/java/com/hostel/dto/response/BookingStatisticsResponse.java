package com.hostel.dto.response;

public class BookingStatisticsResponse {

    private long total;
    private long confirmed;
    private long cancelled;
    private long completed;

    public BookingStatisticsResponse() {}

    public BookingStatisticsResponse(long total, long confirmed, long cancelled, long completed) {
        this.total = total;
        this.confirmed = confirmed;
        this.cancelled = cancelled;
        this.completed = completed;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(long confirmed) {
        this.confirmed = confirmed;
    }

    public long getCancelled() {
        return cancelled;
    }

    public void setCancelled(long cancelled) {
        this.cancelled = cancelled;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}