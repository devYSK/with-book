# 도커교과서

* https://github.com/gilbutITbook/080258

[toc]



# 1장 시작하기 전에

## 1.1 컨테이너가 IT 세상을 점령한 이유

## 1.2 대상 독자

## 1.3 실습 환경 구축하기

## 1.4 바로 활용하기

# 2장 도커의 기본적인 사용법

## 2.2 컨테이너란 무엇인가?

컨테이너란 애플리케이션과 그 실행 환경을 하나의 패키지로 묶어 어디서든 일관되게 실행할 수 있는 기술이다.

호스트명 ,IP주소, 파일시스템까지 모두 도커가 만들어낸 가상 리소스다 

![image-20241023003138721](./images//image-20241023003138721.png)

컨테이너 밖 호스트는 컨테이너를 볼 순 있지만, 컨테이너는 호스트를 볼 수는 없다.

한 컴퓨터에서 여러 애플리케이션을 실행하기 위해 했던 시도는 가상머신이다.

가상머신은 애플리케이션이 실행될 독립적 환경이 생기지만, 호스트 컴퓨터의 운영체제를 공유하지 않고 별도의 운영체제를 필요로 한다. 각각의 가상머신은 별도의 운영체제를 가지므로 자원을 상당히 차지하며 라이선스 비용과 업데이트 부담이 늘어난다. 가상머신은 격리는 달성할 수 있지만 밀집은 달성하지 못한다

컨테이너는 호스트의 운영체제를 공유하므로 리소스가 경감되며 가상머신에 비해 더 많은 애플리케이션을 실행할 수 있다.

## 2.5 도커가 컨테이너를 실행하는 원리

도커 컨테이너를 실행하는 일은 여러 컴포넌트가 관여한다

![image-20241023004210273](./images//image-20241023004210273.png)

* 도커 엔진은 도커 관리 기능을 맡는 컴포넌트. 이미지 캐시, 가상 네트워크, 도커 리소스를 만드는 일도 담당하며, 항시 동작하는 백그라운드 프로세스 
  * 도커 API를 통해 맡은 기능을 수행하며 표준 HTTP 기반 REST API다. 

- **도커 엔진 API 레퍼런스**
  https://docs.docker.com/engine/api/
  도커 엔진 API의 최신 버전 및 이전 버전에 대한 상세한 문서를 제공.
- **도커 SDK 및 API 가이드**
  https://docs.docker.com/develop/sdk/
  다양한 프로그래밍 언어용 도커 SDK와 API 사용 예제를 확인
- **도커 HTTP API 사용 예제**
  https://docs.docker.com/engine/api/sdk/examples/
  도커 API를 활용한 실제 코드 예제

```
컨테이너(Container) 관련 API

GET /containers/json: 실행 중인 컨테이너 목록 조회
POST /containers/create: 새로운 컨테이너 생성
POST /containers/{id}/start: 컨테이너 시작
POST /containers/{id}/stop: 컨테이너 중지
GET /containers/{id}/logs: 컨테이너 로그 확인
DELETE /containers/{id}: 컨테이너 삭제
이미지(Image) 관련 API

GET /images/json: 이미지 목록 조회
POST /images/create: 이미지 가져오기(pull)
POST /build: 이미지 빌드
DELETE /images/{name}: 이미지 삭제
네트워크(Network) 관련 API

GET /networks: 네트워크 목록 조회
POST /networks/create: 새로운 네트워크 생성
POST /networks/{id}/connect: 컨테이너를 네트워크에 연결
POST /networks/{id}/disconnect: 컨테이너를 네트워크에서 분리
DELETE /networks/{id}: 네트워크 삭제
볼륨(Volume) 관련 API

GET /volumes: 볼륨 목록 조회
POST /volumes/create: 새로운 볼륨 생성
DELETE /volumes/{name}: 볼륨 삭제
시스템(System) 관련 API

GET /info: 도커 시스템 정보 확인
GET /version: 도커 버전 정보 확인
GET /events: 도커 이벤트 스트림 수신
Exec 관련 API

POST /containers/{id}/exec: 컨테이너에서 명령 실행 준비
POST /exec/{id}/start: 준비된 명령 실행
POST /exec/{id}/resize: 실행 중인 명령의 터미널 크기 조정
플러그인(Plugin) 관련 API

GET /plugins: 플러그인 목록 조회
POST /plugins/pull: 플러그인 설치
DELETE /plugins/{name}: 플러그인 삭제
시크릿(Secret) 및 컨피그(Config) 관련 API

GET /secrets: 시크릿 목록 조회
POST /secrets/create: 새로운 시크릿 생성
GET /configs: 컨피그 목록 조회
POST /configs/create: 새로운 컨피그 생성
```

호스트와 원격에서 사용할 수 있는 각 도커 REST API의 사용법 예시입니다. 이 예시들은 `curl` 명령을 사용하며, 호스트에서는 UNIX 소켓을, 원격에서는 TCP 소켓을 통해 도커 데몬과 통신합니다.

**참고:** 원격으로 도커 API에 접근할 때는 보안에 유의해야 합니다. 실제 환경에서는 TLS를 사용하여 연결을 보호하는 것이 권장됩니다.

---

### 컨테이너(Container) 관련 API

#### 1. **GET /containers/json**: 실행 중인 컨테이너 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/containers/json
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/containers/json
  ```

#### 2. **POST /containers/create**: 새로운 컨테이너 생성

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d '{"Image": "alpine", "Cmd": ["echo", "Hello, World!"]}' \
       -X POST http://localhost/v1.41/containers/create
  ```

- **원격에서 실행**

  ```bash
  curl -H "Content-Type: application/json" \
       -d '{"Image": "alpine", "Cmd": ["echo", "Hello, World!"]}' \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/containers/create
  ```

#### 3. **POST /containers/{id}/start**: 컨테이너 시작

- **호스트에서 실행**

  ```bash
  CONTAINER_ID=$(curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
                      -d '{"Image": "alpine", "Cmd": ["sleep", "60"]}' \
                      -X POST http://localhost/v1.41/containers/create | jq -r '.Id')

  curl --unix-socket /var/run/docker.sock -X POST http://localhost/v1.41/containers/$CONTAINER_ID/start
  ```

- **원격에서 실행**

  ```bash
  CONTAINER_ID=$(curl -H "Content-Type: application/json" \
                      -d '{"Image": "alpine", "Cmd": ["sleep", "60"]}' \
                      -X POST http://DOCKER_HOST_IP:2375/v1.41/containers/create | jq -r '.Id')
  
  curl -X POST http://DOCKER_HOST_IP:2375/v1.41/containers/$CONTAINER_ID/start
  ```

#### 4. **POST /containers/{id}/stop**: 컨테이너 중지

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -X POST http://localhost/v1.41/containers/$CONTAINER_ID/stop
  ```

- **원격에서 실행**

  ```bash
  curl -X POST http://DOCKER_HOST_IP:2375/v1.41/containers/$CONTAINER_ID/stop
  ```

#### 5. **GET /containers/{id}/logs**: 컨테이너 로그 확인

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock \
       "http://localhost/v1.41/containers/$CONTAINER_ID/logs?stdout=1&stderr=1"
  ```

- **원격에서 실행**

  ```bash
  curl "http://DOCKER_HOST_IP:2375/v1.41/containers/$CONTAINER_ID/logs?stdout=1&stderr=1"
  ```

#### 6. **DELETE /containers/{id}**: 컨테이너 삭제

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -X DELETE http://localhost/v1.41/containers/$CONTAINER_ID
  ```

- **원격에서 실행**

  ```bash
  curl -X DELETE http://DOCKER_HOST_IP:2375/v1.41/containers/$CONTAINER_ID
  ```

---

### 이미지(Image) 관련 API

#### 1. **GET /images/json**: 이미지 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/images/json
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/images/json
  ```

#### 2. **POST /images/create**: 이미지 가져오기(pull)

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -X POST \
       "http://localhost/v1.41/images/create?fromImage=alpine&tag=latest"
  ```

- **원격에서 실행**

  ```bash
  curl -X POST "http://DOCKER_HOST_IP:2375/v1.41/images/create?fromImage=alpine&tag=latest"
  ```

#### 3. **POST /build**: 이미지 빌드

- **호스트에서 실행**

  ```bash
  tar -cz . | curl --unix-socket /var/run/docker.sock -H "Content-Type: application/tar" \
       --data-binary @- -X POST "http://localhost/v1.41/build?t=myimage:latest"
  ```

- **원격에서 실행**

  ```bash
  tar -cz . | curl -H "Content-Type: application/tar" --data-binary @- \
       -X POST "http://DOCKER_HOST_IP:2375/v1.41/build?t=myimage:latest"
  ```

#### 4. **DELETE /images/{name}**: 이미지 삭제

- **호스트에서 실행**

  ```bash
  IMAGE_NAME="myimage:latest"
  curl --unix-socket /var/run/docker.sock -X DELETE http://localhost/v1.41/images/$IMAGE_NAME
  ```

- **원격에서 실행**

  ```bash
  IMAGE_NAME="myimage:latest"
  curl -X DELETE http://DOCKER_HOST_IP:2375/v1.41/images/$IMAGE_NAME
  ```

---

### 네트워크(Network) 관련 API

#### 1. **GET /networks**: 네트워크 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/networks
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/networks
  ```

#### 2. **POST /networks/create**: 새로운 네트워크 생성

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d '{"Name": "mynetwork", "Driver": "bridge"}' \
       -X POST http://localhost/v1.41/networks/create
  ```

- **원격에서 실행**

  ```bash
  curl -H "Content-Type: application/json" \
       -d '{"Name": "mynetwork", "Driver": "bridge"}' \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/networks/create
  ```

#### 3. **POST /networks/{id}/connect**: 컨테이너를 네트워크에 연결

- **호스트에서 실행**

  ```bash
  NETWORK_ID="mynetwork"
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d "{\"Container\": \"$CONTAINER_ID\"}" \
       -X POST http://localhost/v1.41/networks/$NETWORK_ID/connect
  ```

- **원격에서 실행**

  ```bash
  NETWORK_ID="mynetwork"
  curl -H "Content-Type: application/json" \
       -d "{\"Container\": \"$CONTAINER_ID\"}" \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/networks/$NETWORK_ID/connect
  ```

#### 4. **POST /networks/{id}/disconnect**: 컨테이너를 네트워크에서 분리

- **호스트에서 실행**

  ```bash
  NETWORK_ID="mynetwork"
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d "{\"Container\": \"$CONTAINER_ID\"}" \
       -X POST http://localhost/v1.41/networks/$NETWORK_ID/disconnect
  ```

- **원격에서 실행**

  ```bash
  NETWORK_ID="mynetwork"
  curl -H "Content-Type: application/json" \
       -d "{\"Container\": \"$CONTAINER_ID\"}" \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/networks/$NETWORK_ID/disconnect
  ```

#### 5. **DELETE /networks/{id}**: 네트워크 삭제

- **호스트에서 실행**

  ```bash
  NETWORK_ID="mynetwork"
  curl --unix-socket /var/run/docker.sock -X DELETE http://localhost/v1.41/networks/$NETWORK_ID
  ```

- **원격에서 실행**

  ```bash
  NETWORK_ID="mynetwork"
  curl -X DELETE http://DOCKER_HOST_IP:2375/v1.41/networks/$NETWORK_ID
  ```

---

### 볼륨(Volume) 관련 API

#### 1. **GET /volumes**: 볼륨 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/volumes
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/volumes
  ```

#### 2. **POST /volumes/create**: 새로운 볼륨 생성

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d '{"Name": "myvolume"}' \
       -X POST http://localhost/v1.41/volumes/create
  ```

- **원격에서 실행**

  ```bash
  curl -H "Content-Type: application/json" \
       -d '{"Name": "myvolume"}' \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/volumes/create
  ```

#### 3. **DELETE /volumes/{name}**: 볼륨 삭제

- **호스트에서 실행**

  ```bash
  VOLUME_NAME="myvolume"
  curl --unix-socket /var/run/docker.sock -X DELETE http://localhost/v1.41/volumes/$VOLUME_NAME
  ```

- **원격에서 실행**

  ```bash
  VOLUME_NAME="myvolume"
  curl -X DELETE http://DOCKER_HOST_IP:2375/v1.41/volumes/$VOLUME_NAME
  ```

---

### 시스템(System) 관련 API

#### 1. **GET /info**: 도커 시스템 정보 확인

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/info
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/info
  ```

#### 2. **GET /version**: 도커 버전 정보 확인

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/version
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/version
  ```

#### 3. **GET /events**: 도커 이벤트 스트림 수신

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/events
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/events
  ```

---

### Exec 관련 API

#### 1. **POST /containers/{id}/exec**: 컨테이너에서 명령 실행 준비

- **호스트에서 실행**

  ```bash
  EXEC_ID=$(curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
                 -d '{"Cmd": ["ls", "/"], "AttachStdout": true, "AttachStderr": true}' \
                 -X POST http://localhost/v1.41/containers/$CONTAINER_ID/exec | jq -r '.Id')
  ```

- **원격에서 실행**

  ```bash
  EXEC_ID=$(curl -H "Content-Type: application/json" \
                 -d '{"Cmd": ["ls", "/"], "AttachStdout": true, "AttachStderr": true}' \
                 -X POST http://DOCKER_HOST_IP:2375/v1.41/containers/$CONTAINER_ID/exec | jq -r '.Id')
  ```

#### 2. **POST /exec/{id}/start**: 준비된 명령 실행

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d '{"Detach": false, "Tty": false}' \
       -X POST http://localhost/v1.41/exec/$EXEC_ID/start
  ```

- **원격에서 실행**

  ```bash
  curl -H "Content-Type: application/json" \
       -d '{"Detach": false, "Tty": false}' \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/exec/$EXEC_ID/start
  ```

#### 3. **POST /exec/{id}/resize**: 실행 중인 명령의 터미널 크기 조정

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -X POST \
       "http://localhost/v1.41/exec/$EXEC_ID/resize?h=40&w=80"
  ```

- **원격에서 실행**

  ```bash
  curl -X POST "http://DOCKER_HOST_IP:2375/v1.41/exec/$EXEC_ID/resize?h=40&w=80"
  ```

---

### 플러그인(Plugin) 관련 API

#### 1. **GET /plugins**: 플러그인 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/plugins
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/plugins
  ```

#### 2. **POST /plugins/pull**: 플러그인 설치

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -X POST \
       "http://localhost/v1.41/plugins/pull?remote=plugin_name"
  ```

- **원격에서 실행**

  ```bash
  curl -X POST "http://DOCKER_HOST_IP:2375/v1.41/plugins/pull?remote=plugin_name"
  ```

#### 3. **DELETE /plugins/{name}**: 플러그인 삭제

- **호스트에서 실행**

  ```bash
  PLUGIN_NAME="plugin_name"
  curl --unix-socket /var/run/docker.sock -X DELETE http://localhost/v1.41/plugins/$PLUGIN_NAME
  ```

- **원격에서 실행**

  ```bash
  PLUGIN_NAME="plugin_name"
  curl -X DELETE http://DOCKER_HOST_IP:2375/v1.41/plugins/$PLUGIN_NAME
  ```

---

### 시크릿(Secret) 및 컨피그(Config) 관련 API

**주의:** 시크릿과 컨피그는 Docker Swarm 모드에서 사용됩니다.

#### 1. **GET /secrets**: 시크릿 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/secrets
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/secrets
  ```

#### 2. **POST /secrets/create**: 새로운 시크릿 생성

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d '{"Name": "mysecret", "Data": "'$(echo -n "secret_data" | base64)'"}' \
       -X POST http://localhost/v1.41/secrets/create
  ```

- **원격에서 실행**

  ```bash
  curl -H "Content-Type: application/json" \
       -d '{"Name": "mysecret", "Data": "'$(echo -n "secret_data" | base64)'"}' \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/secrets/create
  ```

#### 3. **GET /configs**: 컨피그 목록 조회

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock http://localhost/v1.41/configs
  ```

- **원격에서 실행**

  ```bash
  curl http://DOCKER_HOST_IP:2375/v1.41/configs
  ```

#### 4. **POST /configs/create**: 새로운 컨피그 생성

- **호스트에서 실행**

  ```bash
  curl --unix-socket /var/run/docker.sock -H "Content-Type: application/json" \
       -d '{"Name": "myconfig", "Data": "'$(echo -n "config_data" | base64)'"}' \
       -X POST http://localhost/v1.41/configs/create
  ```

- **원격에서 실행**

  ```bash
  curl -H "Content-Type: application/json" \
       -d '{"Name": "myconfig", "Data": "'$(echo -n "config_data" | base64)'"}' \
       -X POST http://DOCKER_HOST_IP:2375/v1.41/configs/create
  ```

---

**추가 참고사항:**

- **API 버전 조정:** 예시에서는 도커 엔진 API 버전 `v1.41`을 사용. 도커 엔진 버전에 따라 API 버전이 다를 수 있으니, 자신의 도커 버전에 맞게 조정이 필요.

- **`jq` 유틸리티 사용:** 일부 예제에서는 JSON 응답을 처리하기 위해 `jq`를 사용하였습니다. 설치되어 있지 않다면 다음 명령어로 설치할 수 있습니다.

  ```bash
  # Debian/Ubuntu
  sudo apt-get install jq

  # CentOS/RHEL
  sudo yum install jq
  ```

- **환경 변수 설정:** 예시에서 사용된 `$CONTAINER_ID`, `$IMAGE_NAME`, `$NETWORK_ID`, `$VOLUME_NAME`, `$EXEC_ID`, `$PLUGIN_NAME` 등은 실제 값으로 대체되어야 합니다.

- **보안 고려:** 원격으로 도커 API에 접근할 때는 반드시 TLS 설정 등을 통해 연결을 안전하게 보호해야 합니다. 그렇지 않으면 도커 데몬이 외부에 노출되어 보안 위험이 발생할 수 있습니다.

- **도커 데몬 설정:** 원격 접근을 위해 도커 데몬이 TCP 소켓을 수신하도록 설정되어 있어야 합니다. `/etc/docker/daemon.json` 파일에 다음과 같이 설정할 수 있습니다.

  ```json
  {
    "hosts": ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2375"]
  }
  ```

  **주의:** 실제 운영 환경에서는 TLS 없이 TCP 포트를 열어두는 것은 매우 위험합니다. 반드시 TLS를 사용하여 인증 및 암호화를 적용해야 합니다.



# 3장 도커 이미지 만들기

## 3.1 도커 허브에 공유된 이미지 사용하기

## 3.2 Dockerfile 작성하기

* FROM : 다른 이미지로부터 출발한다. 베이스 이미지이다
* ENV : 환경 변수 값을 지정하기 위함. key=value 형식 
* WORKDIR : 컨테이너 이미지 파일 시스템에 디렉터리 만들고 해당 디렉터리를 작업 디렉터리로 지정하는 인스트럭션. 모두 구분자로 슬래시
* COPY : 로컬 파일 시스템의 파일 혹은 디렉터리를 컨테이너 이미지로 복사. [원본경로] [복사경로]
* CMD : 도커가 이미지로부터 컨테이너 실행시 실행할 명령을 지정하는 인스트럭션 

## 3.5 이미지 레이어 캐시를 이용한 Dockerfile 스크립트 최적화

도커파일 스크립트의 인스트럭션은 잘 수정하지 않는 인스트럭션이 앞으로 오고 자주 수정되는 인스트럭션이 뒤에 오도록 배치하는것이 좋다. 그래야 캐시에 저장된 이미지를 재사용 하기 때문이다

# 4장 애플리케이션 소스 코드에서 도커 이미지까지

## 4.1 Dockerfile이 있는데 빌드 서버가 필요할까?



책에선, 팀원들이 각 다른 버전으로 로컬에서 개발하다 오류나고 환경이 맞지 않는것에 대해서 도커로 해결할 수 있다고 한다.

그 예시로 멀티 스테이지 빌드를 설명했다.

멀티스테이지 빌드는 Dockerfile 내에서 여러 개의 `FROM` 지시자를 사용하여 빌드 단계를 여러 단계로 분리하는 방법이다. 이를 통해 빌드 과정에서 필요한 도구나 종속성을 중간 단계에서 처리하고, 최종 단계에서는 필요한 산출물만을 포함하여 이미지 크기를 최적화할 수 있다.

```dockerfile
FROM diamol/base AS build-stage
RUN echo 'Building...' > /build.txt

FROM diamol/base AS test-stage
COPY --from=build-stage /build.txt /build.txt
RUN echo 'Testing...' >> /build.txt

FROM diamol/base
COPY --from=test-stage /build.txt /build.txt
CMD cat /build.txt
```

각 빌드 단계는 FROM 인스트럭션으로 시작하고 AS를 통해 이름을 붙인다.

최종 산출물은 마지막 단계의 도커 이미지이다. 

각 단계는 독립적으로 실행되지만, 앞서 만들어진 디렉터리나 파일을 복사하여 사용할 수 있다.

각 단계는 서로 격리되어 있으며, 어느 한단계서라도 실패하면 전체 빌드가 실패한다

```she
docker image build -t multi-stageFileName .
```

이런식으로, 이식성을 확보 할 수 있다. 

## 4.2 애플리케이션 빌드 실전 예제: 자바 소스 코드

메이븐과 openJDk를 사용한 도커파일이다

```dockerFile
# 빌드 단계: Maven을 사용하여 애플리케이션을 빌드합니다.
FROM diamol/maven AS builder

# 컨테이너 내 작업 디렉토리를 /usr/src/iotd로 설정합니다.
WORKDIR /usr/src/iotd

# 프로젝트의 pom.xml 파일을 현재 작업 디렉토리로 복사합니다.
COPY pom.xml .

# Maven을 사용하여 필요한 의존성을 오프라인 모드로 미리 다운로드합니다.
RUN mvn -B dependency:go-offline

# 소스 코드 전체를 현재 작업 디렉토리로 복사합니다.
COPY . .

# Maven을 사용하여 애플리케이션을 패키징(빌드)합니다.
RUN mvn package

# 실행 단계: 빌드된 애플리케이션을 실행하기 위한 OpenJDK 이미지를 사용합니다.
FROM diamol/openjdk

# 컨테이너 내 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 실행 단계로 복사합니다.
COPY --from=builder /usr/src/iotd/target/iotd-service-0.1.0.jar .

# 컨테이너가 외부와 통신할 포트 80을 엽니다.
EXPOSE 80

# 컨테이너가 시작될 때 실행할 명령어를 지정합니다.
ENTRYPOINT ["java", "-jar", "/app/iotd-service-0.1.0.jar"]
```

gradle

```dockerfile
# 빌드 단계: Gradle을 사용하여 애플리케이션을 빌드합니다.
FROM gradle:7.6-jdk11 AS builder

# 컨테이너 내 작업 디렉토리를 /usr/src/iotd로 설정합니다.
WORKDIR /usr/src/iotd

# Gradle 캐시를 활용하기 위해 build.gradle과 settings.gradle 파일을 먼저 복사합니다.
COPY build.gradle settings.gradle ./

# Gradle을 사용하여 필요한 의존성을 미리 다운로드합니다.
RUN gradle build --no-daemon -x test

# 소스 코드 전체를 현재 작업 디렉토리로 복사합니다.
COPY src ./src

# Gradle을 사용하여 애플리케이션을 패키징(빌드)합니다.
RUN gradle build --no-daemon -x test

# 실행 단계: 빌드된 애플리케이션을 실행하기 위한 OpenJDK 이미지를 사용합니다.
FROM openjdk:11-jre-slim

# 컨테이너 내 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 실행 단계로 복사합니다.
COPY --from=builder /usr/src/iotd/build/libs/iotd-service-0.1.0.jar .

# 컨테이너가 외부와 통신할 포트 80을 엽니다.
EXPOSE 80

# 컨테이너가 시작될 때 실행할 명령어를 지정합니다.
ENTRYPOINT ["java", "-jar", "/app/iotd-service-0.1.0.jar"]
```

일반적으로 나는, 외부에서 jar를 빌드하고 dockerfile에서 copy 명령어를 사용한다.



### 컨테이너간 통신에 사용되는 도커 네트워크

```she
docker network create nat
```

## 4.3 애플리케이션 빌드 실전 예제: Node.js 소스 코드

```dockerfile
# 빌드 단계: Node.js를 사용하여 애플리케이션의 의존성을 설치합니다.
FROM diamol/node AS builder

# 컨테이너 내 작업 디렉토리를 /src로 설정합니다.
WORKDIR /src

# 소스 코드의 package.json 파일을 현재 작업 디렉토리로 복사합니다.
COPY src/package.json .

# npm을 사용하여 의존성을 설치합니다.
RUN npm install

# 실행 단계: 애플리케이션을 실행하기 위한 Node.js 이미지를 사용합니다.
FROM diamol/node

# 컨테이너가 외부와 통신할 포트 80을 엽니다.
EXPOSE 80

# 컨테이너가 시작될 때 실행할 명령어를 지정합니다.
CMD ["node", "server.js"]

# 컨테이너 내 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 단계에서 설치된 node_modules를 실행 단계로 복사합니다.
COPY --from=builder /src/node_modules/ /app/node_modules/

# 소스 코드 전체를 /app 디렉토리로 복사합니다.
COPY src/ .

```



## 4.4 애플리케이션 빌드 실전 예제: Go 소스 코드

go는 네이티브 바이너리로 컴파일되는 클래스 플랫폼 언어다. 같은 코드로, 어떤 플랫폼에서든 동작하는 바이너리를 컴파일 할 수 있으며 런타임이 필요하지도 않아서 도커 이미지 크기가 매우 작다. 

```dockerf
FROM diamol/golang AS builder

COPY main.go .
RUN go build-o /server

#app
FROM diamol/base

ENV IMAGE_API_URL="http://iotd/image" \
    ACCESS_API_URL="http://accesslog/access-log"

CMD ["/web/server"]

WORKDIR web
COPY index.html
COPY --from=builder /server .
RUN chmod+x server
```

* build 단계에선 고 도구 (컴파일러 등)이 필요하긴 하다. 하지만 애플리케이션은 운영체제랑 바이너리만 있으면 된다.



## 4.5 멀티 스테이지 Dockerfile 스크립트 이해하기

장점 1. 표준화

* 어떤 os던 로컬에 무엇이 있던 상관없이 도커 컨테이너 내부에서 빌드가 이뤄지므로 신규 개발자 및 팀원들끼리 문제가 생길일이 적다.

장점 2. 성능 향상

* 멀티 스테이지 빌드 각 단계는 자신만의 캐시를 가져 재사용 하게 되면 시간을 절약할 수 있다. 

## 4.6 연습 문제

도커파일

```dockerfile
FROM diamol/golang 

WORKDIR web
COPY index.html .
COPY main.go .

RUN go build -o /web/server
RUN chmod +x /web/server

CMD ["/web/server"]
ENV USER=sixeyed
EXPOSE 80


-- 최적화 후 
FROM diamol/golang AS builder

COPY main.go .
RUN go build -o /server
RUN chmod +x /server

# app
FROM diamol/base

EXPOSE 80
CMD ["/web/server"]
ENV USER="sixeyed"

WORKDIR web
COPY --from=builder /server .
COPY index.html .
```

무엇이 달라진것인가?

1. 이미지 크기 감소
   * 첫 번째 도커파일은 `diamol/golang` 이미지를 그대로 사용해 최종 이미지를 구성하여 불필요한 빌드 도구와 라이브러리들이 포함되어 이미지 크기가 크다
   * 두 번째 도커파일에서는 **멀티스테이지 빌드**를 사용하여, 빌드 단계에서 필요한 파일만 가져와서 실행 이미지에 포함한다. `builder` 스테이지에서 `go build` 명령을 수행하고, 최종 실행용 이미지는 `diamol/base`라는 가벼운 이미지 위에 빌드된 바이너리와 필요한 파일만 복사하기 때문

2. 빌드 단계 분리로 캐싱 최적화
   1. 두 번째 도커파일은 멀티스테이지 빌드 덕분에, **빌드 단계와 실행 단계를 분리**하여 Docker가 각 단계에서 캐시를 효과적으로 활용할 수 있다
   2. 코드 수정 시 빌드 스테이지만 다시 빌드되고, 최종 이미지는 실행에 필요한 최소한의 구성만 담기 때문에 재사용이 쉬워진다. 

3. 레이어 수 감소로 빌드 속도 향상
   1. 첫 번째 도커파일은 각 명령(`RUN go build`, `RUN chmod +x /web/server`)마다 **새로운 레이어**를 생성
   2. 두 번째 도커파일은 빌드 스테이지에서의 모든 작업을 단일 단계로 처리한 후, 실행 스테이지에 필요한 바이너리 파일만 가져오므로 불필요한 레이어 생성을 줄인다. 



멀티스테이지 빌드를 사용하지 않고, **외부에서 빌드한 파일을 도커파일에서 복사**하는 방식도 성능 면에서 효과가 있다.

**이미지 크기 최적화**:

- 외부에서 컴파일된 바이너리 파일을 도커파일에서 `COPY` 명령으로 가져오면, 빌드 도구나 라이브러리가 이미지에 포함되지 않으므로 최종 이미지 크기를 줄일 수 있다.

**더 빠른 빌드 속도**:

- 도커 내부에서 빌드하지 않으므로, 빌드가 빠르다. 특히, 로컬 또는 CI/CD 파이프라인에서 고성능 하드웨어나 최적화된 환경을 사용할 경우 **빌드 시간이 단축**될 수 있다.

**환경 일관성 유지**:

- 개발 환경에서 직접 컴파일을 하고, 이미 컴파일된 바이너리 파일을 이미지에 포함하는 방식으로, **실행 환경에서의 일관성을 유지**할 수 있다.

# 5장 도커 허브 등 레지스트리에 이미지 공유하기

## 5.1 레지스트리, 리포지터리, 이미지 태그 다루기

깃허브처럼 도커 이미지를 저장소에 저장할 수 있다.

보통 4가지 단계로 나뉜다

```
docker.io/diamol/golang:latest
```

* docker.io : 레지스트리 도메인. 기본값은 도커 허브
* diamol : 이미 작성자의 계정 이름, 단체 이름, 조직 이름(orga)
* golang : 이미지 레포지토리 이름. 일반적으로 애플리케이션 이름
* latest : 이미지 태그. 애플리케이션의 버전을 나타냄. 



규모가 큰 회사는 사내 네트워크나 전용 클라우드 환경에 자사의 도커 레지스트리를 별도로 꾸민다. 



오픈소스 도구는 다음과 같이 있다. 

* harbor : Harbor는 CNCF(Cloud Native Computing Foundation) 프로젝트로, 오픈소스 컨테이너 이미지 레지스트리 
* Portus : Portus는 SUSE에서 개발한 Docker Registry 관리 도구

## 5.2 도커 허브에 직접 빌드한 이미지 푸시하기

명령어는 다음과 같다.

```
# Docker Hub 로그인. 자격증명 필요 
docker login --username '유저네임'

# 이미지 빌드
docker build -t myapp .

# Docker Hub에 맞는 태그 지정
docker tag myapp myusername/myapp:latest

# Docker Hub로 이미지 푸시
docker push myusername/myapp:latest

```



도커 레지스트리에 이미지를 푸시하면 이미지 레이어가 푸시된다.

레지스트리에서도 도커파일 스크립트처럼 같이, 캐시상에 레이어 해시와 일치하는 레이어가 없을 경우에만 실제로 업로드가 이뤄진다.  즉 레이어 캐시와 비슷하게 동작한다. 

## 5.3 나만의 도커 레지스트리 운영하기

개인 전용 레지스트리가 있으면 편한점이 많다.

인터넷 회선 사용량이 당연히 줄고 전송 시간도 줄고, 다운로드도 빠르게 할 수 있으며, 공개 레지스트리가 죽어도 사용할 수 있다.

책에서 말하는 저자의 개인 레지스트리에 이미지를 푸시하려면 https설정이 필요하다. 도커 기본 레지스트리 설정이 https를 필요로 하기 때문이다.

https를 사용하지 않는 레지스트리를 사용하려면 비보안 레지스트리 허용 목록에 추가해야 한다.

이미지 레이어 저장 경로, 포트 번호, 허용된 비보안 레지스트리 목록 등 도커의 모든 설정은 daemon.json파일에 들어있다.

* 리눅스에서는 /etc/docker에 존재

* 도커 데스크톱에서 설정 수정가능 
  * ![image-20241104135721208](./images//image-20241104135721208.png)

다음 설정 파일을 수정하고 도커 엔진 재시작 해야한다

* sudo service docker restart

```json
{
  "insecure-registries" : ["registry.local:5000"]
}
```

## 5.4 이미지 태그를 효율적으로 사용하기

이미지 태그는 보통 버저닝을 의미하며 어떤 문자열이라도 포함시킬 수 있다.

보통 소수점으로 버전을 나타낸다.

* [major].[minor],[patch]

```
docker image tag image-gallery registory.local:5000/gallery/ui:2.1.106
```

## 5.5 공식 이미지에서 골든 이미지로 전환하기

공식 허브에서 누구든 이미지를 푸시할 수 있고 누구나 내려받을 수 있다. 그러므로 보안에 취약할 수 있다.

도커 허브는 verified publisher와 official image 제도를 통해 이러한 피해를 방지한다. 

공식 이미지는 취약점 탐색을 거치고 주기적으로 업데이트되며 잘 최적화된 Dockerfile 스크립트로 구성된다.

직접 빌드한 이미지를 사용할 대 자신이 선호하는 기반 이미지로 전환하는것을 골든 이미지라고 한다.

<img src="./images//image-20241104140255314.png" width = 400>

골든 이미지라고해서 특별한것은없고, 우리가 사용하기에 좀더 편리하고 설정이 추가된 이미지이다. 

## 5.6 연습 문제

https://docs.docker.com/registry/apec/api/

* https://docker-docs.uclv.cu/registry/spec/api/

* 404 뜨길래  https://www.ibm.com/docs/ko/cloud-paks/cp-management/2.3.x?topic=apis-docker-registry-v2

연습 문제의 목표는 gallery/ui 이미지의 모든 태그를 로컬 컴퓨터의 레지스트리에 푸시 하는 것이다. 모든 태그가 푸시됐는지 확인한 다음, 삭제하고 삭제가 완료됐는지까지 확인

 다음 힌트를 참고

- ﻿﻿한 번의 image push 명령만으로 모든 태그를 푸시해야 한다.
- ﻿﻿로컬 컴퓨터의 레지스트리 API 주소는 http://registry.local:5000/x2다.
- ﻿﻿대상 리포지터리(gallery/ui)의 태그 목록을 먼저 확인하라.
- ﻿﻿그다음 이미지 매니페스트를 확인하라.
- ﻿﻿API를 통해 이미지를 삭제하라. 이때 매니페스트를 사용해야 한다.
- ﻿﻿참조 문서를 참고하라. HEAD 요청에서 꼭 필요한 요청 헤더가 있다.

```
## 리포지토리 모든 태그 목록 확인
curl http://registry.local:5000/v2/gallery/ui/tags/list

## 모든 태그 한번에 push 명령으로 푸시하기
docker push registry.local:5000/gallery/ui

## 모든 태그 푸시 확인
curl http://registry.local:5000/v2/gallery/ui/tags/list

## 이미지 매니페스트 확인. 삭제 전 삭제할 이미지의 SHA 해시를 조회
curl http://registry.local:5000/v2/gallery/ui/manifests/<tag>

## API를 통한 이미지 삭제
curl -X DELETE http://registry.local:5000/v2/gallery/ui/manifests/<digest>

## 삭제 완료 확인 404시 삭제 완료 
curl http://registry.local:5000/v2/gallery/ui/tags/list
```



# 6장 도커 볼륨을 이용한 퍼시스턴트 스토리지

컨테이너는 무상태 애플리케이션의 최적의 실행 환경이다. 이로 인해 확장성이 높아지고, 여러 개의 컨테이너 인스턴스를 생성하거나 삭제하더라도 문제가 생기지 않는다. 

그러나 어쩔수없이 파일시스템과 디스크 공간이 필요하긴 하다. 예를 들면, 애플리케이션에서 생성한 로그 파일을 저장하거나, 업로드된 파일을 처리할 때, 또는 애플리케이션이 일시적인 캐시 데이터를 보관할 때 디스크 공간이 필요하다.

## 6.1 컨테이너 속 데이터가 사라지는 이유

도커 컨테이너도 단일 드라이브로 된 파일 시스템이 있다. COPY 인스트럭션을 사용해 파일을 이미지로 복사하면 컨테이너 내부에 지정된 경로에 파일이 있다.

도커 이미지는 여러 레이어 형태로 저장되는데, 컨테이너 디스크 역시 이 이미지 레이어를 순서대로 합쳐 만든 가상 파일 시스템이다.

컨테이너가 종료되도 파일 시스템은 삭제되지 않는다.

cp 명령어로 컨테이너 내의 파일을 로컬 컴퓨터로, 혹은 컨테이너 내부로 파일을 복사할 수 있따.

```docker
## 컨테이너 to 로컬
docker cp <컨테이너 ID 또는 이름>:<컨테이너 내 파일 경로> <로컬 경로>

## 로컬 to 컨테이너
docker cp <로컬 파일 경로> <컨테이너 ID 또는 이름>:<컨테이너 내 경로>
```

컨테이너 내 파일 시스템은 단일 디스크(리눅스는 /dev/sda1, 윈도우는 C:\)다.

각 컨테이너가 공유하는 이미지 레이어는 읽기 전용이고, 쓰기 가능 레이어는 컨테이너를 실행될 때 생성되며 삭제할때 함께 삭제된다(그래서 종료하는 것만으로는 컨테이너가 삭제되지 않고, 데이터도 그대로 남아있다. )

컨테이너 속 파일을 수정하면 이미지를 공유하는 다른 컨테이너나 이미지에는 영향을 받지 않는다. 수정된 파일은 해당 컨테이너만의 기록 가능 레이어에서만 존재하기 때문. 그리고 컨테이너가 삭제되면 이 컨테이너의 기록 가능 레이어와 수정된 데이터도 함께 삭제된다. 

만약 데이터베이스 컨테이너라고 하면 지옥이라고 말할 수 있다. 도커는 이런 상황도 감안해 도커 볼륨과 마운트라는 요소로 영속 데이터를 저장할 수 있다. 

## 6.2 도커 볼륨을 사용하는 컨테이너 실행하기

도커 볼륨은 도커에서 스토리지를 다루는 단위다. 컨테이너와 독립적으로 존재하며 별도의 생애주기를 갖지만 컨테이너와 연결할 수 있다. 볼륨을 생성해 애플리케이션 컨테이너에 연결하면 컨테이너 파일 시스템의 한 디렉터리가 된다.

애플리케이션을 업데이트 하더라도 새 컨테이너에 다시 볼륨을 연결하면 데이터가 유지된다.

컨테이너에서 볼륨을 사용하는 방법은 2가지다.

1. 수동으로 직접 볼륨 생성해 연결
2. Dockerfile 스크립트에서 VOLUME 인스트럭션 사용

```dockerfile
FROM diamol/dotnet-aspnet
WORKDIR /app
ENTRYPOINT ["dotnet", "ToDoList.dll"]

VOLUME /data
COPY --from=builder /out/ .
```

컨테이너 실행시 /data 티렉터리가 있고, 해당 디렉터리 내용은 볼륨에 영구적으로 저장된다 

docker volume 명령을 사용해 목록을 확인할 수 있다

```
docker volume ls 

# docker volume prune -f < 사용하지 않는 볼륨 목록 제거 
```

도커 이미지에서 볼륨을 정의하면 컨테이너를 생성할 때마다 새로운 볼륨을 만든다.

같은 볼륨을 공유하게 할 수도 있다. volumes-from 플래그 적용시 다른 컨테이너의 볼륨을 연결할 수 있다. 

볼륨은 컨테이너가 삭제되더라도 그대로 남아있다. 즉 생명주기를 다르게 가진다. 

```
docker run -d -p 8011:80 -v 볼륨위치:/볼륨명 --name appName 이미지/태그명
```



또한 주의할점은. VOLUME 인스터럭션을 사용해 도커파일에서 빌드된 이미지로, docker run 명령어에서 볼륨을 지정하지 않으면 항상 새로운 볼륨을 함께 생성하며 무작위로 만들어진 식별자를 가지므로 이 식별자를 미리 기억하지 않으면 재사용할 수 없다. 

## 6.3 파일 시스템 마운트를 사용하는 컨테이너 실행하기

볼륨의 장점은 컨테이너와 스토리지의 생애주기를 분리하면서 도커 사용하는 방식 그대로 스토리지를 다를 수 있다.

볼륨 역시 호스트 컴퓨터에 존재하지만 컨테이너와는 분리돼있다. 호스트의 스토리지를 컨테이너에 직접적으로 연결할 수 있는 수단은 바로 바인드 마운트다. 바인드 마운트는 호스트 컴퓨터 파일 시스템의 디렉터리를 컨테이너 파일 시스템의 디렉터리로 만든다. 즉 호스트 컴퓨터의 파일에 직접 접근할 수 있다.

SSD, 디스크 어레이, NFS 분산 스토리지 등 호스트 컴에서 접근가능한 파일 시스템이라면 무엇이든 컨테이너에서 사용할 수 있다. 만약 RAID가 적용된 디스크 어레이를 가진 서버가 있다면 디비 스토리지로도 쓸 수 있다.

* RAID(Redundant Array of Independent Disks)는 여러 개의 디스크를 결합하여 하나의 디스크처럼 사용하는 기술.  RAID가 적용된 디스크 어레이는 여러 하드디스크를 하나의 논리적 단위로 묶어 운영함으로써, 데이터 중복 저장과 병렬 처리를 통해 성능과 안정성을 높이는 시스템이다. 

명령어

```
# v옵션 사용법
## 호스트의 /Users/ysk/business/data 디렉토리를 컨테이너의 /app/data 디렉토리에 마운트하려면:

docker run -v /Users/ysk/business/data:/app/data [이미지 이름]

# --mount는 더 세부적인 설정이 가능
docker run --mount type=bind,source=[호스트 디렉토리],target=[컨테이너 디렉토리] [이미지 이름]

docker run --mount type=bind,source=/Users/ysk/business/data,target=/app/data [이미지 이름]

```

* *type=bind**: 호스트의 디렉토리를 컨테이너에 바인드 마운트할 것을 지정

* **source**: 호스트의 디렉토리 경로를 지정

* **target**: 컨테이너 내부의 디렉토리 경로를 지정

파일을 읽기 전용으로 마운트하려면 `:ro` 또는 `readonly` 옵션을 추가

```
docker run -v /Users/ysk/business/data:/app/data:ro [이미지 이름]
```

바인드 마운트는 양방향으로 동작하여 컨테이너에서도 호스트에서도 수정이 가능하며 바뀐게 바로 적용된다

호스트 컴퓨터에 대한 공격 방어로 컨테이너는 파일 읽기 또는 수정 권한이 없다면 Dockerfile에서 권한을 부여할 수 있다.

```dockerfile
# 베이스 이미지 설정
FROM ubuntu:latest

# 관리자 권한이 필요한 패키지 설치 단계
USER root
RUN apt-get update && apt-get install -y sudo curl

# 컨테이너 내부에 새로운 사용자 생성
RUN useradd -m myuser && echo "myuser:mypassword" | chpasswd && adduser myuser sudo

# 특정 작업을 myuser로 실행하기 위해 사용자 변경
USER myuser

# 사용자 권한을 이용해 작업 수행
CMD ["echo", "Hello from myuser!"]
```

### NFS

*NFS(Network File System)**는 네트워크를 통해 다른 서버의 파일 시스템을 공유하는 프로토콜로  이를 통해 클라이언트 시스템이 원격 파일 시스템을 로컬처럼 사용할 수 있게한다. 

 주로 Unix와 Linux 환경에서 사용되며, 원격 파일 시스템을 마운트하여 데이터를 공유하는 데 유용하다.

Docker에서는 NFS를 사용하여 외부 스토리지나 다른 서버의 파일 시스템을 컨테이너에 볼륨으로 마운트하여 여러 컨테이너가 동일한 파일 시스템을 공유할 수 있다.

### NFS란?
**NFS(Network File System)**는 네트워크를 통해 다른 서버의 파일 시스템을 공유하는 프로토콜입니다. 이를 통해 클라이언트 시스템이 원격 파일 시스템을 로컬처럼 사용할 수 있게 해줍니다. 주로 Unix와 Linux 환경에서 사용되며, 원격 파일 시스템을 마운트하여 데이터를 공유하는 데 유용합니다.

#### NFS 볼륨을 Docker에 마운트하기
1. **NFS 서버 설정**: 먼저 NFS 서버가 설치되어 있어야 하며, 공유할 디렉터리를 설정해야 한다.

   예를 들어, `/etc/exports` 파일에 다음과 같이 공유할 디렉터리를 설정한다.

   ```bash
   /path/to/shared/folder *(rw,sync,no_subtree_check)
   ```

2. **클라이언트에서 NFS 공유 마운트 확인**: 클라이언트에서 다음 명령어로 NFS가 설치되어 있는지 확인한다.

   ```bash
   sudo apt-get install nfs-common
   ```

3. **Docker에서 NFS 볼륨 생성**: Docker에서 외부 NFS 볼륨을 생성하고 마운트하려면, `docker run` 명령어에 `--mount` 옵션을 사용하거나 `docker-compose` 파일에서 설정할 수 있다.

   - **docker run 명령어 사용**:
     ```bash
     # SSH 터널 생성 (서버와의 통신을 로컬 포트로 포워딩)
     ssh -L 2049:<NFS_SERVER_IP>:2049 -N -f user@<NFS_SERVER_IP>
     
     # Docker 명령어 실행
     docker run -d \
       --name my-container \
       --mount type=volume,source=nfs-volume,target=/mnt/shared \
       --volume-driver local \
       --opt type=nfs \
       --opt o=addr=localhost,port=2049,rw \
       --opt device=:/path/to/shared/folder \
       my-image
     
     ```
     
   - **docker-compose 파일 설정**:
     ```yaml
     version: '3.8'
     
     services:
       my-service:
         image: my-image
         volumes:
           - nfs-volume:/mnt/shared
     
     volumes:
       nfs-volume:
         driver: local
         driver_opts:
           type: "nfs"
           o: "addr=localhost,port=2049,rw"
           device: ":/path/to/shared/folder"
     ```

### 리모트 DB 서버를 여러 컨테이너에서 사용하는 방법

1. **리모트 DB 서버 준비**: 데이터베이스 서버가 리모트 환경에 있고, 컨테이너들이 접근할 수 있도록 네트워크 설정이 필요하다.. 일반적으로 데이터베이스 서버의 포트(예: MySQL의 경우 3306, PostgreSQL의 경우 5432)를 열어야 하며, 방화벽과 보안 그룹 설정을 통해 컨테이너에서 접근할 수 있는지 확인해야한다.

2. **컨테이너에서 리모트 DB 연결**:
   - **환경 변수 사용**: Docker Compose 파일이나 `docker run` 명령어에서 데이터베이스의 IP 주소와 포트를 환경 변수로 설정해 접근할 수 있다.
   
   - **예시 (docker-compose.yaml)**:
     ```yaml
     version: '3.8'
     
     services:
       my-app:
         image: my-app-image
         volumes:
           - /path/to/private_key:/root/.ssh/id_rsa:ro    # SSH 키 파일을 읽기 전용으로 마운트
         environment:
           DB_HOST: "<DB_SERVER_IP>"
           DB_PORT: "3306"
           DB_USER: "your_db_user"
           DB_PASSWORD: "your_db_password"
           SSH_KEY_PATH: "/root/.ssh/id_rsa"              # SSH 키 파일 경로 설정
           SSH_USER: "ssh_user"                           # SSH 사용자 설정
           DB_NAME: "your_database_name"                  # 데이터베이스 이름 설정
     
     ```
     
   - **예시 (docker run 명령어)**:
     ```bash
     docker run -d \
       --name my-app-container \
       -v /path/to/private_key:/root/.ssh/id_rsa:ro \    # SSH 키 파일을 읽기 전용으로 마운트
       -e DB_HOST=<DB_SERVER_IP> \
       -e DB_PORT=3306 \
       -e DB_USER="your_db_user" \
       -e DB_PASSWORD="your_db_password" \
       -e SSH_KEY_PATH="/root/.ssh/id_rsa" \              # SSH 키 파일 경로 설정
       -e SSH_USER="ssh_user" \                           # SSH 사용자 설정
       -e DB_NAME="your_database_name" \                  # 데이터베이스 이름 설정
       my-app-image
     
     ```

이 방식으로 여러 컨테이너에서 동일한 원격 DB 서버에 접근할 수 있지만 성능 최적화와 네트워크 지연을 고려해야 한다.

또다른 방법으론  **Docker 네트워크** 기능을 사용하거나, **VPN 및 프록시 설정**을 통해 여러 컨테이너에서 안전하고 효율적으로 외부 리모트 스토리지 또는 DB에 접근할 수 있다. 

### 1. Docker 네트워크 오버레이 사용
Docker Swarm이나 Kubernetes 같은 오케스트레이션 도구를 사용하면, 클러스터 내 여러 노드와 컨테이너가 서로 네트워크를 통해 쉽게 통신할 수 있다. . Docker Swarm에서는 **오버레이 네트워크(Overlay Network)**를 생성해 여러 컨테이너가 하나의 네트워크를 공유하도록 할 수 있다.

#### Docker 오버레이 네트워크 설정
1. Docker Swarm을 시작
   ```bash
   docker swarm init
   ```

2. 오버레이 네트워크를 생성
   ```bash
   docker network create -d overlay --attachable my-overlay-network
   ```

3. 컨테이너를 오버레이 네트워크에 연결하여 실행
   ```bash
   docker run -d --name app-container --network my-overlay-network my-app-image
   docker run -d --name db-container --network my-overlay-network my-db-image
   ```

이렇게 하면 `app-container`와 `db-container`가 같은 네트워크를 공유하므로, 서로 간에 DNS를 통해 이름으로 접근할 수 있다. 이를 통해 DB 서버가 외부에 있더라도 컨테이너에서 쉽게 접근할 수 있다.

### 2. VPN을 통한 네트워크 연결
외부 리모트 서버와 Docker 컨테이너 간의 안전한 통신이 필요한 경우, VPN을 사용해 네트워크를 구성할 수 있다.

#### VPN을 통한 설정 방법
1. 외부 DB 서버와 동일한 네트워크에 있는 VPN 서버를 설정
2. Docker 컨테이너 내에서 VPN 클라이언트를 설치하고, VPN을 통해 DB 서버와 연결

   예를 들어, OpenVPN을 사용해 Docker 컨테이너와 외부 리모트 DB 서버 간의 안전한 연결을 구축

   ```bash
   docker run -d --cap-add=NET_ADMIN --device /dev/net/tun \
     -v /path/to/openvpn/config.ovpn:/etc/openvpn/config.ovpn \
     --name vpn-container my-openvpn-image
   ```

3. 컨테이너에서 VPN 네트워크를 통해 DB 서버에 접근할 수 있도록 환경 변수나 네트워크 설정을 조정합니다.

### 3. SSH 터널링을 통한 데이터베이스 연결
DB 서버가 방화벽으로 보호되거나 외부에서 직접 접근할 수 없을 때는 SSH 터널링을 사용하여 연결할 수 있다. SSH 터널링을 통해 로컬에서 외부 리모트 DB 서버로 안전한 포트 포워딩을 설정할 수 있다.

## 6.4 파일 시스템 마운트의 한계점

바인드 마운트와 볼륨을 효율적으로 활용하려면 핵심 사용 시나리오와 한계쩜을 이해해야 한다.



시나리오 1. 컨테이너의 마운트 대상 디렉터리가 이미 존재하고, 이미지 레이어에 이디렉터리의 파일이 포함돼 있다면? 

이미 존재하는 대상 디렉터리에 마운트하면 마운트의 원본 디렉터리가 기존 디렉터리를 완전히 대체하여 이미지에 포함돼 있던 파일은 사용할 수 없다.



시나리오 2. 호스트 컴퓨터의 파일 하나를 컨테이너에 존재하는 디렉터리로 마운트 하면?

디렉터리의 파일이 합쳐져 이미지에서 온 파일과 호스트에서 마운트 된 파일이 모두 나타난다 (윈도 컨테이너는 제공 x)



파일 시스템은 윈도와 리눅스 의 시스템이 다르기 때문에 컨테이너의 동작이 일치하지 않는 몇 안되는 영역 중 하나다. 

> 윈도 컨테이너는 단일 파일 마운트 기능을 지원하지 않아서 디렉터리만 마운트 가능하다. 

시나리오3. 분산 파일 시스템을 컨테이너에 바운드 마인트 하면 어떻게 될까.

네트워크 상의 모든 컴퓨터에서 데이터에 접근할 수 있지만, 윈도 파일 공유 용 SMB, AWs s3등 로컬 컴퓨터 운영체제와 파일 시스템이 달라 지원하지 않는 동작이 발생할 수 있다. 



다시한번 정리.

### 1. 볼륨 마운트 (Volumes)

**볼륨 마운트**는 Docker가 관리하는 스토리지 공간을 사용하여 데이터를 저장하는 방법으로, Docker가 직접 관리하므로 호스트의 파일 시스템에 직접 연결되지 않으며, Docker의 볼륨 드라이버를 통해 데이터를 저장하고 관리한다.

#### 특징

- **관리 용이성**: Docker에서 직접 관리하므로, Docker 명령어를 통해 생성, 제거, 백업, 복원 등을 쉽게 할 수 있다.
- **호스트 독립성**: 호스트 시스템의 특정 파일 경로에 종속되지 않습니다. 데이터는 Docker가 지정한 경로에 저장되며, 여러 운영 체제에서 동일한 방식으로 사용된다.
- **보안성**: Docker가 관리하는 경로에 저장되기 때문에 컨테이너와 호스트 간의 경로 충돌이나 권한 문제가 상대적으로 적다.
- **성능 최적화**: Docker가 볼륨을 최적화하여 성능과 안정성을 제공한다. 특히 `local` 드라이버 외에도 `nfs`, `ceph`, `glusterfs` 등 다양한 외부 스토리지 드라이버와 연동이 가능하다.

### 2. 바인드 마운트 (Bind Mounts)

**바인드 마운트**는 호스트 시스템의 특정 디렉터리를 컨테이너 내부의 특정 경로에 연결하는 방법으로, 바인드 마운트를 사용하면 호스트 시스템에 있는 파일과 디렉터리를 컨테이너에서 직접 참조할 수 있다.

#### 특징

- **호스트 의존성**: 호스트 파일 시스템의 경로를 직접 사용하므로, 지정된 경로가 존재해야 하고, 운영 체제에 따라 경로가 다를 수 있다.
- **유연성**: 컨테이너가 실행될 때마다 바인드 마운트를 사용하여 다른 호스트 디렉터리를 컨테이너에 마운트할 수 있습니다. 개발 환경에서 컨테이너가 호스트 파일을 실시간으로 참조해야 할 때 유용하다.
- **권한 관리**: 호스트 파일과 직접 연결되어 있으므로 권한 문제가 발생할 수 있다. 예를 들어, 호스트에서 생성한 파일이 컨테이너의 사용자 권한과 다를 수 있다.
- **성능**: 볼륨보다 성능이 떨어질 수 있으며, 특히 네트워크 파일 시스템이나 분산 파일 시스템을 바인드 마운트하면 성능이 저하될 가능성이 있다.



## 6.5 컨테이너의 파일 시스템은 어떻게 만들어지는가?

모든 컨테이너는 도커가 다양한 출처로부터 모아 만든 단일 가상 디스크로 구성된 파일 시스템을 갖는다.

이 파일 시스템을 union file system 이라고 한다. 이 시스템은 우영체제마다 다른 방식으로 구현되어 있고, 도커가 알아서 우리가 사용하는 운영체제에 맞춰 구현을 선택해주기 때문에 상세한 구현에 대해서는 신경쓸 필요가 없다. 



여러 개의 이미지 레이어를 쌓아 올리는 도커 시스템에서, 하나 이상의 볼륨 마운트와 바인드 마운트를 컨테이너에 연결할 수 있다.

그러나 기록 가능 레이어(쓰기 레이어)는 하나박에 가질 수 없다. 

그러므로 컨테이너 스토리지 구성시 다음을 고려해야 한다

* 기록 가능 레이어(쓰기 레이어): 비용이 비싼 계싼이나 네트워크를 통해 저장해야 하는 데이터의 캐싱등 단기 저장에 적합하다. 각 컨테이너마다 독립적인 기록 가능 레이어를 갖지만 컨테이너가 삭제되면 여기 저장된 데이터는 유실된다. 
* 로컬 바인드 마운트 : 호스트 <-> 컨테이너간 데이터 공유시 사용한다. 소스코드 전달시 사용하면 이미지 빌드 없이 즉시 전달 가능
* 분산 바인드 마운트 : 네트워크 스토리지와 컨테이너간데이터 공유시 사용. 가용성이 높지만 지원하지 않는 파일시스템기능이 있거나 성능면에서 차이가 있을 수 있다. 공유 캐시로 활용할 수 있다.
* 볼륨 마운트 : 컨테이너와 도커 객체인 볼륨간 데이터 공유시 사용한다. 
* 이미지 레이어: 초기 파일 시스템 구성시 사용한다. 읽기 전용이며 여러 컨테이너가 공유한다. 

![image-20241104163209965](./images//image-20241104163209965.png)



## 6.6 연습 문제

todo-애플리케이션을 컨테이너로 실행하되 미리 등록된 할 일이 없는 상태로 실행되도록 스토리지 설정

- ﻿﻿docker rm-f S(docker ps-aq) 명령으로 먼저 모든 컨테이너를 삭제하라.
- ﻿﻿diamol/ch06-lab 이미지로 컨테이너를 실행해 현재 등록된 할 일을 확인하라.
- ﻿﻿이때 마운트를 추가해 컨테이너를 실행해야 한다.
- ﻿﻿to-do 애플리케이션의 설정 파일은 앞에서 본 로그 설정보다 좀 더 복잡하다.



# 7장 도커 컴포즈로 분산 애플리케이션 실행하기

도커 컴포즈를 사용하면 여러 컨테이너에 걸쳐 실행되는 애플리케이션을 정의하고 관리할 수 있다. 

## 7.1 도커 컴포즈 파일의 구조

Dockerfile 스크립트는 한 애플리케이션을 패키징하는 수단에 지나지 않는다.

프론트, 백엔드, 디비를 갖춘 애플리케이션을 패키징 하려면 3개의 Dockerfile 스크립트가 필요하다.

이런 방법 대신 도커 컴포즈 파일에 애플리케이션의 구조를 정의하면 된다.

도커 컴포즈 파일은 애플리케이션의 '원하는 상태' 다시 말해 모든 컴포넌트가 실행 중일 때 어떤 상태여야 하는지를 기술하는 파일이다.

도커 컴포즈 파일을 작성하고 애플리케이션을 실행하면 컴포즈가 컨테이너 네트워크 볼륨 등 필요한 모든 도커 객체를 만들도록 도커 API에 명령을 내린다. 

```yaml
version: '3.7'

services:

  todo-web:
    image: diamol/todoWeb
    networks:
      - app-net
    ports:
    	- "8020:80" 

networks:
  app-net:
    external:
      name: nat
```

* version  : 도커컴포즈 파일 버전
* servicies : 애플리케이션 구성 컴포넌트 열거 부분 
* networks : 도커 네트워크 정의 

실행.

```
# Docker Compose 명령어 정리

## 기본 명령
docker-compose up               # 모든 서비스 시작 및 빌드
docker-compose up -d            # 백그라운드에서 모든 서비스 시작
docker-compose down             # 모든 서비스 중지 및 삭제

## 상태 확인 및 로그
docker-compose ps               # 서비스 상태 확인
docker-compose logs             # 모든 서비스의 로그 확인
docker-compose logs -f          # 실시간 로그 스트리밍
docker-compose logs <서비스명>  # 특정 서비스의 로그 확인

## 서비스 관리
docker-compose start            # 모든 서비스 시작
docker-compose stop             # 모든 서비스 중지
docker-compose restart          # 모든 서비스 재시작
docker-compose kill             # 강제 종료

## 특정 서비스 실행
docker-compose up <서비스명>           # 특정 서비스만 실행
docker-compose up -d <서비스명>        # 특정 서비스를 백그라운드에서 실행

## 환경 변수와 구성 파일 지정
docker-compose --env-file <파일명> up  # 특정 환경 변수 파일 사용
docker-compose -f <파일명> up          # 특정 Compose 파일 사용

## 서비스 상태 점검 및 명령 실행
docker-compose exec <서비스명> <명령어>  # 실행 중인 특정 서비스에서 명령 실행
docker-compose run <서비스명> <명령어>   # 새로운 컨테이너에서 명령 실행

## 빌드 및 이미지 관리
docker-compose build                   # 모든 서비스 이미지 빌드
docker-compose build --no-cache        # 캐시 없이 이미지 빌드
docker-compose pull                    # 정의된 이미지 가져오기
docker-compose push                    # 정의된 이미지 푸시

## 볼륨과 네트워크 관리
docker-compose down --volumes          # 모든 서비스와 볼륨 삭제
docker-compose down --remove-orphans   # 정의되지 않은 컨테이너 삭제
docker-compose up --force-recreate     # 모든 컨테이너 강제 재생성
docker-compose up --no-deps <서비스명> # 종속 없이 특정 서비스 실행
```





## 7.2 도커 컴포즈를 사용해 여러 컨테이너로 구성된 애플리케이션 실행하기

```yaml
version: '3.7'

services:
  # accesslog 서비스는 이미지 로그를 관리하는 컨테이너입니다.
  # 이 서비스는 'diamol/ch04-access-log' 이미지를 기반으로 생성됩니다.
  # 다른 서비스들과 통신하기 위해 'app-net' 네트워크에 연결됩니다.
  accesslog:
    image: diamol/ch04-access-log
    networks:
      - app-net

  # iotd 서비스는 "오늘의 이미지"를 제공하는 컨테이너입니다.
  # 'diamol/ch04-image-of-the-day' 이미지를 사용하여 생성됩니다.
  # 이 서비스는 외부 포트 80으로 노출되어, 클라이언트가 접근할 수 있습니다.
  # 또한, 'app-net' 네트워크에 연결되어 다른 서비스와 통신이 가능합니다.
  iotd:
    image: diamol/ch04-image-of-the-day
    ports:
      - "80"
    networks:
      - app-net

  # image-gallery 서비스는 이미지 갤러리를 관리하는 주요 컨테이너입니다.
  # 'diamol/ch04-image-gallery' 이미지를 사용하여 생성되며,
  # 외부 포트 8010을 컨테이너의 80번 포트에 매핑하여 접근할 수 있습니다.
  # 이 서비스는 accesslog와 iotd 서비스에 종속되어 있어,
  # 해당 서비스들이 먼저 시작된 후에야 실행됩니다.
  # 'app-net' 네트워크에 연결되어, 다른 서비스들과 통신할 수 있습니다.
  image-gallery:
    image: diamol/ch04-image-gallery
    ports:
      - "8010:80" 
    depends_on:
      - accesslog
      - iotd
    networks:
      - app-net

# 네트워크 설정
# app-net이라는 사용자 정의 네트워크를 구성합니다.
# 외부에서 관리하는 네트워크(nat)를 사용하도록 지정되어 있어
# 각 서비스가 이 네트워크를 통해 상호 연결될 수 있습니다.
networks:
  app-net:
    external:
      name: nat

```

위 컴포즈 파일의 실행을 도식화하면 다음과 같다 

<img src="./images//image-20241104165806389.png" width = 450>



## 7.3 도커 컨테이너 간의 통신

도커 컴포즈에서 실행된 컨테이너는 서로 어떻게 통신할까?

컨테이너는 도커 엔진으로부터 부여받은 가상 주소 IP를 가지며 모두 같은 네트워크로 연결하여 IP 주소를 통해 통신할 수 있다. 그러나 컨테이너가 교체되면 IP도 변경되기 때문에 DNS를 통해 서비스 디스커버리 기능을 제공한다.

컨테이너 이름은 도메인삼아 조회하면 컨테이너의 IP 주소를 찾아준다. 만약 도메인이 가리키는 대상이 컨테이너가 아니면 도커 엔진을 실행중인 컴퓨터에 요청을 보내 호스트 컴퓨터에 속한 네트워크나 인터넷의 IP 주소를 조회한다. 



하나의 도메인에 대해 DNS 조회 결과에 여러 IP 주소가 나올 수 있어, 컴포즈는 이 점을 이용해 간단한 로드 밸런싱을 구현할 수 있다. 

```yaml
version: '3.7'

services:
  app:
    image: myapp:latest
    networks:
      - app-net
    deploy:
      replicas: 3    # 동일한 서비스의 인스턴스 3개를 생성
  web:
    image: nginx:latest
    ports:
      - "80:80"
    networks:
      - app-net
    depends_on:
      - app

networks:
  app-net:
    driver: bridge
```

 방식은 Docker Compose에서 기본적으로 제공되는 간단한 로드 밸런싱 방법으로, 대규모 트래픽을 효율적으로 처리해야 할 경우에는 Docker Swarm이나 Kubernetes와 같은 별도의 로드 밸런싱 솔루션을 사용하는 것이 좋다.

## 7.4 도커 컴포즈로 애플리케이션 설정값 지정하기

environment에 환경변수를 정의할 수 있다.

secrets에 실행시 컨테이너 내부의 파일에 기록될 비밀값을 정의할 수 있다.

```yaml
version: "3.7"

services:
  todo-db:
    image: diamol/postgres:11.5
    ports:
      - "5433:5432"
    networks:
      - app-net

  todo-web:
    image: diamol/ch06-todo-list
    ports:
      - "8030:80"
    environment:
      - Database:Provider=Postgres
    depends_on:
      - todo-db
    networks:
      - app-net
    secrets:
      - source: postgres-connection
        target: /app/config/secrets.json

networks:
  app-net:

secrets:
  postgres-connection:
    file: ./config/secrets.json
```



## 7.5 도커 컴포즈도 만능은 아니다

도커 컴포즈는 멀티 컨테이너 구성에 적합하며, 대규모 애플리케이션의 프로덕션 배포에는 한계까 있다.

매우 간단한 애플리케이션 배포과정이기 때문이다.

그러나 여기까지다. 도커 스웜이나 쿠버네티스같은 완전한 컨테이너 플랫폼이 아니다. 장애로 종료되더라도 원래 상태로 돌릴 방법이 없다.

아래 그림은 도커 컴포즈를 사용하기 적합한 주기이다.

![image-20241104172240543](./images//image-20241104172240543.png)

## 7.6 연습 문제

- ﻿﻿호스트 컴퓨터가 재부팅되거나 도커 엔진이 재시작되면 애플리케이션 컨테이너도 재시작되 도록 하라.
- ﻿﻿데이터베이스 컨테이너는 바인드 마운트에 파일을 저장해 애플리케이션을 재시작하더라도 데이터를 유지할 수 있도록 하라.
- ﻿﻿테스트를 위해 웹 애플리케이션은 80번 포트를 주시하도록 하라.

```yaml
version: '3.8'

services:
  web:
    image: my-todo-app:latest
    ports:
      - "80:80"
    restart: always
    depends_on:
      - db
    networks:
      - app-net

  db:
    image: postgres:latest
    environment:
      POSTGRES_DB: todo_db
      POSTGRES_USER: todo_user
      POSTGRES_PASSWORD: todo_password
    volumes:
      - db_data:/var/lib/postgresql/data
    restart: always
    networks:
      - app-net

networks:
  app-net:
    external:
      name: nat

volumes:
  db_data:
```



# 8장 헬스 체크와 디펜던시 체크로 애플리케이션의 신뢰성 확보하기

## 8.1 헬스 체크를 지원하는 도커 이미지 빌드하기

도커는 컨테이너 실행할때마다 애플리케이션 상태를 확인하며, 지정한 프로세스의 실행 상태를 확인한다.

만약 프로세스가 종료됐다면 컨테이너도 종료 상태가 되므로 이것으로 애플리케이션 자체의 헬스 체크가 가능하다.

그러나 애플리케이션이 실행중이라는것이지 정상적인 상태라는것은 보장할 수 없다. 

도커는 애플리케이션의 상태가 실제 정상인지 확인할 수 있는 정보를 도커 이미지에 직접 넣을 수 있는 기능을 제공한다.

ockerfile에 `HEALTHCHECK` 지시자를 추가하여 헬스체크를 설정할 수 있다. 

```dockerfile
FROM diamol/dotnet-aspnet

ENTRYPOINT ["dotnet", "/app/Numbers.Api.dll"]
HEALTHCHECK CMD curl --fail http://localhost/health

WORKDIR /app
COPY --from=builder /out/ .
```

curl --fail은 요청 성공시 0 실패시 0아닌값을 반환하는데, 도커는 0을 헬스 체크 정상 0이외 값을 비정상으로 간주한다. 

기본값은 30초 간격으로 연속 3회 이상 실패하면 애플리케이션이 이상 상태로 간주된다. 

아래처럼도 가능하다

```dockerfile
# 기본 이미지
FROM alpine:3.18

# 애플리케이션 실행
CMD ["sh", "-c", "sleep 3600"]

# 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1
  
## 내부에서도 가능
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD sh -c "sleep 10 && curl -f http://localhost:8080/health || exit 1"
```

*  `--interval`: 헬스체크를 수행하는 간격 (기본값: `30s`)

* `--timeout`: 헬스체크 명령어의 최대 실행 시간 (기본값: `30s`)

* `--start-period`: 컨테이너 시작 후 헬스체크를 시작하기 전에 대기하는 시간 (기본값: `0s`)

* `--retries`: 헬스체크가 실패로 간주되기 전 허용되는 재시도 횟수 (기본값: `3`)



`docker-compose.yml` 파일에서도 헬스체크를 설정할 수 있다.

```yaml
version: '3.8'
services:
  web:
    image: my-web-app
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 10s
```

* `test`: 헬스체크 명령어

* `interval`: 헬스체크 간격

* `timeout`: 헬스체크 타임아웃

* `retries`: 실패 허용 재시도 횟수

* `start_period`: 초기 시작 대기 시간



헬스체크가 연속으로 실패하면 컨테이너의 상태는 **`unhealthy`**로 변경되지만 컨테이너는 종료되지 않고 계속 실행된다. 헬스체크는 모니터링하기 위한 용도이지 강제종료하거나재시작하는 기능이 아니기 때문이다

Docker Compose나 Docker CLI에서 **`restart` 정책**을 설정하면, 헬스체크 실패 시 컨테이너를 자동으로 재시작하도록 구성할 수 있다.

```
version: '3.8'
services:
  web:
    image: my-web-app
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 10s
    restart: on-failure
```

- `on-failure` : 컨테이너가 비정상 종료하거나 헬스체크 실패 시 재시작.
- `always` :어떤 이유로든 컨테이너가 종료되면 항상 재시작.
- `unless-stopped` : 명시적으로 중지된 경우를 제외하고 항상 재시작.

## 8.2 디펜던시 체크가 적용된 컨테이너 실행하기

## 8.3 애플리케이션 체크를 위한 커스텀 유틸리티 만들기

curl이 컨테이너 상태 체크에 유용한 도구이기는 하지만 사용하지 않을 도구를 넣어봤짜 용량만 늘어나기 때문에

실제 애플리케이션 체크에는 애플리케이션과 같은 언어로 구현된 별도의 커스텀 유틸리티를 사용하는것이 좋다

장점.

* 커스텀 유틸리티 실행시 애플리케이션과 같은 도구를 사용하므로 이미지에 추가 소프트웨어를 추가할 필요가 없음
* 셸 스크립트로는 구현하기 어려운 로직을 적용할 수 있음

애플리케이션 빌드, 유틸리티 빌드 로 나뉜 멀티 스테이지 빌드를 적용한 도커 파일이다

```dockerfile
FROM diamol/dotnet-sdk AS builder

WORKDIR /src
COPY src/Numbers.Api/Numbers.Api.csproj .
RUN dotnet restore

COPY src/Numbers.Api/ .
RUN dotnet publish -c Release -o /out Numbers.Api.csproj

# http check utility
FROM diamol/dotnet-sdk AS http-check-builder

WORKDIR /src
COPY src/Utilities.HttpCheck/Utilities.HttpCheck.csproj .
RUN dotnet restore

COPY src/Utilities.HttpCheck/ .
RUN dotnet publish -c Release -o /out Utilities.HttpCheck.csproj

# app image
FROM diamol/dotnet-aspnet

ENTRYPOINT ["dotnet", "Numbers.Api.dll"]
HEALTHCHECK CMD ["dotnet", "Utilities.HttpCheck.dll", "-u", "http://localhost/health"]

WORKDIR /app
COPY --from=http-check-builder /out/ .
COPY --from=builder /out/ .
```

컨테이너의 상태를 확인하기 위해 주기적으로 실행되는 명령.

여기서는 `Utilities.HttpCheck.dll`이 `/health` 엔드포인트를 호출하여 컨테이너의 상태를 확인.

## 8.4 도커 컴포즈에 헬스 체크와 디펜던시 체크 정의하기

도커 컴포즈에도 헬스 체크를 추가할 수 있다.

그리고 도커 컴포즈는 새 컨테이너로 대채하진 않지만, 종료된 컨테이너를 재시작하거나 이미지에 정의되지 않은 헬스 체크를 추가할 수 있다. 

```yaml
version: "3.7"

services:
  numbers-api:
    image: diamol/ch08-numbers-api:v3
    ports:
      - "8087:80"
    healthcheck:
      interval: 5s
      timeout: 1s
      retries: 2
      start_period: 5s
    networks:
      - app-net

  numbers-web:
    image: diamol/ch08-numbers-web:v3
    restart: on-failure
    environment:
      - RngApi__Url=http://numbers-api/rng
    ports:
      - "8088:80"
    healthcheck:
      test: ["CMD", "dotnet", "Utilities.HttpCheck.dll", "-t", "150"]
      interval: 5s
      timeout: 1s
      retries: 2
      start_period: 10s
    networks:
      - app-net

networks:
  app-net:
    external:
      name: nat

```

* interval : 헬스 체크 실시 간격
* timeout 응답 받지 못하면 실패로 간주하는 제한 시간
* retries : 이상으로 간주할 때까지 필요한 연속 실패 횟수
* start_period : 컨테이너 실행 후 첫 헬스 체크를 실시하는 시간 간격 

restart: on-failure 설정이 있으므로, 컨테이너 종료시 컨테인너를 재시작 한다. 

## 8.6 연습 문제

애플리케이션은 인위적인 메모리 누수를 일으킨다. nodejs로 구현되어 있다.



• 애플리케이션 시작 시 충분한 메모리가 있는지 확인하고, 메모리가 부족한 경우 컨테이너를 종료한다.

• 애플리케이션 실행 중 5초 간격으로 최대치를 초과해 메모리를 사용하는지 확인한다. 최대 치를 초과했다면 해당 컨테이너의 상태를 이상으로 판정해야 한다.

• 테스트 로직은 memory-check.js 스크립트에 이미 작성돼 있다. Dockerfile 스크립트에 서 테스트 스크립트를 그대로 사용하면 된다.

• 테스트 스크립트와 Dockerfile 스크립트는 ch08/Iab 디렉터리에 있다.

```javascript
const fs = require("fs");

if (!fs.existsSync("ALLOCATED_MB")) {
  console.log("Memory check: OK, none allocated.");
  process.exit(0);
}

const max = process.env.MAX_ALLOCATION_MB;
const allocated = parseInt(fs.readFileSync("ALLOCATED_MB", "utf-8"));

if (max >= allocated) {
  console.log(`Memory check: OK, allocated: ${allocated}MB, max: ${max}MB`);
  process.exit(0);
} else {
  console.log(`Memory check: FAIL, allocated: ${allocated}MB, max: ${max}MB`);
  process.exit(1);
}

-- memory-hog.js
const fs = require("fs");

function allocate() {
  const toAllocate = 1024 * 1024 * process.env.LOOP_ALLOCATION_MB;

  /* this allocates a large chunk of memory, but in this app we're just going to pretend
  var buffers = new Array();
  for (i = 0; i <= loop; i++) {
    buffers.push(Buffer.alloc(toAllocate));
  }
  */

  var allocatedMb = process.env.LOOP_ALLOCATION_MB * loop;
  console.log(`Allocated: ${allocatedMb}MB`);
  fs.writeFileSync("ALLOCATED_MB", allocatedMb, "utf-8");

  loop++;
  setTimeout(allocate, process.env.LOOP_INTERVAL_MS);
}

var loop = 1;
allocate();
```

```dockerfile
# Node.js 기반 이미지 사용
FROM diamol/node

# 환경 변수 설정
ENV MAX_ALLOCATION_MB=4096 \
    LOOP_ALLOCATION_MB=512 \
    LOOP_INTERVAL_MS=2000

WORKDIR /app

COPY src/ .

# 애플리케이션 실행 및 메모리 검사
CMD node memory-check.js && \
    node memory-hog.js

# 컨테이너 상태 확인을 위한 HEALTHCHECK 추가
HEALTHCHECK --interval=5s --timeout=3s --start-period=10s --retries=3 \
 CMD node memory-check.js
```





# 9장 컨테이너 모니터링으로 투명성 있는 애플리케이션 만들기

옵저버빌리티(투명성 이라고 표현하는데 보통 관측하다 라는 의미)은 매우 중요한 요소이다.

그라파나와 프로메테우스를 사용해 애플리케이션 컨테이너에서 측정된 수치를 수집하고 그라파나를 사용해 시각화 할 수 있다. (프로메테우스는 스케일 아웃이 안된다. 때문에 미미르라는 새로운 수집 도구가 나왔다.)

도커 엔진의 측정값도 추출할 수 있다.

도커 엔진의 설정은 daemon.json이라는 이름이 파일에 포함되어있는데, 이 파일에서 설정 파일을 열어 수정하면 된다

```
mac은
1. Docker Desktop 열기.
2. Settings (설정) → Docker Engine 선택.
3. JSON 형식으로 도커 엔진 설정 편집 가능.

리눅스는
보통 /etc/docker 아래에 있는데, 없으면 생성해서 쓰면 됌 
```

설정 파일을 열고 다음 두 값을 추가하면 9323 포트를 통해 측정 값이 공개된다

```
"metrics-addr" : "0.0.0.0:9323",
"experimental" : true
```

```
curl http://localhost:9323
```

출력 포맷은 프로메테우스 포맷이다. 상태 정보가 이름 값 쌍 형태로 표현된다.

## 9.1 컨테이너화된 애플리케이션에서 사용되는 모니터링 기술 스택

## 9.2 애플리케이션의 측정값 출력하기

프로메테우스는 http 폴링으로 애플리케이션의 상태정보를 수집한다.

그래서 각 언어마다 보통 라이브러리를 지원한다

* java - micrometer 
* go : promhttp
* nodejs : prom-client

프로메테우스 클라이언트 라이브러리를 이용하면 자동으로 측정해주며, 수집된 정보는 런타임 수준의 측정값이다.

도커 엔진에서 얻은 측정값과는 또 다른 수준의 정보이며 상당한 로우한 정보까지도 얻을 수 있다. 

어떤 값을 수집할지는 보통 아래의 기준으로 한다.

**애플리케이션 성능 관련 메트릭**

- **요청 처리 시간(latency)**: 각 요청이나 작업의 평균, 최소, 최대, 그리고 95/99퍼센타일 응답 시간.
- **애플리케이션 스루풋(throughput)**: 초당 처리되는 요청 수.
- **에러율(error rate)**: 실패한 요청의 비율 (HTTP 5xx, 비정상 상태 코드 등).
- **큐 대기 시간**: 작업 큐에서 대기하는 시간 (있다면).

**리소스 사용량**

- **CPU 사용량**: 전체 및 개별 코어의 사용률.
- **메모리 사용량**: 애플리케이션 메모리 사용량, GC(Garbage Collection) 횟수 및 지연 시간.
- **디스크 사용량 및 I/O**: 읽기/쓰기 속도, 디스크 사용률.
- **네트워크 사용량**: 송/수신된 데이터 양, 대역폭 사용률.

**데이터베이스 메트릭**

- **쿼리 성능**: 각 쿼리의 응답 시간 및 실패율.
- **연결 수(pool usage)**: 데이터베이스 연결 풀 사용량.
- **캐시 히트율**: 캐시된 데이터의 활용도 (e.g., Redis, Memcached).
- **슬로우 쿼리 로그**: 지연된 쿼리 정보.

**애플리케이션 상태**

- **스레드 수**: 실행 중인 스레드 및 스레드 풀 상태.
- **에러 및 예외 발생 빈도**: 애플리케이션의 주요 예외 및 로그 기반 에러 분석.
- **상태 전이 이벤트**: 애플리케이션의 상태 전환, 서비스 시작/종료.

**사용자 행동 관련 메트릭**

- **유입 경로 및 트래픽 패턴**: 사용자 활동 경로 분석.
- **사용자 세션 수**: 활성 세션, 세션 지속 시간.
- **기기/브라우저 정보**: 사용자가 애플리케이션에 접속한 기기 및 환경 정보.

**컨테이너 및 클라우드 환경 메트릭** (만약 사용 중이라면)

- **컨테이너 리소스 사용량**: CPU, 메모리, 네트워크 및 디스크 I/O.
- **오토스케일링 이벤트**: 인스턴스 추가/제거 로그.
- **노드 상태**: 클러스터 내 노드의 상태 (Ready/NotReady 등).

**보안 및 접근 메트릭**

- **인증 및 권한 요청 수**: 성공, 실패한 인증 시도.
- **비정상적인 접근 시도**: 특정 IP, 사용자, 시간대에 집중된 비정상적 트래픽.

**비즈니스 관련 메트릭**

- **거래/트랜잭션 성공률**: 주문, 결제 등 주요 트랜잭션의 성공/실패 비율.
- **매출 또는 사용자 행동 분석**: 특정 이벤트 발생 빈도 (예: 구매 완료, 장바구니 추가).
- **사용자 참여도**: 페이지뷰, 클릭수, 평균 체류 시간.

**경보 및 이상 탐지 메트릭**

- **임계값 초과**: 특정 임계값을 초과한 이벤트 수집 (e.g., CPU 90% 초과).
- **이상 징후 탐지**: 일반적인 패턴에서 벗어난 메트릭의 이상 징후.



## 9.3 측정값 수집을 맡을 프로메테우스 컨테이너 실행하기

prometheus_config.yml에서 어떤 애플리케이션을 수집할지 지정할 수 있따.

```yaml
global:
  scrape_interval: 10s

scrape_configs:
  - job_name: "image-gallery"
    metrics_path: /metrics
    static_configs:
      - targets: ["image-gallery"]

  - job_name: "iotd-api"
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ["iotd"]

  - job_name: "access-log"
    metrics_path: /metrics
    scrape_interval: 3s
    dns_sd_configs:
      - names:
          - accesslog
        type: A
        port: 80
        
  - job_name: "docker"
    metrics_path: /metrics
    static_configs:
      - targets: ["DOCKER_HOST:9323"]

```

프로메테우스  컨테이너 실행시 해당 파일을 설정 파일로 지정해주면 된다.

```yaml 
services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    restart: always
    network_mode: host
    user: root  # 명시적으로 루트 유저로 실행
    privileged: true
    ports:
      - "9090:9090"  # Prometheus 웹 UI 포트
    volumes:
      - "./prometheus.yml:/etc/prometheus/prometheus.yml" # 볼륨 파일 지정 
      - "./data/prometheus:/prometheus"  # TSDB 데이터 저장

    command:
      - '--storage.tsdb.path=/prometheus'               # 데이터베이스 경로
      - '--storage.tsdb.wal-compression'                      # WAL 파일 압축을 활성화합니다.
      - '--web.enable-lifecycle'
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.retention.time=120d' # 데이터 보존 기간 설정
      - --enable-feature=exemplar-storage
      - --enable-feature=otlp-write-receiver

    extra_hosts: ['host.docker.internal:host-gateway']
    environment:
      - TZ=Asia/Seoul  # 시간대 설정
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "5"
        compress: "true"  # 압축 활성화
```



## 9.4 측정값 시각화를 위한 그라파나 컨테이너 실행하기

프로메테우스를 통해 데이터를 수집했다면, 시각화 시키는것은 그라파나가 맡을 수 있다.

그라파나 대시보드는 쿼리를 커스텀하여 다양한 프로메테우스 수집 데이터들을 시각화하여 그래프, 테이블, 로우 등 다양하게 만들 수 있다. 

보통 프로메테우스와 그라파나를 묶어 같이 이용한다

```yaml
version: "3.7"

services:

  prometheus:
    image: diamol/ch09-prometheus
    ports:
      - "9090:9090"
    environment:
      - DOCKER_HOST=${HOST_IP}
    networks:
      - app-net

  grafana:
    image: diamol/ch09-grafana
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
    networks:
      - app-net

networks:
  app-net:
    external:
      name: nat

```



## 9.5 투명성의 수준

observability, 프로덕트가 실제 서비스가 가능한 수준이 되려면 반드시 관측을 할 수 있어야 한다.

가장 중요한 것은 애플리케이션의 전체 상황을 조망하는 대시보드다. 측정값 중에서 가장 애플리 케이션에 중요한 데이터를 모아 하나의 화면으로 구성할 수 있어야 한다. 그래야만 한눈에 이상 상황을 파악하고 사태가 악화되기 전에 과감한 조치를 취할 수 있다. 또한, 그라파나와 프로메테우스는 특정 임계치 값이 넘어가면 알림을 보낼 수 있기도 하다. 

# 10장 도커 컴포즈를 이용한 여러 환경 구성

## 10.1 도커 컴포즈로 여러 개의 애플리케이션 배포하기

도커 컴포즈는 여러개의 컨테이너로 구성된 애플리케이션을 단일 도커 엔진 호스트에서 실행하기 위한 도구이며 개발자에게 특히 유용하므로 개발 환경이나 테스트 환경에서 쓰인다. 여러 환경 구성 사용시 주의해야할점은 같은 포트를 통해 요청을 받게 하거나 서버에 호스트에 있는 파일을 여러 컨테이너가 쓰려고 해서는 안된다. 

컴포즈가 사용하는 프로젝트 이름의 기본값을 바꿀 수 있어서 프로젝트 이름을 바꾸는 방법으로 단일 도커 호스트에 같은 애플리케이션을 여러번 실ㄹ행시킬 수 있다.

```
docker-compose -f docker-compose.yml -p todo-test up -d 
```

* todo-test가 프로젝트 이름. 이것만 바꾸면 무작위로 여러벌 실행 가능.

이 정도로도 충분히 유용하지만 약간 아쉬운 점이 있다. 무작위로 정해진 공개 포트를 일일이 찾아야 하는 것 은 운영 팀에게나 테스트 팀에게나 바람직한 일이 아니다. 컴포즈 파일을 복사해 필요한 부분만 수정하는 방법도 가능하겠지만, 컴포즈가 제공하는 기능 중에 더 좋은 방법이 있다. 설정을 오버 라이드 할 수 있다. 

## 10.2 도커 컴포즈의 오버라이드 파일

도커 컴포즈는 여러 파일을 합쳐 컴포즈 파일을 구성하는데, 나중에 지정된 파일의 내용이 이전 파일의 내용을 덮어쓰기 할 수 있다. 

먼저, 기본 애플리케이션 구조와 모든 환경에서 공통으로 쓰이는 속성이 정의된 docker-compose.yml 파일이 있고,

환경별로 달라진 속성을 정의할 수 있다.

![image-20241128022729598](./images//image-20241128022729598.png)

어떻게 오버라이드 하냐고? 하나 이상의 파일이 인자로 지정돼면 이들 파일을 병합한다.

```
docker-compose -f ./docker-compose.yml -f ./docker-compose-v2.yml config
```

* config 명령은 병합 후 유효한 파일일때만 최종 출력된다. 실행시키지는 않는다 

병합 파일 순서는 인자로 받은 순서를 따르며 먼저인 파일이 나중인 파일보다 우선한다. 그러므로 순서를 중요시 해야한다. 

## 10.3 환경 변수와 비밀값을 이용해 설정 주입하기

도커 컴포즈를 이용해 환경 변수를 주입할 수 있다. 

비밀 값도 주입할 수 있다.

```
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list
    secrets:
      - source: todo-db-connection
        target: /app/config/secrets.json


-- /app/config/secrets.json
{
    "ConnectionStrings": {
      "ToDoDb": "Server=todo-db;Database=todo;User Id=postgres;Password=postgres;"
    }
}
```

* source는 컨테이너 런타임이 비밀값을 읽ㅇ오는 위치
* target은 컨테이너 안에 비밀값이 위치할 경로이다

환경변수는 아래처럼 넣는다.

```yaml
version: "3.7"

services:
  todo-web:
    ports:
      - "${TODO_WEB_PORT}:80"
    environment:
      - Database:Provider=Postgres
    env_file:
      - ./config/logging.information.env
    networks:
      - app-net

  todo-db:
    image: diamol/postgres:11.5
    ports:
      - "${TODO_DB_PORT}:5432"
    networks:
      - app-net

networks:
  app-net:
    name: todo-test

secrets:
  todo-db-connection:
    file: ./config/secrets.json
```

* environment 프로퍼티는 컨테이너 안에서만 사용되는 환경변수를 넣는다.
* env_file 프로퍼티는 텍스트 파일의 경로를 받아서 해당 텍스트 파일에 정의된 환경 변수가 컨테이너에 적용된다 
  * 내부적으로 키=값 형태로 한줄에 하나씩 넣는다
* ${TODO_DB_PORT}는 호스트 컴퓨터의 환경변수 값으로 치환되어 들어간다.
  * 만약 ${TODO_DB_PORT}가 5432면 5432:5432가 되는것이다 

## 10.4 확장 필드로 중복 제거하기

서비스간 많은 설정값을 공유하는 컴포즈 파일이 덩치가 점점 커질 수도 있는 문제가 있다. 

이 문제를 해결하기 위해선, YAML의 여러 블록을 한곳에서 정의하는 확장 필드를 이용할 수 있다. 

컴포즈 파일 전체 스크립트에 걸쳐 이블록을 재사용하는 효과를 얻을 수 있다. 

```yaml
version: "3.7"

x-labels: &logging
  logging:  
    options:
      max-size: '100m'
      max-file: '10'

x-labels: &labels
  app-name: image-gallery

services:
  accesslog:
    <<: *logging
    labels:
      <<: *labels

  iotd:
    ports:
      - 8080:80
    <<: *logging
    labels:
      <<: *labels
      public: api

  image-gallery:
    ports:
      - 80:80
    <<: *logging
    labels:
      <<: *labels
      public: web

  prometheus:
    image: diamol/ch09-prometheus
    ports:
      - "9090:9090"
    environment:
      - DOCKER_HOST=${HOST_IP}
    networks:
      - app-net
    <<: *logging
    labels:
      <<: *labels

networks:
  app-net:
      name: image-gallery-prod
```

확장 필드는 사용자 정의 필드이며, logging과 lables라는 두개의 확장 필드가 있다.

블록은 관습적으로 x로 시작하는 이름을 붙인다. 확장 필드 재사용시 YAML 병합 문법인 <<:*필드명 과 같이 쓴다.

해당 위치에 x-lables: &logging의 값인 logging:... 값이 들어가는 것이다. 



## 10.5 도커를 이용한 설정 워크플로 이해하기

지금까지 도커 컴포즈를 봤을때, 환경간 설정 차이를 정의하는 법과 관리하는 법을 배웠다.

1. 애플리케이션 구성 요소의 조합 : 오버라이드 파일을 이용해서 공통 서비스는 두고 환경마다 필요한 서비스만 설정함
2. 컨테이너 설정 : 포트 충돌, 볼륨 설정 등을 설정했다. 각 다르게 해서 여러 애플리케이션을 실행 가능하다 
3. 애플리케이션 설정 : 컨테이너별로 내부 동작이 다르도록 환경 변수, 비밀 값 등을 다르게 설정할 수 있다. 

## 10.6 연습 문제

개발 환경과 테스트 환경을 하나의 호스트 컴퓨터에서 실행한다.

그리고 개발 환경을 docker-compose up 명령의 기본값으로 삼으며 다음과 같이 설정한다.

- ﻿﻿로컬 파일 데이터베이스 사용
- ﻿﻿8089번 포트 공개
- ﻿﻿to-do 애플리케이션의 v2 버전 실행

테스트 환경은 프로젝트 이름과 특정 컴포즈 파일을 지정해 실행하도록 하며, 다음과 같이 설정 한다.

- ﻿﻿별도의 데이터베이스 컨테이너 사용
- ﻿﻿데이터베이스 스토리지를 위한 볼륨 사용
- ﻿﻿8080번 포트 공개
- ﻿﻿to-do 애플리케이션의 최신(latest) 버전 실행

```yaml
# 베이스파일
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list
    secrets:
      - source: todo-db-connection
        target: /app/config/secrets.json

# 개발 환경
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list:v2
    ports:
      - 8089:80
    environment:
      - Database:Provider=Sqlite

secrets:
  todo-db-connection:
    file: empty.json

# 테스트 환경
version: "3.7"

services:
  todo-web:
    ports:
      - "8080:80"
    environment:
      - Database:Provider=Postgres
    networks:
      - app-net

  todo-db:
    image: diamol/postgres:11.5
    environment:
      - PGDATA=/data
    ports:
      - "5433:5432"
    volumes:
      - "todo-database:/data"
    networks:
      - app-net

networks:
  app-net:
    name: todo-test

secrets:
  todo-db-connection:
    file: postgres-connection.json

volumes:
  todo-database: # 디비 스토리지 사용을 위한 볼륨
  
# 실행 명령어는?
개발시 : docker-compose up -d (기본값으로 삼음 )

테스트시 : 

docker-compose -f .\docker-compose.yml -f .\docker-compose-test.yml -p good up -d
```





# 11장 도커와 도커 컴포즈를 이용한 애플리케이션 빌드 및 테스트

CI는 정기 반복되며 애플리케이션을 빌드, 코드 통합, 일련 테스트를 수행하는 절차다.

도커를 이용하면 CI 서버 설치 등 작업의 대부분을 간소화 할 수 있다. 

## 11.1 도커를 이용한 지속적 통합 절차

CI 파이프라인이 끝나면 즉시 배포 가능한 결과물을 내놓는다. 파이프라인은 다양한 프로그래밍 언어와 프레임워크의 조합마다 달라질 수 있다. 도커는 CI 절차의 일관성을 유지해준다.

![image-20241128143042883](./images//image-20241128143042883.png)

CI 파이프라인 각 단계는 도커에 의해 실행되며 과정은 컨테이너 내부에서 진행된다. 

## 11.2 도커를 이용한 빌드 인프라스트럭처 구축하기

신뢰성있는 다른 서비스를 쓸 수 있는데 왜 직접 인프라를 구축하려고 할까?

보안, 속도, 인터넷 회선 다운된 비상상황 등은 외부 서비스에 의존하는 한 평생 고민해야 할 숙제다.

이를 하나만 알아두면 내부에서 처리할 수 있다.  

## 11.6 연습 문제

# 12장 컨테이너 오케스트레이션: 도커 스웜과 쿠버네티스

대량의 트래픽을 처리하기 위해 요러 대의 도커 호스트로 구성된 운영 환경을 다루는 법을 배운다.

여러 대의 도커 호스트와 컨테이너를 관리하는 레이어를 오케스트레이션이라고 하며 도커 스웜과 쿠버네티스가 있다.

## 12.1 컨테이너 오케스트레이션 도구란?

오케스트레이션 도구란 클러스터를 구성하는 여러대의 호스트 컴퓨터다. 오케스트레이션 도구는 컨테이너를 관리하고, 서비스를 제공하기 위한 작업을 여러 컴퓨터에 분배하며, 네트워크 트래픽 부하를 고르게 분산시키고 상태 불량한 컨테이너를 새 컨테이너로 교체한다. 

여러대의 호스트에 도커를 설치해 클러스터를 만들고 이들 컴퓨터를 스웜이나 쿠버네티스에 등록하낟. 다음부터는 명령행 도구 등을 통해 원격에서 클러스터를 관리할 수 있다.

![image-20241128154317341](./images//image-20241128154317341.png)

클러스터에는 배포된 애플리케이션에 대한 모든 정보가 담긴 분산 데이터베이스와 어떤 컨테이너를 어떤 호스트에서 실행할지 배정하는 스케줄러, 헬스체커 등 시스템이 있다. 클러스터에 애플리케이션을 배포하려면 yaml 파일을 전달하면 된다. 

![image-20241128154441796](./images//image-20241128154441796.png)



## 12.2 도커 스웜으로 클러스터 만들기

도커 스웜은 도커 엔진에 포함돼 있어 별도 설치가 필요 없다. 엔진을 스웜 모드로 전환해 클러스터를 초기화 하면 된다

```
docker swarm init
```

```
ysk 🌈   ~  docker swarm init
Swarm initialized: current node (uhjgl80mg68jbw0nm4nh8gdw2) is now a manager.

To add a worker to this swarm, run the following command:

# 아래는 다른 컴퓨터를 현재 생성한 스웜에 참여시킬때 입력해야 할 명령어 
    docker swarm join --token SWMTKN-1-4jldxlymxqa1o5b1in2auo1cduj0f3nlpxhslzwlrbqxyo5fha-dk7geoszg7c9s7pdfa2m40gqb 192.168.65.3:2377

To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.

```

스웜을 만들고 나면 원하는 만큼 컴퓨터를 스웜에 추가할 수 있다. 스웜에 추가된 컴퓨터를 노드라고 하고, 같은 네트워크상에 있어야 노드로 추가할 수 있다. 매니저로부터 토큰을 발급받아야 스웜에 추가 가능하다 

```
# 워커 노드로 스웜에 참여하기 위한 명령을 화면에 출력
docker swarm join-token worker

# 매니저 노드로 스웜에 참여하기 위한 명령을 화면에 출력
docker swarm join-token manager

#스웜에 참여 중인 노드의 목록을 출력
docker node 1s

ID                            HOSTNAME         STATUS    AVAILABILITY   MANAGER STATUS   ENGINE VERSION
uhjgl80mg68jbw0nm4nh8gdw2 *   docker-desktop   Ready     Active         Leader           26.1.1
```

* 같은 네트워크 상에있는 어떤 컴퓨터라도 매니저 노드의 IP 주소와 join 명령 사용시 스웜에 노드로 추가 가능 
* 운영 스웜에는 세 개의 매니저 노드를 구성하여 고가용성을 향상. 

## 12.3 도커 스웜 서비스로 애플리케이션 실행하기

도커 스웜환경에서는 컨테이너를 직접 실행할 필요 없다 스웜이 대신 컨테이너를 실행해준다.

서비스는 컨테이너와 같은 정보로 정의되며 (이미지, 환경변수) 여러 ㄹ레플리카를 가질 수 있다.

아래 명령어는, 컨테이너 하나를 실행하는 서비스이다

```
docker service create --name timecheck --replicas 1 diamol/ch12-timecheck:1.0

docker service ls 

# 이 서비스의 레플리카 목록을 확인한다
docker service ps timecheck

# 현재 컴퓨터에 실행 중인 컨테이너를 확인한다
docker container ls

# 가장 최근에 생성한 컨테이너(레플리카)를 삭제한다
docker container rm -f $( docker container ls --last 1 -q)

# 레플리카 목록을 다시 확인한다
docker service ps timecheck
```

레플리카 삭제시, 스웜은 서비스 레플리카가 부족하다고 판단하고 새 레플리카를 시작한다. 

> 그냥 간단히 생각하면 기존 도커 명령어에서 service라는 추가 명령어로 스웜 컨테이너들을 관리할 수 있다.

서비스 업데이트

```
docker service update --image diamol/ch12-timecheck:2.0 timecheck

 ✘ ysk 🌈   ~  docker service ps timecheck
ID             NAME              IMAGE                       NODE             DESIRED STATE   CURRENT STATE                 ERROR                         PORTS
o177bdbf08bf   timecheck.1       diamol/ch12-timecheck:2.0   docker-desktop   Running         Running about a minute ago
tbh5pb0n3p1z    \_ timecheck.1   diamol/ch12-timecheck:1.0   docker-desktop   Shutdown        Shutdown about a minute ago
yin258egdqrf    \_ timecheck.1   diamol/ch12-timecheck:1.0   docker-desktop   Shutdown        Failed 3 minutes ago          "task: non-zero exit (137)"
```

* 목록을 보면 기존 서비스는 셧다운, 새로운 버전의 서비스가 실행되어 있따

모든 컨테이너 오케스트레이션 도구는 애플리케이션 업데이트시 점진적으로 컨테이너를 교체해 나가는 롤링 업데이트 방식을 디폴트로 사용한다.  롤링업데이트시 구버전 신버전 둘다 존재하므로 사용자 경험은 직접 관리해야 한다.



배포 작업중 이전 상태로 롤백할 수 있다. 도커 스웜은 이전 상태를 저장하고 있다.

```
# 이전 버전으로 롤백
docker service update --rollback timecheck

# 레플리카의 목록 확인
docker service ps timecheck

# 최근 25초 동안 모든 레플리카의 로그 출력
docker service logs --since 25s timecheck
```

롤백도 배포와 마찬가지로 롤링 업데이트하면서 롤백한다 

## 12.4 클러스터 환경에서 네트워크 트래픽 관리하기

도커 스웜 네트워크는 tcp/ip 방식으로 통신하며 컴포넌트는 도메인 네임으로 서로를 식별한다.

도커 DNS 서버가 도메인 네임을 조회해 IP주소로 트래픽을 전달한다. 

스웜 모드 내부에서 클러스터에 속한 모든 노드를 연결하는 가상 네트워크인 오버레이 네트워크가 이루어져 있어, 오버레이 네트워크에 연결된 서비스는 서비스 이름을 도메인 네임 삼아 다른 서비스와 통신할 수 있다. 

![image-20241128184239267](./images//image-20241128184239267.png)

오버레이 네트워크는 클러스터 단위로 생성되므로 다른 클러스터끼리는 다른 방식을 통해 통신해야 한다.

배정된 가상 IP 주소를 확인하려면 레플리카 컨테이너에서 터미널로 접속하여 DNS 조회해 IP 주소 확인하면 된다.

```
nslookup iotd
nslookup accesslog
```

서비스당 레플리카가 여러개여도, 서비스당 할당된 IP 주소는 하나이다. 즉 숨겨두는 것이다.

도커 스웜 모드에서 트래픽을 처리하는 방식은 클러스터의 복잡한 구조를 사용자에게 숨긴다.

예를 들어, 클러스터에 20개의 노드(서버)가 있고, 그중 10개 노드에서 웹 애플리케이션 컨테이너가 실행되고 있을때,

**문제**:

- 트래픽이 들어오는 요청은 컨테이너가 실행되고 있는 노드로만 전달되어야 함.
- 그런데 모든 노드가 컨테이너를 실행 중인 것은 아니므로, 트래픽이 적절한 노드로 분산되어야 함.

**해결**:

- 스웜은 **인그레스 네트워킹(Ingress Networking)**을 사용해 이러한 문제를 해결한다.
- 클러스터의 모든 노드가 외부에서 들어오는 트래픽을 받아들이고, 이를 실행 중인 컨테이너가 있는 적절한 노드로 전달한다.

> 인그레스 네트워킹(Ingress Networking)**은 도커 스웜 모드에서 **서비스에 대한 외부 및 내부 트래픽 라우팅을 관리하는 네트워킹 구성이다. 스웜 클러스터 내의 서비스들이 외부 네트워크 또는 클러스터 내부에서 원활하게 통신할 수 있도록 한다. 

![image-20241130205022162](./images//image-20241130205022162.png)

이로 인해, 클러스터나 레플리케이션 내부의 트래픽 복잡성을 신경쓰지 않아도 된다.



## 12.5 도커 스웜과 쿠버네티스 중 무엇을 사용할까?

도커 스웜을 매니지드 서비스 형태로 지원하는 클라우드는 없어서, 매니지드 형태로 제공하는 쿠버네티스를 주로 사용한다. 확장성이 쿠버네티스보다 부족하다고 한다. 

* 도커 스웜은 약 1000개 노드와 30000개 컨테이너, 쿠버네티스는 이론적으로 5000개 이상 노드와 150000개 이상의 pod 지원 

그러나 도커 스웜에 사용하는 설정파일인 yaml 파일은 도커 컴포즈 문법을 확장하였기 때문에 직관적이지만 쿠버네티스 yaml 파일은 쿠버네티스 전용이여서 훨씬 어렵고 복잡함이 있다. 

우선 도커 스웜을 먼저 도 입한 후 도커 스웜에 없는 기능이 필요해졌을 때 쿠버네티스로 이전하는 방안을 추천한다. 나중에 쿠버네티스로 이전하더라도 애플리케이션을 도커로 전환하는 초기 비용이 낭비되는 일은 없다.

쿠버네티스로 이전하는 결정이 무 자르듯 쉽게 내릴 수 있는 결정은 아니지만, 몇 가지 참고 기준 이 될 만한 사항을 언급해 둔다.

- ﻿﻿인프라스트럭처: 애플리케이션을 클라우드 환경에 배포하고 있다면, 쿠버네티스가 더 적합 하다. 하지만 온프레미스 환경이라면 관리 면에서 스원이 훨씬 간편하다. 그리고 현재 조직 의 기술 기반이 완전히 윈도 기반이라면, 스웜을 선택해야 리눅스를 도입하지 않고도 이전할 수 있다.
- ﻿﻿학습 곡선: 스웜의 사용자 경험은 도커와 도커 컴포즈의 연장선상에 있어 학습 부하 면에서 는 스웜으로 이전하는 것이 유리하다. 쿠버네티스는 전혀 새로운 도구를 학습해야 하는 부 담이 있으므로 개발 팀 모두가 받아들이기 어려울 수 있다.
- ﻿﻿기능: 쿠버네티스의 사용법이 복잡한 이유는 그만큼 세세하게 설정할 수 있는 기능이 많기 때문이기도 하다. 예를 들어 블루-그린 배포(blue-green deploymen)나 자동 스케일링, 역할 기반 접근 제어 같은 기능은 쿠버네티스에는 쉽게 적용할 수 있는 반면, 스웜에는 적용하기가 까다롭다.
- ﻿﻿미래를 위한 투자: 쿠버네티스의 오픈 소스 커뮤니티는 매우 활동적이고 규모도 업계 최대 다. 스웜은 신규 기능이 추가되지 않은 지 좀 됐고, 쿠버네티스는 추가 기능이 계속 업데이트 중이다.



## 12.6 연습 문제

```
docker network create --driver overlay numbers

docker service create --detach --network numbers --name numbers-api diamol/ch08-numbers-api:v3

docker service create --detach --network numbers --name numbers-web --publish 8020:80  diamol/ch08-numbers-web:v3
```



# 13장 도커 스웜 스택으로 분산 애플리케이션 배포하기

실무에선 명령행 도구를 사용하지 않고, yaml 파일로 정의해 매니저 노드에 이 파일을 전달하는 방법을 쓴다

yaml 파일에 애플리케이션의 원하는 상태를 기술하고 오케스트레이션 도구가 애플리케이션의 현재 상태를 파악해 원하는 상태로 만들기 위한 조치를 자동으로 취한다.

## 13.1 도커 컴포즈를 사용한 운영 환경

도커 컴포즈는 도커 스웜 환경에서 동일한 파일 포맷을 사용할 수 있다. 스웜에서 가장 간단한 형태의 배포는 컴포즈 파일 그 자체다. 

다음 예시는 docker-compose에서 deploy 프로퍼티를 추가해, 레플리카의 개수를 여러되로 늘리되 레플리카의 사용량을 제한한 예시이다

```yaml
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list
    ports:
      - 8080:80
    deploy:
      replicas: 2
      resources:
        limits:
          cpus: "0.50" # 코어 한개의 50% 
          memory: 100M # 메모리 경우 최대 100 MB 까지 점유 

```

기존 스택의 정의를 수정한 컴포즈 파일을 배포하려면

```
docker statck deploy -c ./todo-list/v2.yml todo 
```





## 13.2 컨피그 객체를 이용한 설정값 관리

클러스터에 도커 config 객체를 이용해 애플리케이션에 설정 값을 제공할 수 있다.

![image-20241208175823645](./images//image-20241208175823645.png)

객체의 이름과 설정값이 담긴 파일 경로를 지정하면 config 객체를 만들 수 있따.

* JSON, XML, key=value 쌍, 바이너리 파일 형식

```
docker config create todo-list-config ./todo-list/configs/config.json

docker config ls

{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft": "Warning",
      "Microsoft.Hosting.Lifetime": "Warning"
    }
  },
  "AllowedHosts": "*",
  "Database": {
    "Provider": "Postgres"
  }
}
```

클러스터에 이 로컬 설정 파일을 전달할 수 있다. 

서비스는 컴포즈 파일에 지정된 컨피그 객체를 사용할 수 있따.

```
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list
    ports:
      - 8080:80
    configs:
      - source: todo-list-config
        target: /app/config/config.json
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "0.50"
          memory: 100M
    networks:
      - app-net

  todo-db:
    image: diamol/postgres:11.5
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "0.50"
          memory: 500M
    networks:
      - app-net

