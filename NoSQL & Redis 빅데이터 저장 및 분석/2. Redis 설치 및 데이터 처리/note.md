# 2 Redis 설치 및 데이터 처리

[toc]

## 2.1 주요 특징

### 1. Redis의 주요 특징

**1. Redis는 Key-Value 데이터베이스**

**2. Key-Value DB면서 대표적인 In Memory 기반의 데이터 처리 및 저장 기술을 제공**

* 다른 NoSQL에 비해 상대적으로 빠른 Read/Write가 가능함

**3. String, Set, Sorted Set, Hash, List, HyperLogLogs 유형의 데이터를 저장할 수 있ek**

**4) Dump 파일과 AOF(Append Of File) 방식으로 메모리 상의 데이터를 디스크에 저장할 수 있ek**

**5) Master/Slave Replication 기능을 통해 데이터의 분산, 복제 기능을 제공한다**

* Query Off Loading 기능을 통해 Master는 Read/Write를 수행하고, Slave는 Read만 수행할 수 있음

**6) 파티셔닝을 통해 동적인 스케일 아웃인 수평 확장이 가능**

**7) Expriation 기능 지원**

* Expiration 기능은 일정 시간이 지났을 때 메모리 상의 데이터를 자동 삭제할 수 있는 기능으로, 이를 지원한다

### **2. Redis의 주요 업무 영역**

**1. In Memory DB가 제공하는 최대 장점인 빠른 쓰기와 읽기 작업은 가능하지만 모든 데이터를 저장할 수 없다**

지속적인 관리가 요구되는 비즈니스 영역에 사용하는 것은 제한적일 수 밖에 없는데, 기업의 비즈니스 영역과 데이터 성격에 따라 다르겠지만 전형적인 기업 데이터를 저장하기 위한 용도보다 덜 위험한 업무 영역에서 Secondary DB로 사용하는 것이 보편적인 경우다

**2. 주된 비즈니스 영역**

데이터 캐싱을 통한 빠른 쓰기/읽기 작업이 요구되는 업무 영역, IoT 디바이스를 활용한 데이터 수집 및 처리 영역, 실시간 분석 및 통계 분석 영역 등이 있다

**3. 메시지 큐, 머신 러닝, 애플리케이션 잡 매니지먼트, 검색 엔진 업무 영역**

기존의 관계형 DB와 타 NoSQL에 비해 효율적으로 사용할 수 있다

## 2.2 제품 유형

Redis는 대부분의 NoSQL 제품과 같이 듀얼 라이선스를 제공하는데, 사용자의 개발 용도, 개발 환경, 요구되는 기술, 향후 기술 지원과 유지보수 등을 고려하여 선택할 수 있다.

**1. 커뮤니티 에디션**

*  일반적으로 NoSQL은 오픈소스 라이선스를 기반으로 개발과 지원되는 기술로, 해당 SW를 다운로드하여 사용하더라도 별도의 비용이 발생하지 않고, 사용자는 SW 구입 및 설치 비용을 지불하지 않아도 된다

*  해당 개발사는 SW를 무상 제공하지만 사용자가 이를 이용하여 SW를 개발하면서 발생하는 어떠한 문제에 대해서도 개발사는 책임지지 않고, 모든 사용자가 책임진다.

**2. 엔터프라이즈 에디션**

*  사용자가 해당 SW를 사용하면서 발생하게 되는 기술적 문제에 대해 개발사가 책임지고 기술 지원 및 유지보수 작업을 수행하며 긴급 패치가 필요한 경우, 개발사는 우선 제공하게 됨

*  개발사와 사용자는 연간 단위의 유지보수 계약을 체결하게 되며 사용자는 그에 상응하는 비용을 개발사에 지불해야 함

*  커뮤니티 에디션에는 없는 다양한 옵션 기능들이 제공되기 때문에 상황에 따라 알아보자.



### **3Redis 시스템의 아키텍처 구성**

**1) Redis 시스템을 구성하고 있는 Sub 시스템**

*  데이터 저장 엔진

*  분산 시스템

*  복제 시스템

* Index Support

* 관리 툴(redis-server, redis-cli, Redis-benchmark 등) 

**2) 3'rd Party에서 제공하는 다양한 Redis 확장 Module을 함께 연동해서 사용하면 강력한 솔루션으로 활용 가능**

*  Redis Search Engine : 검색 엔진

