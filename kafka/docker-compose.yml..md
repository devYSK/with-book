```
version: '3.8'

services:
  kafka1:
    image: confluentinc/cp-kafka:latest
    container_name: kafka1
    hostname: kafka1
    ports:
      - "9092:9092"   # EXTERNAL_PLAINTEXT 리스너
      - "29091:29091" # INTERNAL_PLAINTEXT 리스너
      # 컨트롤러 리스너는 내부 통신용으로만 사용
    environment:
      # KRaft 모드 활성화
      KAFKA_KRAFT_MODE: "true"

      # 이 노드가 브로커와 컨트롤러 역할을 모두 수행하도록 설정
      KAFKA_PROCESS_ROLES: "broker,controller"

      # Kafka 노드의 고유 ID 설정
      KAFKA_NODE_ID: 1

      # 컨트롤러 쿼럼 투표자 설정 (노드 ID@호스트:포트 형식)
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka1:9093,2@kafka2:9095,3@kafka3:9097"

      # Kafka가 청취할 리스너 설정
      KAFKA_LISTENERS: INTERNAL_PLAINTEXT://0.0.0.0:29091,EXTERNAL_PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093

      # 리스너별 보안 프로토콜 매핑 설정
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL_PLAINTEXT:PLAINTEXT,EXTERNAL_PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT

      # 클라이언트와 다른 브로커에 광고할 리스너 설정
      KAFKA_ADVERTISED_LISTENERS: INTERNAL_PLAINTEXT://kafka1:29091,EXTERNAL_PLAINTEXT://localhost:9092

      # 브로커 간 통신에 사용할 리스너 이름 설정
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL_PLAINTEXT

      # 컨트롤러와의 통신에 사용할 리스너 이름 설정
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      # Kafka 로그 데이터 저장 디렉토리 설정
      KAFKA_LOG_DIRS: /var/lib/kafka/data

      # 자동으로 토픽 생성 허용 여부 설정
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

      # 오프셋 토픽의 복제 인수 설정
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3

      # 로그 보존 시간 설정 (시간 단위)
      KAFKA_LOG_RETENTION_HOURS: 168

      # 그룹 초기 리밸런싱 지연 시간 설정 (밀리초 단위)
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

      # 클러스터 ID 설정
      CLUSTER_ID: "Mk3OEYBSD34fcwNTJENDM2Qk"
    volumes:
      - ./kafka-data/kafka1:/var/lib/kafka/data
    networks:
      - kafka-net

  kafka2:
    image: confluentinc/cp-kafka:latest
    container_name: kafka2
    hostname: kafka2
    ports:
      - "9094:9094"   # EXTERNAL_PLAINTEXT 리스너
      - "29092:29092" # INTERNAL_PLAINTEXT 리스너
      # 컨트롤러 리스너는 내부 통신용으로만 사용
    environment:
      # KRaft 모드 활성화
      KAFKA_KRAFT_MODE: "true"

      # 이 노드가 브로커와 컨트롤러 역할을 모두 수행하도록 설정
      KAFKA_PROCESS_ROLES: "broker,controller"

      # Kafka 노드의 고유 ID 설정
      KAFKA_NODE_ID: 2

      # 컨트롤러 쿼럼 투표자 설정 (노드 ID@호스트:포트 형식)
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka1:9093,2@kafka2:9095,3@kafka3:9097"

      # Kafka가 청취할 리스너 설정
      KAFKA_LISTENERS: INTERNAL_PLAINTEXT://0.0.0.0:29092,EXTERNAL_PLAINTEXT://0.0.0.0:9094,CONTROLLER://0.0.0.0:9095

      # 리스너별 보안 프로토콜 매핑 설정
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL_PLAINTEXT:PLAINTEXT,EXTERNAL_PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT

      # 클라이언트와 다른 브로커에 광고할 리스너 설정
      KAFKA_ADVERTISED_LISTENERS: INTERNAL_PLAINTEXT://kafka2:29092,EXTERNAL_PLAINTEXT://localhost:9094

      # 브로커 간 통신에 사용할 리스너 이름 설정
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL_PLAINTEXT

      # 컨트롤러와의 통신에 사용할 리스너 이름 설정
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      # Kafka 로그 데이터 저장 디렉토리 설정
      KAFKA_LOG_DIRS: /var/lib/kafka/data

      # 자동으로 토픽 생성 허용 여부 설정
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

      # 오프셋 토픽의 복제 인수 설정
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3

      # 로그 보존 시간 설정 (시간 단위)
      KAFKA_LOG_RETENTION_HOURS: 168

      # 그룹 초기 리밸런싱 지연 시간 설정 (밀리초 단위)
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

      # 클러스터 ID 설정
      CLUSTER_ID: "Mk3OEYBSD34fcwNTJENDM2Qk"
    volumes:
      - ./kafka-data/kafka2:/var/lib/kafka/data
    networks:
      - kafka-net

  kafka3:
    image: confluentinc/cp-kafka:latest
    container_name: kafka3
    hostname: kafka3
    ports:
      - "9096:9096"   # EXTERNAL_PLAINTEXT 리스너
      - "29093:29093" # INTERNAL_PLAINTEXT 리스너
      # 컨트롤러 리스너는 내부 통신용으로만 사용
    environment:
      # KRaft 모드 활성화
      KAFKA_KRAFT_MODE: "true"

      # 이 노드가 브로커와 컨트롤러 역할을 모두 수행하도록 설정
      KAFKA_PROCESS_ROLES: "broker,controller"

      # Kafka 노드의 고유 ID 설정
      KAFKA_NODE_ID: 3

      # 컨트롤러 쿼럼 투표자 설정 (노드 ID@호스트:포트 형식)
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@kafka1:9093,2@kafka2:9095,3@kafka3:9097"

      # Kafka가 청취할 리스너 설정
      KAFKA_LISTENERS: INTERNAL_PLAINTEXT://0.0.0.0:29093,EXTERNAL_PLAINTEXT://0.0.0.0:9096,CONTROLLER://0.0.0.0:9097

      # 리스너별 보안 프로토콜 매핑 설정
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL_PLAINTEXT:PLAINTEXT,EXTERNAL_PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT

      # 클라이언트와 다른 브로커에 광고할 리스너 설정
      KAFKA_ADVERTISED_LISTENERS: INTERNAL_PLAINTEXT://kafka3:29093,EXTERNAL_PLAINTEXT://localhost:9096

      # 브로커 간 통신에 사용할 리스너 이름 설정
      KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL_PLAINTEXT

      # 컨트롤러와의 통신에 사용할 리스너 이름 설정
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER

      # Kafka 로그 데이터 저장 디렉토리 설정
      KAFKA_LOG_DIRS: /var/lib/kafka/data

      # 자동으로 토픽 생성 허용 여부 설정
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

      # 오프셋 토픽의 복제 인수 설정
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3

      # 로그 보존 시간 설정 (시간 단위)
      KAFKA_LOG_RETENTION_HOURS: 168

      # 그룹 초기 리밸런싱 지연 시간 설정 (밀리초 단위)
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0

      # 클러스터 ID 설정
      CLUSTER_ID: "Mk3OEYBSD34fcwNTJENDM2Qk"
    volumes:
      - ./kafka-data/kafka3:/var/lib/kafka/data
    networks:
      - kafka-net

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-ui
    ports:
      - "8085:8080"
    environment:
      # Kafka 클러스터 이름 설정
      KAFKA_CLUSTERS_0_NAME: local

      # Kafka 클러스터의 부트스트랩 서버 설정
      KAFKA_CLUSTERS_0_BOOTSTRAP_SERVERS: "kafka1:29091,kafka2:29092,kafka3:29093"
    depends_on:
      - kafka1
      - kafka2
      - kafka3
    networks:
      - kafka-net

networks:
  kafka-net:
    driver: bridge
```