configs:
  todo-list-config:
    external: true

networks:
  app-net:
```



그러나 config 객체 자체는 민감한 데이터를 보관하기 위한 수단이 아니며 파일 내용은 암호화 되지 않는다. 

## 13.3 비밀값을 이용한 대외비 설정 정보 관리하기

비밀값은 클러스터의 관리를 받는 스웜 리소스다. 로컬로부터 파일 생성 후 클러스터 DB에 저장했다가 서비스 정의시 비밀값을 참조하면 비밀값이 전달된다. 이 비밀값은 항상 암호화된 상태로 존재하지만 해당 비밀값을 사용하는 컨테이너를 실행한 노드에서는 볼 수 있다.

```
docker secret create todo-list-secret ./todo-listsecrets/secrets.json

docker secret inspect --pretty todo-list-secret
```

이렇게 설정된 비밀값을 컴포즈에 정의하여 참조할 수 있다.

```yaml
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list
    ports:
      - 8080:80
    configs:
      - source: todo-list-config
        target: /app/config/config.json
    secrets:
      - source: todo-list-secret
        target: /app/config/secrets.json
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "0.50"
          memory: 100M
    networks:
      - app-net

  todo-db:
    image: diamol/postgres:11.5
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "0.50"
          memory: 500M
    networks:
      - app-net

