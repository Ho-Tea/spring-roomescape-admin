package roomescape.dto;

import roomescape.domain.Reservation;

public class ReservationResponseDto {
    private final Long id;
    private final String name;
    private final String date;
    private final String time;

    private ReservationResponseDto(final Long id, final String name, final String date, final String time) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public static ReservationResponseDto from(final Reservation reservation) {
        return new ReservationResponseDto(reservation.getId(), reservation.getName(), reservation.getDate().toString(), reservation.getTime().toString());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
