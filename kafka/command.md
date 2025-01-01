아래는 Docker 환경에서 설정된 Kafka 컨테이너를 사용하여 Kafka 작업을 수행하기 위한 명령어들입니다. 각 명령어는 `docker exec`를 사용하여 컨테이너 내부에서 Kafka CLI 도구를 실행합니다.

---

### **1. 토픽 생성**
```bash
docker exec -it kafka1 kafka-topics --create \
  --bootstrap-server kafka1:29091 \
  --replication-factor 3 \
  --partitions 3 \
  --topic my-topic
```

- `kafka1`: Kafka 브로커 컨테이너 이름.
- `--bootstrap-server kafka1:29091`: Kafka 브로커의 내부 클러스터 통신용 포트.

---

### **2. 토픽 목록 확인**
```bash
docker exec -it kafka1 kafka-topics --list \
  --bootstrap-server kafka1:29091
```

- 클러스터에서 사용 가능한 모든 토픽을 나열합니다.

---

### **3. 토픽 정보 확인**
```bash
docker exec -it kafka1 kafka-topics --describe \
  --bootstrap-server kafka1:29091 \
  --topic my-topic
```

- 특정 토픽의 세부 정보를 확인합니다.

---

### **4. 메시지 전송 (프로듀서)**
```bash
docker exec -it kafka1 kafka-console-producer --broker-list kafka1:29091 --topic my-topic
```

- 실행 후 입력창이 나타나며, 메시지를 입력하면 토픽으로 전송됩니다.

---

### **5. 메시지 수신 (컨슈머)**
```bash
docker exec -it kafka1 kafka-console-consumer --bootstrap-server kafka1:29091 \
  --topic my-topic \
  --from-beginning
```

- `--from-beginning` 옵션은 토픽의 처음부터 모든 메시지를 가져옵니다.

---

### **6. 토픽 삭제**
```bash
docker exec -it kafka1 kafka-topics --delete \
  --bootstrap-server kafka1:29091 \
  --topic my-topic
```

- 특정 토픽을 삭제합니다.

---

### **7. 컨슈머 그룹 목록 확인**
```bash
docker exec -it kafka1 kafka-consumer-groups --bootstrap-server kafka1:29091 --list
```

- 클러스터에서 존재하는 모든 컨슈머 그룹을 나열합니다.

---

### **8. 특정 컨슈머 그룹 상세 정보**
```bash
docker exec -it kafka1 kafka-consumer-groups --bootstrap-server kafka1:29091 \
  --describe --group my-consumer-group
```

- 특정 컨슈머 그룹(`my-consumer-group`)의 상세 정보를 확인합니다.

---

### **9. 토픽 메시지 확인 (컨슈머 그룹 없이)**
```bash
docker exec -it kafka1 kafka-console-consumer --bootstrap-server kafka1:29091 \
  --topic my-topic \
  --group my-consumer-group
```

- 특정 그룹을 지정하여 메시지를 수신합니다.

---

### **10. 오프셋 리셋 (Earliest/Latest)**
```bash
docker exec -it kafka1 kafka-consumer-groups --bootstrap-server kafka1:29091 \
  --group my-consumer-group \
  --reset-offsets --to-earliest \
  --execute --topic my-topic
```

- 특정 컨슈머 그룹의 오프셋을 초기화합니다.
- `--to-earliest`는 가장 처음으로 이동시키고, `--to-latest`는 가장 최근 메시지로 이동합니다.

---

### **11. Kafka UI 접속**
Kafka UI는 `http://localhost:8085`에서 실행됩니다. 브라우저에서 해당 URL을 열어 Kafka 클러스터와 토픽 상태를 시각적으로 확인할 수 있습니다.

---

이 명령어들은 Docker 컨테이너 내부의 Kafka CLI 도구를 실행하여 작업을 수행합니다. `kafka1`, `kafka2`, `kafka3` 컨테이너 중 하나를 사용할 수 있으며, 필요에 따라 명령어에서 컨테이너 이름과 브로커 포트를 변경하면 됩니다.