configs:
  todo-list-config:
    external: true

secrets:
  todo-list-secret:
    external: true

networks:
  app-net:
```

클러스터에서 컨피그 객체와 비밀값을 만들고 나면 이들의 내용은 변하지 않는다.

수정하려면 새로운 컨피그 객체나 비밀값을 만들어야 한다.

* 변경된 내용을 담은 새로운 컨피그 객체 혹은 비밀값을 기존 것과 다른 이름으로 만든다
* 컴포즈 파일의 정의에 사용된 컨피그 객체 혹은 비밀값의 이름을 새로 만든 이름으로 바꾼다
* 변경된 컴포즈 파일을 스택으로 배포한다. 

즉 위의 단계를 따라 서비스를 업데이트 하여 컨테이너를 새 컨테이너로 교체해야 한다. 

## 13.4 스웜에서 볼륨 사용하기

볼륨은 컨테이너와 별개의 생애주기를 갖는 스토리지의 단위이다. 

컨테이너 파일 시스템 일부처럼 사용하지만, 컨테이너 외부에 존재하는 리소스다. 

오케스트레이션 환경도 볼륨의 개념은 같다. 컴포즈 파일의 서비스 정의에 볼륨 마운트를 정의하면 레플리카에서 볼륨을 로컬 파일 시스템 디렉터리처럼 사용할 수 있다. 

컨테이너를 새로 시작하면 기존 컨테이너의 볼륨에 연결되지만, 새로운 레플리카가 이전 레플리카와 다른 노드에서 실행되면 기존 볼륨을 사용할 수 없다. 이 문제는 서비스가 특정 노드에서만 실행되게 노드에 레이블을 부여하고 컴포즈 파일에서 해당 노드에서만 실행하도록 강제하면 된다

```
# 노드의 식별자를 찾아 해당 노드에 레이블을 부여한다
docker node update --label-add storage=raid $(docker node ls -q)
```

```yaml
version: "3.7"

