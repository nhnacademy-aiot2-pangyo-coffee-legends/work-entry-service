package com.nhnacademy.workentry.attendance.constant;

/**
 * 출결 상태에 대한 상수를 정의한 클래스입니다.
 * <p>
 * 이 클래스는 출결 상태(description)를 하드코딩된 문자열로 사용하는 곳에서
 * 오타나 중복을 방지하기 위해 사용됩니다.
 * </p>
 * <p>
 * 본 클래스는 인스턴스를 생성할 수 없으며, 모든 필드는 정적(static) 상수입니다.
 * </p>
 */
public final class AttendanceStatusConstants {

    public static final String STATUS_PRESENT = "출근";
    public static final String STATUS_LATE = "지각";
    public static final String STATUS_ABSENT = "결근";
    public static final String STATUS_OUTING = "외근";
    public static final String STATUS_VACATION = "연차";
    public static final String STATUS_SICK = "질병/입원";
    public static final String STATUS_EARLY_LEAVE = "반차";
    public static final String STATUS_BEREAVEMENT = "상(喪)";

    /**
     * 이 클래스는 인스턴스를 생성할 수 없습니다.
     * 생성 시도 시 {@link AssertionError}가 발생합니다.
     */
    private AttendanceStatusConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}