*  RedisSQL : Redis와 SQLite DB 연동 솔루션

*  RedisGraph : Redis와 Graph DB 연동 솔루션

*  Redis sPiped : Redis 암호화 솔루션

## 2. 레디스 설치

### 도커로 설치하는 예제

```shell
$ docker pull redis:latest
```

* 만약 애플 실리콘 M1 칩셋을 사용 중이라서 오류가 난다면 '**--platform linux/amd64**' 옵션을 추가로 넣어준다

* ```
  docker pull --paltform linux/amd64 redis
  ```

설정파일 디렉토리 생성 및 설정파일 다운로드

```shell
 $ mkdir -p /Users/ysk/redis-book/config 
 $ wget http://download.redis.io/redis-stable/redis.conf -O /Users/ysk/redis-book/config/redis.conf
```



레디스 실행

```she
$ docker run \
-d \
--restart always \
--name redis-book \
-p 6379:6379 \
-e TZ=Asia/Seoul \
-v /Users/ysk/redis-book/config/redis.conf:/etc/redis/redis.conf \
-v /Users/ysk/redis-book/data:/data \
--requirepass "비밀번호" \
redis:latest redis-server /etc/redis/redis.conf
```

* --restart : 재실행 여부. 무시 가능
* --name : 이름 지정
* -v : 볼륨 지정

redis-cli로 접근

```java
$ docker exec -i -t (redis 컨테이너 이름) redis-cli -a "비밀번호"
```

레디스 cli 접속 후  패스워드 인증방법

```
127.0.0.1:6379> AUTH 패스워드
```





Redis 원격 연결

```shell
`#현장에 있는 경우 localhost 를 사용하고 다른 곳에 있는 경우 호스트 ip 를 사용합니다.
$ docker exec -it redis redis-cli -h localhost -p 6379 -a 123456
```

Redis 컨테이너 내부에서 실행

```
$ docker exec -it 컨테이너명 redis-cli

또는 컨테이너 내부에서
$ redis-cli

or

$ redis-cli -p 6379 
```

https://hub.docker.com/_/redis?tab=description

### Redis config

Redis의 환경 설정을 위해 필요한 config 파일을 작성한다. 또한 작성한 config 파일은 host에서 관리할 수 있도록 한다.

```null
# 어떤 네트위크 인터페이스로부터 연결할 수 있도록 할 것인지 관리 (여기에서는 Anywhere)
bind 0.0.0.0

# 사용 포트 관리
port 6379

# Master 노드의 기본 사용자(default user)의 비밀번호 설정
requirepass [사용하고자 하는 비밀번호]

# Redis 에서 사용할 수 있는 최대 메모리 용량. 지정하지 않으면 시스템 전체 용량
maxmemory 1g

# maxmemory 에 설정된 용량을 초과했을때 삭제할 데이터 선정 방식
# - noeviction : 쓰기 동작에 대해 error 반환 (Default)
# - volatile-lru : expire 가 설정된 key 들중에서 LRU algorithm 에 의해서 선택된 key 제거
# - allkeys-lru : 모든 key 들 중 LRU algorithm에 의해서 선택된 key 제거
# - volatile-random : expire 가 설정된 key 들 중 임의의 key 제거
# - allkeys-random : 모든 key 들 중 임의의 key 제거
# - volatile-ttl : expire time(TTL)이 가장 적게 남은 key 제거 (minor TTL)
maxmemory-policy volatile-ttl

# DB 데이터를 주기적으로 파일로 백업하기 위한 설정입니다.
# Redis 가 재시작되면 이 백업을 통해 DB 를 복구합니다.