services:
  todo-web:
    image: diamol/ch06-todo-list
    ports:
      - 8080:80
    configs:
      - source: todo-list-config
        target: /app/config/config.json
    secrets:
      - source: todo-list-secret
        target: /app/config/secrets.json
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "0.50"
          memory: 100M
    networks:
      - app-net

  todo-db:
    image: diamol/postgres:11.5
    environment:
      PGDATA: "/var/lib/postgresql/data/pgdata"
    volumes:
      - todo-db-data:/var/lib/postgresql/data
    deploy:
      replicas: 1
      resources:
        limits:
          cpus: "0.50"
          memory: 500M
      placement: # 노드 라벨 
        constraints:
          - node.labels.storage == raid
    networks:
      - app-net

configs:
  todo-list-config:
    external: true

secrets:
  todo-list-secret:
    external: true

networks:
  app-net:

volumes:
  todo-db-data:

```



## 13.5 클러스터는 스택을 어떻게 관리하는가?

도커스웜에서 스택은 클러스터가 관리를 담당하는 리소스의 모임이다. 

![image-20241208182906657](./images//image-20241208182906657.png)

- ﻿﻿스원도 볼륨을 생성하고 삭제할 수 있다. 서비스 이미지에 볼륨의 정의가 포함된 경우 스택 도 기본 볼륨을 생성하지만, 기본 볼륨은 스택을 제거하면 함께 제거된다. 스택 정의에 이름 이 붙은 볼륨을 사용하면 스택 배포와 함께 볼륨이 생성되지만, 이 볼륨은 스택을 제거해도 삭제되지 않는다.
- ﻿﻿비밀값과 컨피그 객체는 설정값이 든 파일을 클러스터에 업로드하는 방법으로 생성한다. 비 밀값과 컨피그 객체는 클러스터 데이터베이스에 저장됐다가 이 비밀값이나 컨피그 객체를 사용하는 컨테이너가 실행될 때 해당 컨테이너로 전달된다. 비밀값과 컨피그 객체는 전형적 인 읽기 위주 객체로, 수정이 불가능하다. 스 환경에서 애플리케이션 설정 관리는 배포 프 로세스와 별개다.
- ﻿﻿네트워크는 애플리케이션과 별도로 관리된다. 관리자가 명시적으로 네트워크를 생성할 수 도 있고 필요할 때마다 스원이 네트워크를 생성하기도 한다. 모든 스택은 컴포즈 파일에 네 트워크가 정의되지 않았더라도 자신이 포함한 서비스를 연결할 네트워크와 함께 배포된다.
- ﻿﻿서비스는 스택이 배포될 때 생성되거나 제거된다. 서비스가 실행 중일 때는 스웜이 서비스 를 모니터링하며 서비스 수준이 정상치를 유지하는지 확인한다. 헬스 체크를 통해 이상이 검출된 컨테이너는 새로운 컨테이너로 교체되며, 고장을 일으킨 노드에서 실행 중이던 컨테 이너도 마찬가지로 교체된다.



## 13.6 연습 문제

9장에서 살펴본 오늘의 천문 사진 애플리케이션을 운영 환경에 배포할 수 있는 컴포즈 파 일을 작성하라. 단일 파일로 된 컴포즈 파일로 다음 조건을 만족하면 된다.

- ﻿﻿로그 API는 diamol/chog-access-log 이미지를 사용한다. 로그 API는 웹 애플리케이션에서 만 접근하는 내부용 컴포넌트이며, 세 개의 레플리카로 실행된다.
- ﻿﻿NASA API는 diamol/chog-image-of-the-day 이미지를 사용한다. 8088번 포트를 통해 외 부 접근이 가능해야 하며, 사용량 증가에 대비해 다섯 개의 레플리카로 실행된다.
- ﻿﻿웹 애플리케이션은 diamol/chog-image-gallery 이미지를 사용한다. HTTP 표준 포트인 80 번 포트를 통해 외부 접근이 가능해야 하며, 두 개의 레플리카로 실행된다.
- ﻿﻿모든 컴포넌트는 적정한 수준으로 CPU 및 메모리 사용량이 제한돼야 한다(안전한 수치를 정하기 위해 시행착오를 통해 여러 번의 배포를 거쳐야 할 수도 있다).
- ﻿﻿스택이 완전히 배포된 후 애플리케이션이 정상적으로 동작해야 한다.

```
version: "3.7"

