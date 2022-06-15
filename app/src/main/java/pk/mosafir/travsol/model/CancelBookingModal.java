package pk.mosafir.travsol.model;

public class CancelBookingModal {
    private String bookingId;
    private String bookingType;

    public CancelBookingModal(String bookingId, String bookingType) {
        this.bookingId = bookingId;
        this.bookingType = bookingType;
    }
    public CancelBookingModal(){

    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }
}
