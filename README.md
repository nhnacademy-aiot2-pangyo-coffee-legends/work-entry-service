# **Work-Entry Service**

- **Work-Entry Service**는 Spring Boot 및 Spring Cloud 기반으로 구축된 백엔드 시스템으로, 다양한 IoT 센서로부터 출입 데이터를 수집·저장하고, 이를 실시간으로 처리합니다. WebSocket, FeignClient 등의 최신 기술을 활용하여 근태 데이터의 생성, 통계, 알림 등 종합적인 출입 관리 기능을 제공합니다.

---

## **소개**

- 직원들의 출입 데이터를 실시간으로 수집하고, 근무 시간 통계 및 출결 상태(출근, 지각, 반차 등) 를 계산합니다.

- InfluxDB를 통해 실시간 출입 데이터를 저장·조회하며, 일정 주기로 데이터를 조회하여 프론트엔드에 전달합니다.

- 하루 2회(출근/퇴근 시점) 자동으로 실행되는 근태 생성 봇을 통해 데이터를 임의 생성하여 테스트 및 모니터링이 가능하게 합니다.

- 이상 출입 감지 시, 관리자에게 이메일 알림 및 웹 페이지에 실시간 경고 메시지를 표시합니다.

---

## **주요 기능**

- 직원별 근태 데이터 생성 및 조회

- 주간 출입 통계 제공

- 1분 단위의 실시간 출입 데이터 확인

- WebSocket 기반 실시간 로그 전송

- 외부 서비스 연동을 위한 Spring Cloud FeignClient 적용

- 출입 이상 징후 감지 및 관리자 알림



---

## **아키텍처**

```sql
[Front] → Nginx → Gateway → Eureka → Member Service / Token Service
                                 ↓
                      [WorkEntry Service] → MySQL / InfluxDB / NotifyService
                                 ↑
                             Gateway → Front

```

- 출입 센서 데이터는 경남 NHN 아카데미의 InfluxDB로부터 수집됩니다.

- 서비스 내부는 계층형 구조(Domain, DTO, Repository, Service, Controller)로 구성되어 있습니다.

- 실시간 출입 수치는 WebSocket으로 프론트엔드에 전송됩니다.

- 외부 시스템과의 연동은 Spring Cloud의 FeignClient를 통해 구현합니다.

---

## **폴더 구조**

```bash
src/
└── main/
    └── java/
        └── com/
            └── nhnacademy/
                └── workentry/
                    ├── WorkEntryApplication.java
                    ├── config/
                    │   ├── InfluxConfig.java
                    │   └── RabbitMQConfig.java
                    ├── controller/
                    │   ├── AttendanceBotController.java
                    │   └── EntryController.java
                    ├── dto/
                    │   └── EmailRequest.java
                    ├── entry/
                    │   ├── controller/
                    │   │   └── EntryController.java
                    │   ├── dto/
                    │   │   └── EntryCountDto.java
                    │   ├── realtime/
                    │   │   ├── dto/
                    │   │   │   └── EntryRealtimeDto.java
                    │   │   └── service/
                    │   │       ├── EntryRealtimeService.java
                    │   │       └── impl/
                    │   │           └── EntryRealtimeServiceImpl.java
                    │   └── service/
                    │       ├── EntryService.java
                    │       └── impl/
                    │           └── EntryServiceImpl.java
                    ├── influxdb/
                    │   └── config/
                    │       └── InfluxConfig.java
                    ├── log/
                    │   ├── config/
                    │   │   └── WebSocketConfig.java
                    │   └── realtime/
                    │       ├── LogWebSocketHandler.java
                    │       └── WebSocketContextHolder.java
                    ├── notify/
                    │   ├── adapter/
                    │   │   └── NotifyAdapter.java
                    │   └── config/
                    │       └── RabbitMQConfig.java
                    └── time/
                        ├── impl/
                        │   ├── SystemTodayProvider.java
                        │   └── TodayProvider.java
                        └── CommonAdvice.java

```

---

## **기술 스택**