services:
  accesslog:
    image: diamol/ch09-access-log
    deploy:
      replicas: 3 # 3개 레플리카 
      resources:
        limits: # cpu 메모리 제한 
          cpus: "0.50"
          memory: 100M
    networks:
      - app-net

  iotd:
    image: diamol/ch09-image-of-the-day
    ports:
      - 8088:80
    deploy:
      replicas: 5 # 5개 레플리카 
      resources:
        limits:
          cpus: "1"
          memory: 200M
    networks:
      - app-net

  image-gallery:
    image: diamol/ch09-image-gallery
    ports:
      - 80:80
    deploy:
      replicas: 2
      resources: # 2개 레플리카 
        limits:
          cpus: "0.75"
          memory: 75M
    networks:
      - app-net

networks:
  app-net:
    name: image-gallery-prod
```





# 14장 업그레이드와 롤백을 이용한 업데이트 자동화

컨테이너 애플리케이션을 오케스트레이션 도구와 조합하면 무중단 업데이트가 가능하다. 

## 14.1 도커를 사용한 애플리케이션 업그레이드 프로세스

배포 주기는 다음 네가지 주기를 고려해야 한다

1. 의존 모듈 업데이트
2. 애플리케이션 코드 컴파일 sdk 업데이트
3. 애플리케이션 플랫폼 업데이트
4. 운영체제 업데이트

라이브러리 업데이트, sdk 업데이트까지 반영하려면 정해진 주기 없이 주기적으로 애플리케이션을 업데이트 해야한다.

빌드 파이프라인이 프로젝트의 핵심을 차지하는것도 이때문이다. 

롤링 업데이트에서  헬스 체크가 없다면 업데이트 성공 여부를 알 수 없기 때문에 헬스체크가 필요하다

```
# docker-compose.yml
version: "3.7"