save 900 1      # 15분 안에 최소 1개 이상의 key 가 변경 되었을 때
save 300 10     # 5분 안에 최소 10개 이상의 key 가 변경 되었을 때
save 60 10000   # 60초 안에 최소 10000 개 이상의 key 가 변경 되었을 때
```

### **2.3.1 Redis 설치 on Linux**

**1) Redis Community Edition**

https://redis.io/download

**2) Redis Enterprise Edition**

https://redis.com/redis-enterprise-software/download-center/software/

**3) Redis 드라이브**

Redis를 기반으로 다양한 개발 언어를 사용할 수 있도록 드라이브를 지원함

https://redis.io/clients

**2. Redis 다운로드 및 설치**

**1) Redis 최신 버전 파일 다운로드**

```
wget http://download.redis.io/redis-stable.tar.gz
```

**2) 압축 해제**

```
tar -xvzf redis-stable.tar.gz
```

**3) 디렉토리명 변경**

```
mv redis-stable redis
```

**Redis 디렉토리 정보**

| **디렉토리, 파일** | **설명**               |
| ------------------ | ---------------------- |
| redis.conf         | Redis Config 파일      |
| src                | 실행 파일이 있는 경로  |
| src/redis-cli      | Client 실행 코드       |
| src/redis-sentinel | Sentinel 실행 코드     |
| src/redis-server   | Redis Server 실행 코드 |

### 2.4 Redis 시작과 종료

**2.4.1 시작&종료 on Linux**

Redis 서버가 설치된 위치의 src 경로에 redis-server.exe라는 실행 코드를 확인할 수 있는데, 설정한 conf 파일명과 경로를 함께 지정해주면 Redis 인스턴스가 시작됨

시작 화면에서 해당 Redis 인스턴스가 standalone 모드인지 cluster 모드인지 확인할 수 있고, 활성화된 인스턴스의 포트 번호와 시스템 프로세스 ID 번호를 함께 출력함

**1) Redis Server 시작**

```
./redis-server redis6379.conf
```

**2) Redis Client 실행 및 서버 종료**

1 Redis Client 실행

```
./redis-cli -p 6379
```

2 Redis 인스턴스 종료

````
shutdown
````

**3) Redis Shell에서 실행할 수 있는 명령어 조회**

```
help

help {실행할 명령어}
```

**4) Redis 서버 상태 명령어**

```
info
```

## **2.5 데이터 처리**

### **2.5.1 용어 설명**

| **용어**  | **설명**                                                     |
| --------- | ------------------------------------------------------------ |
| Table     | 하나의 DB에서 데이터를 저장하는 논리적 구조(관계형 DB에서 표현하는 논리적 개념인 테이블과 동일 |
| Data Sets | - 테이블을 구성하는 논리적 단위- 하나의 Data Sets은 하나의 Key와 한 개 이상의 Field/Element로 구성됨 |
| Key       | 하나의 Key는 하나 이상의 조합된 값으로 표현이 가능함예) 주문번호 또는 주문번호+순번, 년월일+순번 등 |
| Values    | - 해당 Key에 대한 구체적인 데이터 값을 표현함- Values는 하나 이상의 Field 또는 Element로 구성됨 |

### 2.5.2 데이터 입력/수정/삭제/조회

Redis DB에 데이터를 입력/수정/삭제/조회하기 위해서는 반드시 Redis 서버에서 제공하는 명령어를 사용해야 하고, 데이터를 조작할 때는 하나의 Key에 대해 하나 이상의 Field 또는 Element로 표현해야 함

| **종류**  | **설명**                                         |
| --------- | ------------------------------------------------ |
| set       | 데이터를 저장할 때(key, value)                   |
| get       | 저장된 데이터를 검색할 때                        |
| rename    | 저장된 데이터 값을 변경할 때                     |
| randomkey | 저장된 key 중에 하나의 key를 랜덤하게 검색할 때  |
| keys      | 저장된 모든 key를 검색할 때                      |
| exits     | 검색 대상 key가 존재하는지 여부를 확인할 때      |
| mset/mget | 여러 개의 key와 value를 한 번 저장하고 검색할 때 |

## 명령어 실습

```sh
127.0.0.1:6379> set 1111 "ysk"         # key: 1111, value: rinjyu 데이터 저장(set)
OK
127.0.0.1:6379> get 1111                  # key: 1111에 대한 데이터 검색(get)
"ysk"

127.0.0.1:6379> keys *                    # 현재 저장되어 있는 모든 key 출력

127.0.0.1:6379> keys *2            # 현재 저장되어 있는 key 중에 2로 끝나는 key만 검색 "1112"
"1112"
127.0.0.1:6379> del 1112                   # key: 1112 삭제

127.0.0.1:6379> rename 1113 1116           # key: 1113을 key: 1116으로 변경

127.0.0.1:6379> randomkey                  # 현재 key 중에 랜덤으로 key 선택

127.0.0.1:6379> exists 1116                # key: 1116의 존재여부 검색(존재 : 1, 없으면 0)
(integer) 1

127.0.0.1:6379> strlen 1111                # key: 1111의 value 길이(ysk)
(integer) 3 # ysk = 3

