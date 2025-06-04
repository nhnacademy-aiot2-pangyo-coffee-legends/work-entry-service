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

```
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

```src/
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

## ** 기술 스택**

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

-------
## 기여자
| <img src="https://avatars.githubusercontent.com/u/97999152?v=4" width="300" height="300" alt="woo"/> | <img src="https://avatars.githubusercontent.com/u/176984173?v=4" width="300" height="300" alt="ho"/> |
|--------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| [김경영의 GitHub](https://github.com/rudduddl)                                                  | [김미성의 GitHub](https://github.com/Migong0311)                                                |