services:
  numbers-api:
    image: diamol/ch08-numbers-api
    networks:
      - app-net

  numbers-web:
    image: diamol/ch08-numbers-web
    environment:
      - RngApi__Url=http://numbers-api/rng
    networks:
      - app-net


# prod.yml
version: "3.7"

services:
  numbers-api:
    deploy:
      replicas: 6
      resources:
        limits:
          cpus: "0.50"
          memory: 75M

  numbers-web:
    ports:
      - target: 80
        published: 80
        mode: host
    deploy:
      mode: global
      resources:
        limits:
          cpus: "0.75"
          memory: 150M

networks:
  app-net:
    name: numbers-prod


# prod-healthcheck
version: "3.7"

services:
  numbers-api:
    healthcheck:
      test: ["CMD", "dotnet", "Utilities.HttpCheck.dll", "-u", "http://localhost/health", "-t", "500"]
      interval: 2s
      timeout: 3s
      retries: 2
      start_period: 5s

  numbers-web:
    healthcheck:
      interval: 20s
      timeout: 10s
      retries: 3
      start_period: 30s

# v2.yml
version: "3.7"

services:
  numbers-api:
    image: diamol/ch08-numbers-api:v2

  numbers-web:
    image: diamol/ch08-numbers-web:v2

```

이 세 파일을 병합하여 실행한다

```
docker-compose -f docker-compose.yml -f prod.yml -f prod-healthcheck.yml -f v2.yml --log-level ERROR config > stack.yml