127.0.0.1:6379> flushall                   # 현재 저장되어 있는 모든 key 삭제

127.0.0.1:6379> setex 1111 30 ysk 					# ex는 expire(일정 기간 동안만 저장)
OK

127.0.0.1:6379> ttl 1111 									# 현재 남은 시간 확인, 30초 중 22초 남은 상태
(integer) 22

127.0.0.1:6379> mset 1113 "NoSQL User Group" 1115 "PIT"    # 여러 개 필드를 한번에 저장

127.0.0.1:6379> mget 1113 1115              # mset에 저장된 값을 한번에 다중 검색
1) "NoSQL User Group"
2) "PIT"

127.0.0.1:6379> mset seq_no 20230815				 # 연속번호 발행을 위한 key/value 저장
OK

127.0.0.1:6379> get seq_no
"20230815"

127.0.0.1:6379> incr seq_no									# Incremental 증가값 +1 (숫자 마음대로 지정 가능)
(integer) 20230816

127.0.0.1:6379> get seq_no
"20230816"

127.0.0.1:6379> decr seq_no                 # Decremental 감소값 -1 (숫자 마음대로 지정 가능)
(integer)  "20230815"

127.0.0.1:6379> append 1115 " co."          # 현재 value에 value 추가
(integer) 7

127.0.0.1:6379> get 1115
"PIT co."

127.0.0.1:6379> save                        # 현재 입력한 key/value 값을 파일로 저장
OK

127.0.0.1:6379> flushall                    # 모든 key/values 값 제거
OK

127.0.0.1:6379> clear                       # 화면 clear

127.0.0.1:6379> time												# 데이터 저장 시간(unix time in secods) microseconds
1) "1692075248"
2) "328453"

127.0.0.1:5000> exit # 종료 후 디렉토리 이동해보자

