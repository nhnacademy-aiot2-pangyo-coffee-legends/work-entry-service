CREATE TABLE attendance_status (
                                   code INT PRIMARY KEY,
                                   description VARCHAR(20) NOT NULL
);

-- 예시 데이터
INSERT INTO attendance_status (code, description) VALUES
                                                      (1, '출석'),
                                                      (2, '지각'),
                                                      (3, '결석'),
                                                      (4, '외출'),
                                                      (5, '휴가'),
                                                      (6, '질병/입원'),
                                                      (7, '조퇴'),
                                                      (8, '기타');