docker stack deploy -c stack.yml numbers

docker stack ps numbers
```



## 14.2 운영 환경을 위한 롤링 업데이트 설정하기

롤링 업데이트의 세세한 방식은 컴포즈 파일 내 서비스 정의의 deploy 항목에서 설정할 수 있다.

```yaml
version: "3.7" # Docker Compose 파일의 버전 (3.7 버전을 사용)

services:
  numbers-api: # 서비스 이름 (numbers-api)
    deploy: # 서비스 배포 설정
      update_config: # 업데이트 설정 (서비스를 업데이트할 때의 동작 정의)
        parallelism: 3 # 동시에 업데이트할 컨테이너의 개수 (최대 3개)
        monitor: 60s # 업데이트 후 60초 동안 모니터링하여 상태 확인
        failure_action: rollback # 업데이트 실패 시 롤백 수행
        order: start-first # 새 컨테이너를 먼저 시작하고 기존 컨테이너를 중단 (무중단 배포)

```



## 14.3 서비스 롤백 설정하기

롤링업데이트 옵션(deploy)이 start-first 방식으로 설정시 배포 실패할 경우 자동 롤백된다.

업데이트 실패시 최대한 빨리 이전 버전으로 돌아가는 설정은 다음과 같다

```yaml
version: "3.7" # Docker Compose 파일의 버전 (3.7 버전을 사용)

services:
  numbers-api: # 서비스 이름 (numbers-api)
    deploy: # 서비스 배포 설정
      rollback_config: # 롤백 설정 (배포 실패 시의 동작 정의)
        parallelism: 6 # 동시에 롤백할 컨테이너의 개수 (최대 6개)
        monitor: 0s # 롤백 후 모니터링 없이 바로 완료로 간주
        failure_action: continue # 롤백 중 실패해도 나머지 작업 계속 진행
        order: start-first # 새 컨테이너를 먼저 시작하고 기존 컨테이너를 중단 (무중단 롤백)

```



# 15장 보안 원격 접근 및 CI/CD를 위한 도커 설정

도커 명령은 api로 지시를 전달하는거 뿐이지 직접 실행하는것이 아니다.

도커 api를 안전하게 공개하고 이를 엔진에 접근하는 방법을 배운다. 

## 15.1 도커 API의 엔드포인트 형태

도커 엔진 설정을 통해 원격 접속할 수 있다. 윈도 10이나 맥에서 도커 데스크톱 -> settings -> expose daemon on tcp://localhost:2375 without tls 체크 

* 이거 안되서 아래로 해야함

```
ettings.json
```

해당 파일을 열어 다음 내용을 추가하거나 수정

```
{
  "builder": {
    "gc": {
      "defaultKeepStorage": "20GB",
      "enabled": true
    }
  },
  "hosts": [
    "tcp://0.0.0.0:2375",
    "fd://"
  ],
  "insecure-registries": [
    "registry.local:5000"
  ]
}
```

```
/Users/ysk/.docker/daemon.json 을 수정해도 안되서 아래 명령어로 사용함 
```

```
curl --unix-socket /Users/ysk/.docker/run/docker.sock http://localhost:2375/containers/json
```

15장 패스. 현재 실습할 수 있는 것이랑 버전이 너무다름 

# 16장 어디서든 실행할 수 있는 도커 이미지 만들기: 리눅스, 윈도, 인텔, ARM

## 16.1 다중 아키텍처 이미지가 중요한 이유

arm 이미지는  amd와 인텔의 프로세서가 실행되는 환경에서 실행되지 않는다. 

반대도 마찬가지다. 

```
a. Buildx 활성화

docker buildx create --use

b. 멀티플랫폼 이미지 빌드
docker buildx build --platform linux/amd64,linux/arm64 -t my-app:latest --push .

# 네이티브 아키텍처(인텔/AMD)로 이미지를 빌드한다
docker image build -t diamol/ch16-folder-list:linux-amd64 -f ./Dockerfile.linux-amd64 .

# 64비트 ARM 아키텍처로 이미지를 빌드한다
docker image build -t diamol/ch16-folder-list:linux-arm64 -f ./Dockerfile.linux-arm64 --platform linux/arm64 .

# 32비트 ARM 아키텍처로 이미지를 빌드한다
docker image build -t diamol/ch16-folder-list:linux-arm -f ./Dockerfile.linux-arm --platform linux/arm .
```

## 16.4 도커 Buildx를 사용해 다중 아키텍처 이미지 빌드하기

`docker buildx`는 Docker의 확장 빌드 도구로, 여러 플랫폼을 지원하는 멀티 아키텍처 이미지를 생성할 수 있는 기능을 제공한다. 로컬 머신에서 실행되는 빌드뿐 아니라 원격 빌드도 가능하며, 복잡한 Dockerfile을 효율적으로 처리할 수 있다.

```
docker buildx version
```

`docker buildx`는 Docker의 확장 빌드 도구로, 여러 플랫폼을 지원하는 멀티 아키텍처 이미지를 생성할 수 있는 기능을 제공합니다. 이를 통해 로컬 머신에서 실행되는 빌드뿐 아니라 원격 빌드도 가능하며, 복잡한 Dockerfile을 효율적으로 처리할 수 있습니다.

### 주요 기능

1. 멀티 아키텍처 빌드 지원

   :

   - 예: `linux/amd64`, `linux/arm64`, `linux/arm` 등 다양한 플랫폼용 이미지를 빌드 가능.

2. 캐시 공유

   :

   - 빌드 캐시를 공유하여 속도 향상.

3. 외부 빌드 노드 지원

   :

   - 여러 머신에서 병렬 빌드 가능.

4. 향상된 빌드 옵션

   :

   - 추가 기능과 설정을 지원하는 플래그 제공.

------

### 설치 및 활성화

`buildx`는 Docker 19.03 이상 버전에 기본 포함되어 있으며, 다음 명령어로 활성화할 수 있습니다:

```bash
docker buildx install
```

활성화된 `buildx`를 확인하려면:

```bash
docker buildx version
```

### 사용 방법

#### 1. **빌더 인스턴스 생성**

`buildx` 빌더 인스턴스를 생성:

```bash
docker buildx create --name mybuilder --use
```

- `--name`: 빌더의 이름 지정.
- `--use`: 생성한 빌더를 활성화.

#### 2. **빌더 활성화 확인**

```bash
docker buildx inspect --bootstrap
```

- 활성화된 빌더를 확인하고 필요한 플랫폼 지원 여부를 확인.

#### 3. **멀티 아키텍처 빌드**

다양한 아키텍처를 지원하는 이미지를 빌드하려면:

```bash
docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 -t username/image:tag .
```

- `--platform`: 지원할 플랫폼 목록 지정.
- `-t`: 이미지 이름과 태그 지정.
- `.`: Dockerfile 경로.

#### 4. **이미지 푸시**

빌드와 동시에 이미지를 푸시하려면 `--push` 옵션을 추가:

```bash
docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 -t username/image:tag --push .
```

#### 5. **캐시 사용**

빌드 시간을 단축하기 위해 캐시를 사용하려면:

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t username/image:tag --cache-to=type=inline --cache-from=type=registry,ref=username/image:tag .
```

------

### 예제

#### 1. 기본 멀티 아키텍처 이미지 빌드

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t myrepo/myapp:latest --push .
```

#### 2. 로컬 빌드

테스트 목적으로 이미지를 로컬 빌드하려면:

```bash
docker buildx build --platform linux/amd64 -t myrepo/myapp:latest --load .
```

- `--load`: 이미지를 로컬 Docker 데몬에 로드.

------

### 장점

- 하나의 명령어로 다양한 플랫폼에 대해 이미지를 빌드 가능.
- 빌드 캐시 공유 및 재사용으로 속도 향상.
- 멀티 노드를 활용한 병렬 빌드 가능.

# 17장 도커 이미지 최적화하기: 보안, 용량, 속도

## 17.1 도커 이미지를 최적화하는 방법

docker system df 명령을 사용하면 내려받은 이미지, 컨테이너, 볼드, 빌드 캐시 등이 점유하는 실제 디스크 용량을 알 수있다.

```
docker system df

# 실제로 빌드캐시 18gb 이미지 18gb 정리함..
 ✘ ysk 🐸   ~/.docker  docker system df
TYPE            TOTAL     ACTIVE    SIZE      RECLAIMABLE
Images          67        7         17.7GB    14.71GB (83%)
Containers      7         0         69.71MB   69.71MB (100%)
Local Volumes   17        3         1.972GB   780.9MB (39%)
Build Cache     397       0         18.26GB   18.26GB
```

도커 이미지는 이미지 레이어가 쌓여서 만들어지는거라서, 각 레이어마다 최적화 하지 않으면 전체 용량은 줄어들지 않는다.

또한 이미지 빌드시 주의해야할점이있다.

도커의 빌드과정은 엔진에 빌드 컨텍스트(빌드를 실행한 디렉터리)를 압축하고 Docerfile 스크립트를 함께 보내면서 시작된다. 이 덕분에 원격 엔진에 이미지 빌드 명령을 내릴 수 있는것이다.

빌드 컨텍스트에는 불필요한 파일이 포함된 경우가 많으므로 .dockerignore 파일에 불필요한 디렉터리나 파일 목록을 기재하여 컨텍스트에서 이들 파일을 제외하고 빌드하는것이 좋다.

```
# IDE 설정 파일 제외
.classpath
.project
.settings/
.idea/

# 빌드된 파일 제외
target/
*.class

# 로그 파일 제외
*.log

# 환경 파일 제외
.env
```



## 17.2 좋은 기반 이미지를 고르는 법

기반 이미지 크기의 차이가 매우 다양할 수 있으므로 현명하게 골라야 한다.

이미지가 작을수록 빌드 속도와 네트워크 전송 속도가 빨라진다. 

리눅스 컨테이너는 알파인 리눅스, 데비안 슬림 이미지를 베이스 이미지로 좋다.

* Python 프로젝트: `python:<버전>-slim`, `python:<버전>-alpine`.

* Node.js 프로젝트: `node:<버전>-slim`, `node:<버전>-alpine`.

* Java 프로젝트: `openjdk:<버전>-slim`, `eclipse-temurin:<버전>-jre`.

## 17.3 이미지 레이어 수와 이미지 크기는 최소한으로

도커 파일에서 명령 한줄한줄 인스트럭션은 레이어가 되어서 한번에 실행시키는것이 좋다.

그리고 실행한 후 로그나 필요업는 데이터는 지우는것이 좋다. 패키지 목록의 캐시까지 지우는것. 

```dockerfile
# 최적화 전
FROM debian:stretch-slim

RUN apt-get update
RUN apt-get install -y curl=7.52.1-5+deb9u16
RUN apt-get install -y socat=1.7.3.1-2+deb9u1

# 최적화 후
FROM debian:stretch-slim