도커 같은 경우 볼륨으로 지정한 /data에 존재함
cd /data
ls 
dump.rdb  # SAVE 명령어에 의해 OS 상에 생성된 RDB 파일
```

### RDB 파일을 열어 내용을 읽는(확인하는) 방법

만약 `dump.rdb` 파일의 내용을 확인하고 싶다면, Redis에 대한 스냅샷을 텍스트 또는 JSON 형식으로 내보내는 데 사용할 수 있는 여러 도구와 라이브러리가 있습니다. 예를 들어, `redis-rdb-tools`와 같은 도구를 사용할 수 있습니다.

`redis-rdb-tools`를 사용하려면 다음 단계를 따르세요:

1. 도구를 설치합니다:

   ```
   bashCopy code
   pip install rdbtools python-lzf
   ```

2. `dump.rdb` 파일을 해석하여 읽을 수 있는 형식으로 변환합니다:

   ```
   bashCopy code
   rdb --command json /path/to/dump.rdb > dump.json
   ```

   이 명령은 `dump.rdb` 파일을 읽고 해당 내용을 JSON 형식의 `dump.json` 파일로 출력합니다.

이제 `dump.json` 파일을 열어 Redis의 데이터 스냅샷을 확인할 수 있습니다.



### 이진 형식의 RDB 파일을 직접 읽거나 검색하는 것은 어렵기 때문에 Redis 서버에 import하여 데이터를 검색하는 것이 일반적인 방법

1. Redis-cli 사용

RDB 파일을 열어 내용을 볼 수 있는 방법 중 하나는 Redis의 redis-cli 도구를 사용하여 RDB 파일을 로드하고 데이터를 검색하는 것입니다.
다음과 같은 명령어를 사용하여 RDB 파일을 로드하고 Redis 데이터를 검색할 수 있습니다.

```Java
redis-server --rdb <rdb_file_path>
```

또 다른 방법은 RDB 파일을 Redis 데이터베이스에 직접 import하는 것입니다.
RDB 파일을 import하려면 Redis를 시작한 후 다음과 같은 명령어를 사용합니다.

```Java
redis-cli --rdb <rdb_file_path>
```

**RDB 파일 로드 vs import**
**시기**: RDB 파일을 로드하는 방법은 Redis 서버가 시작될 때 RDB 파일을 로드합니다. 반면 Redis 서버에 RDB 파일을 import하는 방법은 Redis 서버가 이미 실행 중일 때 RDB 파일을 적용합니다.
**방법**: RDB 파일을 로드하는 방법은 Redis 서버가 RDB 파일을 읽어 Redis 데이터베이스를 복원합니다. 반면 Redis 서버에 RDB 파일을 import하는 방법은 RDB 파일을 직접 Redis 데이터베이스에 적용합니다.
**상황**: RDB 파일을 로드하는 방법은 Redis 서버가 비정상적으로 종료되었을 때 데이터를 복구하거나 데이터베이스를 복원할 때 사용됩니다. 반면 Redis 서버에 RDB 파일을 import하는 방법은 Redis 서버의 데이터를 다른 환경으로 이전하거나 데이터를 공유할 때 사용됩니다.

### Redis에서 RDB 파일을 추출하는 방법

1. Redis CLI 사용

Redis CLI를 사용하여 Redis 서버에서 RDB 파일을 추출할 수 있습니다. 다음 명령어를 사용하면 Redis CLI에서 SAVE 명령을 실행하여 RDB 파일을 추출할 수 있습니다. - Redis 데이터베이스의 스냅샷이 RDB 파일로 저장

```Java
SAVE
```

2. Redis의 BGSAVE 명령어 사용

Redis의 BGSAVE 명령을 사용하여 Redis 서버에서 RDB 파일을 추출할 수 있습니다. 다음 명령어를 사용하면 Redis 서버에서 백그라운드에서 SAVE 명령을 실행하여 RDB 파일을 추출합니다. - Redis 서버가 백그라운드에서 SAVE 명령을 실행하고 RDB 파일을 추출

```Java
BGSAVE
```


두 방법 모두 RDB 파일은 Redis 서버가 실행되는 디렉토리에 dump.rdb라는 이름으로 저장됩니다.

## **2.5.3 데이터 타입**

| **종류**      | **설명**                                                     |
| ------------- | ------------------------------------------------------------ |
| Strings       | 문자(text), Binary 유형 데이터를 저장                        |
| List          | 하나의 Key에 여러 개의 배열 값을 지정                        |
| Hash          | 하나의 Key에 여러 개의 Fields와 Value로 구성된 테이블을 저장 |
| SetSorted set | 정렬되지 않은 String 타입Set과 Hash를 결합한 타입            |
| Bitmaps       | 0 & 1로 표현하는 데이터 타입                                 |
| HyperLogLogs  | Element 중에서 Unique한 개수의 Element만 계산                |
| Geospatial    | 좌표 데이터를 저장 및 관리하는 데이터 타입                   |

### 1. Hash 타입

**1 Redis에서 데이터를 표현할 때 기본 타입은 하나의 Key와 하나 이상의 Field/Element 값으로 저장함**

\- Key : 아스키 값을 저장할 수 있음

\- Value : String 데이터를 저장할 수 있고, 추가로 컨테이너(Container) 타입의 데이터들을 저장할 수 있음

\- Container 타입 : Hash, List, Set/Sorted Set이 있음

> Hash Type은 하나의 key에 여러개의 field와 valuefㅡㄹ 저장할 수 있다

**2) Hash 타입의 주요 특징**

1. 기존 관계형 DB에서 PK와 하나 이상의 컬럼으로 구성된 테이블 구조와 매우 흡사한 데이터 유형

2. 하나의 Key는 오브젝트명과 하나 이상의 필드 값을 콜론(:) 기호로 결합하여 표현할 수 있다
   * 예) order:20220318, order_detail:20220318:01

3. 문자값을 저장할 때 인용부호("")를 사용하고, 숫자값을 저장할 때는 인용부호("")가 필요하지 않다

4. 기본적으로 필드 개수는 제한이 없다

5. 사용 명령어
   * hmset, hget, hgetall, hkey, hlen

```sh 
127.0.0.1:6379> hmset order:20230815 customer_name "Wonman & Sports" emp_name "Magee" total 601100 payment_type "Credit" order_filled "Y" ship_date 20230815
// 하나의 key에 여러 field,value 저장 가능
OK
127.0.0.1:6379> hget order:20230815 customer_name # key : order:20230815 에 대한 customer_name 필드 값 검색 
"Wonman & Sports"
127.0.0.1:6379> hget order:20230815 ship_date # 마찬가지로 ship_date 필드 검색
"20230815"

127.0.0.1:6379> hgetall order:20230815 # key order:20230815에 대한 모든 필드와 value 값 검색
 1) "customer_name"
 2) "Wonman & Sports"
 3) "emp_name"
 4) "Magee"
 5) "total"
 6) "601100"
 7) "payment_type"
 8) "Credit"
 9) "order_filled"