| 분류         | 기술 스택                                |
|--------------|-------------------------------------------|
| **언어**      | Java 21 이상                              |
| **프레임워크** | Spring Boot, Spring Cloud, Spring Data JPA |
| **데이터베이스** | MySQL, InfluxDB                            |
| **비동기 통신** | WebSocket, RabbitMQ                        |
| **API 연동**   | FeignClient                               |
| **유틸리티**   | Lombok                                    |


---

![git](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![java21](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![mysql](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![intellijIdea](https://img.shields.io/badge/IntelliJ_IDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

---

## 📈 근무시간 통계 시각화 기능

해당 기능은 사원의 근무기록을 기반으로 **주간 근무시간 변화**와 **근태 상태(출근, 지각 등)** 를  
**Chart.js** 기반 그래프와 테이블로 직관적으로 시각화하여 HR 관리자에게 실시간 통계 정보를 제공합니다.

---

### ✅ 주요 기능 요약

| 항목 | 설명 |
|------|------|
| **사원 선택 & 날짜 필터** | 연도 / 월 / 일 기준으로 근무 통계 범위 설정 가능 |
| **근태 상태별 필터(체크박스)** | `출근`, `지각`, `결근`, `외근`, `연차`, `질병/임원`, `반차`, `상(喪)` 중 선택 |
| **Chart.js 시각화** | 주 단위 일별 근무시간 막대그래프 제공 (색상별 렌더링) |
| **근태 테이블 출력** | 근태 코드별 색상(배경) 적용된 테이블과 평균 근무시간 표시 |
| **필터링 효과** | 선택된 근태 유형만 시각화 및 테이블 반영되어 **1석 3조 효과** |

---

## 🖼 화면 예시

### 초기조회화면
![image](https://github.com/user-attachments/assets/bb654069-0cb3-4858-82c2-c91fdf4c60ba)

### 주간 일별 근무시간 표시
![image](https://github.com/user-attachments/assets/f8bf112f-8989-42aa-a01c-4e77bda9aa19)

### 근태 종류별 필터링 적용 후
![image](https://github.com/user-attachments/assets/b8068f9b-be99-48d6-85b9-5f944dd9710d)

---

## 🗂 디렉토리 구조 (근무시간 통계)

```bash
📦 work-entry-service
 ┣ 📂 attendance
 ┃ ┣ 📂 config
 ┃ ┃ ┗ 📄 QuerydslConfig.java
 ┃ ┣ 📂 controller
 ┃ ┃ ┗ 📄 AttendanceController.java
 ┃ ┣ 📂 dto
 ┃ ┃ ┣ 📄 AttendanceDto.java
 ┃ ┃ ┣ 📄 AttendanceRequest.java
 ┃ ┃ ┗ 📄 AttendanceSummaryDto.java
 ┃ ┣ 📂 entity
 ┃ ┃ ┣ 📄 Attendance.java
 ┃ ┃ ┗ 📄 AttendanceStatus.java
 ┃ ┣ 📂 repository
 ┃ ┃ ┣ 📂 impl
 ┃ ┃ ┃ ┗ 📄 CustomAttendanceRepositoryImpl.java
 ┃ ┃ ┣ 📄 AttendanceRepository.java
 ┃ ┃ ┣ 📄 AttendanceStatusRepository.java
 ┃ ┃ ┗ 📄 CustomAttendanceRepository.java
 ┃ ┗ 📂 service
 ┃     ┣ 📂 impl
 ┃     ┃ ┗ 📄 AttendanceServiceImpl.java
 ┃     ┗ 📄 AttendanceService.java
```

## 📌 특징 요약
- ✔ Chart.js 기반 주간 근무시간 막대그래프
- ✔ 근태 유형별 컬러 테이블 자동 적용
- ✔ 주차별 평균 근무시간 계산
- ✔ 체크박스를 통한 근태 상태 필터링
- ✔ 단일 페이지에서 통계/그래프/표를 모두 시각화 가능

-------
## 기여자
| <img src="https://avatars.githubusercontent.com/u/97999152?v=4" width="300" height="300" alt="woo"/> | <img src="https://avatars.githubusercontent.com/u/176984173?v=4" width="300" height="300" alt="ho"/> |
|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| [김경영의 GitHub](https://github.com/rudduddl)                                                  | [김미성의 GitHub](https://github.com/Migong0311)                                                |

#### © 2025 rudduddl, Migong0311. All rights reserved.