RUN apt-get update \
 && apt-get install -y --no-install-recommends \
    curl=7.52.1-5+deb9u16 \
    socat=1.7.3.1-2+deb9u1 \
 && rm -rf /var/lib/apt/lists/*
```

다음 예는, 인터넷에서 압축된 패키지를 내려받고 불필요한 파일을 제거한다

```dockerfile
FROM diamol/base

ARG DATASET_URL=https://archive.ics.uci.edu/ml/machine-learning-databases/url/url_svmlight.tar.gz

WORKDIR /dataset

RUN wget -O dataset.tar.gz ${DATASET_URL} && \
    tar xvzf dataset.tar.gz

WORKDIR /dataset/url_svmlight
RUN cp Day1.svm Day1.bak && \
    rm -f *.svm && \
    mv Day1.bak Day1.svm
```





## 17.4 멀티 스테이지 빌드를 한 단계 업그레이드하기

멀티 스테이지 빌드 : 빌드단계, 패키징, 실행단계를 나누어 사용하는것.

멀티스테이지 빌드 캐시를 잘 활용하면 소스 코드 수정시마다 빠르게 진행되어 빌드 시간을 엄청나게 줄일 수 있다. 

## 17.5 최적화가 중요한 이유

베스트 프랙티스

* 기반 이미지 잘 고르기. 자신만의 골든 이미지를 갖출 수 있다면 이상적이다.

* 아주 간단한 애플리케이션이 아니라면 멀티 스테이지 빌드를 적용한다.
* 불필요한 패키지나 파일을 포함시키지 말고, 레이어 크기를 최소한으로 유지한다.
* Dockerfile 스크립트의 인스트럭션은 자주 수정하는 순서대로 뒤에 오도록 배치해 캐시를 최대한 활용한다.

![image-20241208230617909](./images//image-20241208230617909.png)

## 17.6 연습 문제

- ﻿﻿이미지 파일 시스템을 최적화해서 리눅스 컨테이너는 80MB 이하, 윈도 컨테이너는 330MB 이하가 되도록 하라.
- ﻿﻿이미지 레이어 캐시를 적극 활용해 이미지 빌드 시간을 1초 이내로 단축한다.
- ﻿﻿docker run 〈image〉 docker version 명령을 실행해 도커 명령행의 버전을 출력하는 이미 지를 빌드하라(도커 엔진에 접속하지 못하므로 오류를 일으키겠지만, 그래도 버전은 제대로 출력된다).

```
# Base image: lightweight Alpine Linux
FROM alpine:3.18

# Install Docker CLI
RUN apk add --no-cache docker

# Command to print Docker CLI version
CMD ["docker", "version"]
```



# 18장 컨테이너의 애플리케이션 설정 관리

## 18.1 다단 애플리케이션 설정

설정 모델은 설정에 담긴 데이터의 구조를 반영해야 한다. 설정 데이터의 종류는 주로 다음 세 가 지다.

- ﻿﻿버전에 따라 달라지는 설정: 모든 환경에서 동일하지만 버전별로 달라지는 설정
- ﻿﻿환경에 따라 달라지는 설정: 환경별로 달라지는 설정
- ﻿﻿기능 설정: 버전별로 애플리케이션의 동작을 달리하기 위한 설정





## 18.2 환경별 설정 패키징하기

닷넷 코어는 다음과 같은 파일로부터 기본 설정값을 읽어 들인다

* appsettings.json, appsettiongs.{환경이름}.json
* spring에서는 yml, properties가 있따

설정파일과 소스코드를 별도의 시스템으로 분리하여 관리한다면, 설정 파일을 소스코드로 가져와 이미지를 빌드해서 테스트 과정을 ci/cd 등에 넣거나 다양한 프로파일로 운영할 수 있게 된다.



## 18.3 런타임에서 설정 읽어 들이기

예제는 고랭이지만, 스프링은 위와같다.

비밀값 등을 사용한 민감 정보를 API를 통해 아무나 보게 할 수는 없는 노릇이므로 설정 API를 만들 때는 다음과 같은 사항을 지켜야 한다.

- ﻿﻿전체 설정을 공개하지 않는다. 민감하지 않은 정보만 선택하되 민감한 정보는 절대 포함시 키지 않는다.
- ﻿﻿허가받은 사용자만이 접근할 수 있도록 엔드포인트에 보안을 설정한다.
- ﻿﻿설정 API의 사용 여부를 설정할 수 있도록 한다.

## 18.4 레거시 애플리케이션에 설정 전략 적용하기

## 18.5 유연한 설정 모델의 이점

## 18.6 연습 문제

# 19장 도커를 이용한 로그 생성 및 관리

도커는 표준 출력 스트림을 통해 로그를 수집한다.

플러그인 로깅 프레임워크를 가지고 있어 도커가 출력된 로그를 원하는 곳으로 전달하여 로그를 중앙 로그 저장소에 저장하고 수집된 로그를 검색할 수도 있다. 

## 19.1 표준 에러 스트림과 표준 출력 스트림

표준 출력 : stdout, 표준 오류 stderror

* 표준 출력 : 프로그램이 데이터 출력하는데 사용하는 스트림. 
* 표준 에러 : 오류나 경고 메시지를 출력하는 스트림

표준 출력과 표준 오류는 별도의 스트림으로 처리되어서 로그를 분리하거나 오류를 별도로 관리해야 한다.

자바 스프링 계열에서는 system.out.print, system.error.print, log.info, log.error 등이 있다.



또한 종료된 컨테이너의 로그를 수집할 수 있도록 로그를 JSON 파일로도 저장하며 이 파일은 도커 컨테이너와 동일한 생애주기를 가져서 컨테이너가 삭제되면 로그도 삭제된다.

**도커 로그와 로그 파일 경로를 확인하는 다양한 명령어들**

**로그 드라이버(Log Driver)**는 도커 컨테이너의 로그를 저장하거나 전송하는 방식(메커니즘)을 정의하는 컴포넌트

- **역할**:
  - 도커 컨테이너에서 발생하는 표준 출력(stdout) 및 표준 오류(stderr)의 로그를 수집
  - 로그를 특정 저장소(파일, 시스템 로그, 외부 서비스 등)에 저장하거나 외부 서비스로 전송

### 1. `docker logs` 명령어

컨테이너의 표준 출력(stdout)과 표준 오류(stderr)에 기록된 로그를 확인할 수 있습니다.

- **기본 로그 보기**

  ```bash
  docker logs <컨테이너_이름_또는_ID>
  ```

- **실시간 로그 보기 (Follow)**

  실시간으로 로그를 모니터링할 때 사용합니다.

  ```bash
  docker logs -f <컨테이너_이름_또는_ID>
  ```

- **특정 시간 이후의 로그 보기**

  예를 들어, 지난 10분간의 로그를 보고 싶을 때:

  ```bash
  docker logs --since 10m <컨테이너_이름_또는_ID>
  ```

- **최근 N줄의 로그 보기 (Tail)**

  최근 100줄의 로그를 보고 싶을 때:

  ```bash
  docker logs --tail 100 <컨테이너_이름_또는_ID>
  ```

- **타임스탬프와 함께 로그 보기**

  로그 메시지에 타임스탬프를 포함시킵니다.

  ```bash
  docker logs --timestamps <컨테이너_이름_또는_ID>
  ```

### 2. 컨테이너의 로그 드라이버 확인 및 설정

도커는 다양한 로그 드라이버를 지원하며, 각 드라이버에 따라 로그의 저장 위치와 방식이 다릅니다.

- **현재 컨테이너의 로그 드라이버 확인**

  ```bash
  docker inspect --format='{{.HostConfig.LogConfig.Type}}' <컨테이너_이름_또는_ID>
  ```

- **컨테이너 생성 시 로그 드라이버 지정**

  예를 들어, `json-file` 드라이버를 사용하여 컨테이너를 실행할 때:

  ```bash
  docker run --log-driver=json-file <이미지_이름>
  ```

- **도커 데몬의 기본 로그 드라이버 확인**

  ```bash
  docker info | grep 'Logging Driver'
  ```

### 3. 도커 데몬의 로그 파일 경로 확인

도커 데몬 자체의 로그 파일 위치는 운영체제에 따라 다릅니다.

- **Linux (systemd 사용 시)**

  ```bash
  journalctl -u docker.service
  ```

- **Linux (로그 파일 직접 확인 시)**

  일반적으로 `/var/log/docker.log` 또는 `/var/log/upstart/docker.log`에 위치할 수 있습니다.

  ```bash
  sudo tail -f /var/log/docker.log
  ```

- **macOS 및 Windows**

  Docker Desktop을 사용하는 경우, 로그 파일은 GUI를 통해 접근할 수 있습니다.

  - **macOS**: `~/Library/Containers/com.docker.docker/Data/log`
  - **Windows**: `%APPDATA%\Docker\log.txt`

### 4. 컨테이너 내부의 로그 파일 경로 확인

일부 애플리케이션은 컨테이너 내부에 별도의 로그 파일을 생성합니다. 이러한 경우 `docker inspect` 명령어를 사용하여 로그 파일의 경로를 확인할 수 있습니다.

- **컨테이너의 마운트된 볼륨 확인**

  ```bash
  docker inspect --format='{{json .Mounts}}' <컨테이너_이름_또는_ID>
  ```

- **애플리케이션 별 로그 파일 경로 파악**

  예를 들어, Nginx 컨테이너의 로그 파일 경로를 확인하려면:

  ```bash
  docker exec -it <nginx_컨테이너_이름_또는_ID> cat /var/log/nginx/access.log
  docker exec -it <nginx_컨테이너_이름_또는_ID> cat /var/log/nginx/error.log
  ```

### 6. 기타 유용한 명령어

- **모든 실행 중인 컨테이너의 로그 보기**

  ```bash
  docker ps -q | xargs docker logs
  ```

- **로그 포맷 지정**

  JSON 형태로 로그를 출력할 때:

  ```bash
  docker logs --format '{{json .}}' <컨테이너_이름_또는_ID>
  ```

- **로그를 파일로 저장**

  ```bash
  docker logs <컨테이너_이름_또는_ID> > container.log
  ```

### 7. 예제 시나리오

- **Nginx 컨테이너의 최근 50줄 로그를 실시간으로 모니터링**

  ```bash
  docker logs -f --tail 50 <nginx_컨테이너_이름_또는_ID>
  ```

- **애플리케이션 컨테이너의 로그 드라이버를 `syslog`로 변경하고 재시작**

  ```bash
  docker run --log-driver=syslog <이미지_이름>
  ```

- **도커 데몬의 로그를 systemd journal에서 확인**

  ```bash
  journalctl -u docker.service -f
  ```

### 로깅 옵션

`--log-opt` 옵션을 사용하여 로그 드라이버에 필요한 세부 설정을 추가합니다.

- **예: `json-file` 드라이버**

  ```
  docker run \
    --log-driver=json-file \
    --log-opt max-size=10m \
    --log-opt max-file=3 \
    <이미지_이름>
  ```

  - `max-size`: 로그 파일의 최대 크기 (예: 10MB).
  - `max-file`: 로그 파일의 최대 개수 (예: 3개).

- **예: `syslog` 드라이버**

  ```
  docker run \
    --log-driver=syslog \
    --log-opt syslog-address=udp://192.168.1.100:514 \
    --log-opt syslog-format=rfc5424micro \
    <이미지_이름>
  ```

```
version: '3.9'
services:
  app:
    image: my-app-image
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "5"
```

새 파일이 생성될 때 기존 로그 파일의 수가 `max-file`에 도달하면, **가장 오래된 파일이 삭제**된다.



## 19.2 다른 곳으로 출력된 로그를 stdout 스트림에 전달하기

fluentd는 통합 로깅 계층이다. 통합 로깅 계층은 다양한 곳에서 생성되는 로그를 모으고, 필터링 과 가공을 거쳐 다시 여러 대상으로 수집된 로그를 포워딩하는 역할을 한다. fluentd 프로젝트는 클라우드 네이티브 컴퓨팅 재단에서 관리한다. 

fluendtd를 컨테이너로 실행하고 다른 컨테이너에서 json 파일 대신 fluentd 로깅 드라이버를 사용하도록 하면 컨테이너에서 생성되는 로그가 fluentd 컨테이너로 전송된다. 

```
docker run -d -p 24224:24224 --name fluentd -v "$(pwd)/conf:/fluentd/etc" \
-e FLUENTD_CONF=stdout.conf diamol/fluentd

docker container run -d --log-driver=fluentd \
--log-opt fluentd-address=localhost:24224 \
--name timecheck5 image

docker container logs --tail 1 fluentd 
```

수집된 로그는 대게 중앙 저장소로 이동하여 엘라스틱 서치와 키바나와 함께 쓰인다 

## 19.3 컨테이너 로그 수집 및 포워딩하기

## 19.4 로그 출력 및 로그 컬렉션 관리하기

Fluentd의 작동 방식은 크게 **Input**, **Filter**, **Output**의 3단계로 구성됩니다:

1. **Input (입력)**:
   - 로그 데이터를 수집하는 단계.
   - 예: 애플리케이션 로그, 서버 로그, 데이터베이스 로그 등.
   - 대표적인 플러그인: `in_tail`, `in_http`, `in_syslog`.
2. **Filter (필터링 및 변환)**:
   - 수집된 데이터를 변환, 필터링 또는 집계하는 단계.
   - 필요에 따라 데이터를 재구성하거나 필드를 추가/삭제.
   - 대표적인 플러그인: `grep`, `record_transformer`.
3. **Output (출력)**:
   - 처리된 데이터를 저장하거나 외부 시스템으로 전송.
   - 예: Elasticsearch, AWS S3, MongoDB 등.
   - 대표적인 플러그인: `out_elasticsearch`, `out_kafka`, `out_file`.



플루언트디는 위 과정을 거친다. 

## 19.5 컨테이너의 로깅 모델

efk 스택은 단일 머신에서도 무리없지만 운영환경에 맞춰 수평 확장하여 관리하기도 쉽다 

![image-20241221000839846](./images//image-20241221000839846.png)

## 19.6 연습 문제

컴포즈 파일

```
version: "3.7"

services:
  numbers-api:
    logging:
      driver: "fluentd"
      options:
        tag: "numbers.api.{{.ImageName}}"

  numbers-web:
    logging:
      driver: "fluentd"
      options:
        tag: "numbers.web.{{.ImageName}}"

```



# 20장 리버스 프록시를 이용해 컨테이너 HTTP 트래픽 제어하기

외부에서 들어온 트래픽을 컨테이너까지 이어주는 라우팅은 도커 엔진이 담당하지만, 컨테이너가 주시할 수 있는 포트는 하나뿐이다. 클러스터 하나에서 수없이 많은 애플리케이션을 실행해야 하기 때문이고 또한 이들 모두를 http와 https 80/443을 통해 접근가능하게 해야해서 포트가 부족하다.



리버스 프록시는 이런 경우에 유용하다. 

## 20.1 리버스 프록시란?

프록시는 클라이언트를 감춰준다. 리버스 프록시는 서버를 감춰주는것이다.

**리버스 프록시(Reverse Proxy)**는 클라이언트가 직접 백엔드 서버에 요청을 보내지 않고, **프록시 서버가 클라이언트 요청을 받아 백엔드 서버로 전달하고 응답을 다시 클라이언트로 전달**하는 방식의 서버다.

![image-20241221001207357](./images//image-20241221001207357.png)

리버스 프록시 덕분에 컨테이너는 외부에 노출될 필요 없으며, 그만큼 스케일링 업데이트 보안면에서 유리하다.

엔진엑스는 웹 사이트별로 설정 파일을 둘 수 있다(도메인별로)

아래는 각각 다른 파일이다. 

```
1
server {
    server_name api.numbers.local;

    location / {
        proxy_pass             http://numbers-api;
        proxy_set_header       Host $host;
        add_header             X-Host $hostname;         
    }
}


2
server {
    server_name image-gallery.local;

    location = /api/image {
        proxy_pass             http://iotd/image;
        proxy_set_header       Host $host;
        add_header             X-Proxy $hostname;         
        add_header             X-Upstream $upstream_addr;
    }

    location / {
        proxy_pass             http://image-gallery;
        proxy_set_header       Host $host;
        add_header             X-Proxy $hostname;         
        add_header             X-Upstream $upstream_addr;
    }        
}


3
server {
    server_name whoami.local;

    location / {
        proxy_pass             http://whoami;
        proxy_set_header       Host $host;
        add_header             X-Host $hostname;         
    }
}
```

http 요청 헤더에 Host라는 사이트 정보를 넣는데,(도메인) 이 정보로 해당 요청을 처리할 수 있는 사이트의 설정 파일을 찾아 트래픽을 연결한다. 

위 설정파일을 엔진엑스에 넣어주고, 네트워크를 같은 네트워크끼리 묶으면 엔진엑스가 앞단에서 요청을 받아 적절한 컨테이너에게 라우팅 해준다. 

## 20.3 프록시를 이용한 성능 및 신뢰성 개선

캐싱을 활용해서, 로컬디스크나 메모리에 저장해두었다 저장된것을 돌려줄 수 있다.

캐싱 프록시의 장점은 요청 처리하는 시간을 줄일 수 있으며 트래픽을 줄일 수 있어 더많은 요청을 처리할 수 있다.

단 인증 정보를 포함한 요청은 캐싱하지 않으면 개인화된 콘텐츠는 제외할 수 있다. 

```
server {
    server_name image-gallery.local;
    listen 80;
	return 301 https://$server_name$request_uri;
}

server {
	server_name  image-gallery.local;
	listen 443 ssl;
    
    gzip  on;    
    gzip_proxied any;

	ssl_certificate        /etc/nginx/certs/server-cert.pem;
	ssl_certificate_key    /etc/nginx/certs/server-key.pem;
	ssl_session_cache      shared:SSL:10m;
	ssl_session_timeout    20m;
	ssl_protocols          TLSv1 TLSv1.1 TLSv1.2;

	ssl_prefer_server_ciphers on;
	ssl_ciphers 'EECDH+AESGCM:EDH+AESGCM:AES256+EECDH:AES256+EDH';

	add_header  Strict-Transport-Security "max-age=31536000" always;

    # 특정 경로 `/api/image`에 대한 요청을 처리
    # 프록시로 지정된 URL로 요청을 전달 (http://iotd/image)
    # 클라이언트의 Host 헤더를 백엔드 서버로 전달
    # SHORT 캐시 정책을 적용
    # HTTP 상태 코드 200인 응답은 1분 동안 캐싱
    # 캐싱 상태를 나타내는 헤더를 클라이언트 응답에 추가 (X-Cache)
    # 프록시 서버의 호스트 이름을 응답 헤더에 추가 (X-Proxy)
    # 프록시로 전달된 백엔드 서버 주소를 응답 헤더에 추가 (X-Upstream)
    location = /api/image {
        proxy_pass             http://iotd/image;
        proxy_set_header       Host $host;
        proxy_cache            SHORT;
        proxy_cache_valid      200  1m;
        add_header             X-Cache $upstream_cache_status;
        add_header             X-Proxy $hostname;         
        add_header             X-Upstream $upstream_addr;
    }

    # 기본 경로 `/`에 대한 요청 처리
    # 프록시로 지정된 URL로 요청을 전달 (http://image-gallery)
    # 클라이언트의 Host 헤더를 백엔드 서버로 전달
    # LONG 캐시 정책을 적용
    # HTTP 상태 코드 200인 응답은 6시간 동안 캐싱
    # 특정 오류나 시간 초과가 발생하면 캐시된 오래된 데이터를 사용 (http_500, http_502 등 포함)
    # 캐싱 상태를 나타내는 헤더를 클라이언트 응답에 추가 (X-Cache)
    # 프록시 서버의 호스트 이름을 응답 헤더에 추가 (X-Proxy)
    # 프록시로 전달된 백엔드 서버 주소를 응답 헤더에 추가 (X-Upstream)
    location / {
        proxy_pass             http://image-gallery;
        proxy_set_header       Host $host;
        proxy_cache            LONG;
        proxy_cache_valid      200  6h;
        proxy_cache_use_stale  error timeout invalid_header updating
                               http_500 http_502 http_503 http_504;
        add_header             X-Cache $upstream_cache_status;
        add_header             X-Proxy $hostname;         
        add_header             X-Upstream $upstream_addr;
    }

}
```

그 외에도 gzip 압축, 캐시 헤더 추가 등을 활용할 수 있다. 

## 20.5 리버스 프록시를 활용한 패턴의 이해

리버스 프록시가 있어야 적용할 수 있는 세 가지 주요 패턴이 있다.

1. 클라이언트 요청에 포함된 호스트명을 통해 http 혹은 https로 제공되는 애플리케이션에서 콘텐츠 제공하는 패턴. 즉 알맞는 도메인으로 알맞은 컨테이너로 전달하여 요청을 제공한다 
2. 한 애플리케이션이 여러 컨테이너에 걸쳐 실행되는 msa 패턴에 실행된다. 요소 중 일부만을 선택적으로 요청하여 외부에서는 하나의 도메인을 갖지만 경로에 따라 여러 서비스가 요청을 처리하는 구조 
3. 모놀리식 설계를 가진 애플리케이션을 컨테이너로 이주시킬때 유용한 패턴이다. 리버스 프록시를 두어 모놀리식 설게를 가진 애플리케이션의 프론트엔드를 맡기고, 추가되는 기능은 컨테이너로 분할한다. 즉 점진적으로 이동한다. 

# 21장 메시지 큐를 이용한 비동기 통신

api 말고, 컴포넌트 끼리 직접 주고받는 대신 메시지 큐를 둬서 컴포넌트간의 결합을 느슨하게 하는 효과가 있다.

## 21.1 비동기 메시징이란?

api같은 동기적 통신은 서버가 다운돼거나 기능을 상실하거나 타임아웃 시간이 만료될 수 있다. 이는 큰 장애로 이어질 수 있다. 비동기 통신을 적용하면 클라이언트와 서버 사이에 계층이 하나 끼고, 클라이언트는 큐로, 서버는 큐를 주시하다 메시지를 수신하고 처리한다. 

![image-20241221010146631](./images//image-20241221010146631.png)

메시징은 매력적인 수단이지만 몇가지 문제가 있따.

1. 큐를 제공하는 기술의 신뢰성이 뛰어나야함
2. 큐의 사용료
3. 다양한 프로파일에서의 큐 유지

## 21.5 비동기 메시징 패턴 이해하기

pub-sub 패턴에 적합하지 않은 경우는 퍼블리셔가 메시지를 사용하는것이 누구이고 어떻게 처리하며 언제 끝나는지 필요하면 이 패턴을 쓸 수 없다. 이는 request-response패턴에서 적합하다. 

이 패턴에서는 클라이언트 -> 메시지큐 -> 처리 핸들러 -> 응답 메시지 큐 -> 클라이언트 로 처리하는 방식이다. 

대부분 메시지 큐 기술은 fire-and-forget (보내고 신경 끔)을 사용하는것에 적합하다. 

* 이해가 잘 안되는데 일반적인 api에서 처음 클라이언트의 요청을 게속 묶고있는건가? 기술적으로 가능해도 응답이 매우 느릴텐데. 

# 22장 끝없는 정진

## 22.1 도커를 이용한 개념 검증

컨테이너의 위력을 팀원들과 공유하고 싶다면 개념 검증 애플리케이션을 만드는것이 좋다 .

1. 여러개의 컴포넌트를 컨테이너화 한다
2. 도커로 이주하면 전체 ci -cd 사이클을 어떻게 개선할 수 있을지 어필한다
3. 중앙화된 로그 수집 및 컨테이너 정보 수집을 한다



## 22.2 소속 조직에서 도커의 유용함을 입증하라

핵심 이해관계자와 결정권자를 다음과 같이 설득시킬 수 있다.

* 개발자는 전체 애플리케이션 스택을 개발용 컴퓨터에서 운영환경과 동일한 환경으로 실행 가능해서 개발에만 전념 가능하다.
* 운영에 필요한 표준 도구를 갖게 된다.
* 기존 가상 머신에서 컨테이너로 이전하면 줄어드는 서버 대수만큼 큰 비용을 절감 가능하다 -> 운영체제 라이선스 등

## 22.3 운영 환경으로 가는 길

클라우드 환경을 목표로 한다면 쿠버네티스로 시작하는 편이 낫다. 

## 22.4 도커 커뮤니티 소개