10) "Y"
11) "ship_date"
12) "20230815"

127.0.0.1:6379> hexists order:20230815 product_name # 필드 존재 여부 검색 없으면 0 있으면 1 
(integer) 0
127.0.0.1:6379> hexists order:20230815 customer_name
(integer) 1
127.0.0.1:6379> hdel order:20230815 ship_date # ship_date 필드 삭제
(integer) 1

127.0.0.1:6379> hgetall order:20230815
 1) "customer_name"
 2) "Wonman & Sports"
 3) "emp_name"
 4) "Magee"
 5) "total"
 6) "601100"
 7) "payment_type"
 8) "Credit"
 9) "order_filled"
10) "Y"

127.0.0.1:6379> hmset order:20230815 ship_date 20230815 # ship_date 필드 추가

127.0.0.1:6379> hkeys order:20230815 # key order:20230815에 대한 모든 필드명만 출력

127.0.0.1:6379> hvals order:20230815 # key order:20230815에 대한 모든 value만 출력

127.0.0.1:6379> hlen order:20230815 # key 에대한 모든 field 갯수 출력 
```

### 2. List 타입

1. 기존의 관계형 테이블에는 존재하지 않는 데이터 유형으로, 일반적인 프로그래밍 언어에서 데이터를 처리할 때 사용되는 배열 변수와 유사한 데이터 구조

2. 기본적으로 String 타입의 경우, 배열에 저장할 수 있는 데이터 크기 : 512MB

3. 사용 명령어
   * lpush, prange, rpush, rpop, llen, lindex 

### 3. Set 타입

1. List 타입은 하나의 필드에 여러 개의 배열 값을 저장할 수 있는 데이터 구조라면, 
   Set 타입은 배열 구조가 아닌 여러 개의 Element로 데이터값을 표현하는 구조

2. 사용 명령어
   * sadd, smembers, scard, sdiff, sunion

### 4. Sorted Set 타입

1. Set 타입과 동일한 데이터 구조로, 차이점은 저장된 데이터 값이 분류(sorting)된 상태라면 Sorted Set 타입이고 분류되지 않은 상태이면 Set 타입인 것을 알 수 있음

2. 사용 명령어
   * zadd, zrange, zcard, zcount, zrank, zrevrank

### 5. Bit 타입

1.  일반적으로 사용자가 표현하는 데이터는 문자, 숫자, 날짜인데 이를 ASCII 값이라고 표현하는데 컴퓨터는 이를 최종적으로 0, 1로 표현되는 Bit 값으로 변환하여 저장

2. Redis에서 제공되는 Bit 타입은 사용자의 데이터를 0과 1로 표현하고, 컴퓨터가 가장 빠르게 저장할 수 있고 해석할 수 있도록 표현하는 구조

3. 사용 명령어
   * setbit, getbit, bitcount

### 6. Geo 타입

1. Redis DB의 Geo 타입은 위치정보(경도, 위도) 데이터를 효율적으로 저장, 관리할 수 있고 이를 활용한 위치 정보 데이터의 분석 및 검색에 사용할 수 있다
2. 사용 명령어
   * geoadd, geopos, geodist, georadius, geohash

1. **`GEOADD`**: 지정된 키에 하나 이상의 위치 요소를 추가합니다.

   ```css
   GEOADD key longitude latitude member [longitude latitude member ...]
   
   # 위치 추가
   GEOADD locations 126.977063 37.556236 "SeoulStation" 
   ```

2. **`GEODIST`**: 두 지점 사이의 거리를 계산합니다. 옵션으로 단위를 지정할 수 있으며 기본 단위는 미터입니다.

   ```css
   GEODIST key member1 member2 [unit]
   
   GEOADD locations 127.02758 37.49885 "GangnamStation"
   GEODIST locations "SeoulStation" "GangnamStation" km
   ```

3. **`GEOHASH`**: 지정된 모든 요소의 geohash 표현을 반환합니다.

   ```css
   GEOHASH key member [member ...]
   
   GEOHASH locations "SeoulStation"
   ```

4. **`GEOPOS`**: 지정된 모든 요소의 위치 (경도와 위도)를 반환합니다.

   ```css
   GEOPOS key member [member ...]
   
   GEOPOS locations "SeoulStation # 특정 위치의 좌표 가져오기 (GEOPOS)
   ```

5. **`GEORADIUS`**: 지정된 중심에서 주어진 반경 내에 있는 위치 요소를 검색합니다. 단위를 지정할 수 있으며 기본 단위는 미터입니다.

   ```css
   GEORADIUS key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
   
   GEORADIUS locations 126.977063 37.556236 5 km # 특정 위치에서 반경 5km내 위치 
   ```

6. **`GEORADIUSBYMEMBER`**: 지정된 멤버 주변의 주어진 반경 내에 있는 위치 요소를 검색합니다. 단위를 지정할 수 있으며 기본 단위는 미터입니다.

   ```css
   GEORADIUSBYMEMBER key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
   ```

7. **`GEOALONGBYMEMBER`** (Redis 7.0 이상): 지정된 멤버 주변의 지정된 경도의 지점을 기준으로 위치 요소를 검색합니다.

   ```css
   GEOALONGBYMEMBER key member along m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
   ```

8. **`GEOALONG`** (Redis 7.0 이상): 지정된 중심을 기준으로 지정된 경도의 지점을 기준으로 위치 요소를 검색합니다.

   ```css
   GEOALONG key longitude latitude along m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count]
   ```



### 7.HyperLogLogs 타입

관계형 DB의 테이블 구조에서 Check 제약 조건과 유사한 개념의 데이터 구조

1. Redis DB에서도 관계형 DB에서와 같이 동일하게 특정 Field 또는 Element에 저장되어야 할 데이터 값을 미리 생성하여 저장한 후에 필요에 따라 연결(Link)하여 사용할 수 있는 데이터 타입

2. 사용 명령어
   * pfadd, pfcount, pfmerge

## **2.6 Redis 확장 Module**

**1) Redis Server**

빅데이터 저장 및 관리 기술을 제공하는 소프트웨어

**2) Redis Extent Module**

Redis Server의 소스를 이용한 다양한 기능들을 개발하여 배포하는 것

| **모듈명**   | **설명**                                                     |
| ------------ | ------------------------------------------------------------ |
| REJSON       | JSON 데이터 타입을 이용하여 데이터를 처리할 수 있는 모듈     |
| REDISQL      | Redis Server에서 관계형 DB인 SQLite로 데이터를 저장할 수 있는 모듈 |
| RedisSearch  | Redis DB 내에 저장된 데이터에 대한 검색엔진을 사용할 수 있는 모듈 |
| Redis-ML     | Machine Learning Model Server를 Redis 서버에서 사용할 수 있는 모듈 |
| Redis-sPiped | Redis Server로 전송되는 데이터를 암호화할 수 있는 모듈       |

**2.6.1 REJSON**

Redis Server 내에서도 JSON 데이터 타입을 저장할 수 있게 해주는 확장 모듈

**2.6.2 REDISQL**

NoSQL 기술을 도입하면 기존에 구축된 관계형 데이터베이스를 활용할 수 밖에 없는 경우가 발생하는 데, 이 같은 경우에는 확장 모듈 REDISQL을 사용하면 Redis 서버와 관계형 DB인 SQLite를 연동해서 사용할 수 있다

# 2.7 Lua Function & Script

1. Lua는 매우 가볍과 내장 가능한 Sript Programming Language 중에 하나
   * 절차향 프로그래밍과 객체지향 프로그래밍이 가능하고 데이터베이스를 기반으로 하는 프로그래밍이 가능함

2. 간단한 절차형 프로시저와 배열로 데이터를 저장하고 결합할 수 있으며 동적으로 코딩할 수 있을 뿐만 아니라 가상머신 기반에서 바이트 코드를 해석하여 실행할 수 있고, 증분 가비지 컬렉션으로 메모리를 자동 관리할 수 있도록 프로그래밍 할 수 있다

3. Lua는 브라질 리오네자네이로에 있는 교황청 카톨릭 대학교의 PUC-Rio 팀에서 개발 및 유지 관리되고 있다

4. Redis Server는 아키텍처 내부에 Lua Interpreter를 내장해 두었으며, 사용자는 미리 작성해둔 Lua 스크립트 또는 Lua 함수를 Redis 서버 내에서 직접 실행할 수 있다

5. Redis Server는 다양한 Lua 함수를 제공하며 사용자는 이를 통해 데이터를 검색, 수정, 삭제할 수 있다
   * 대표적으로 eval, evalsha 함수

