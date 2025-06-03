package com.nhnacademy.workentry.common.exception;

/**
 * 출결 관련 예외의 최상위 추상 클래스입니다.
 * <p>
 * 이 클래스는 {@code RuntimeException}을 상속하며,
 * 출결 처리 중 발생할 수 있는 다양한 예외들의 상위 타입으로 사용됩니다.
 * 하위 클래스에서 구체적인 예외 상황을 정의하여 사용하십시오.
 * </p>
 *
 * <p><b>직접 인스턴스화해서는 안 되며, 반드시 서브클래스를 통해 사용해야 합니다.</b></p>
 *
 */
public abstract class AttendanceException extends RuntimeException {

    /**
     * AttendanceException 생성자.
     *
     * @param message 예외에 대한 설명 메시지
     */
    protected AttendanceException(String message) {
        super(message);
    }
}
