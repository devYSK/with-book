# 5 Redis 아키텍처

<img src="./images/note//image-20230815160401897.png">

## **1. 메모리 영역**

**1. Resident Area**

*  사용자가 Redis Server에 접속해서 처리하는 모든 데이터가 가장 먼저 저장되는 영역

*  실제 작업이 수행되는 공간이며 WorkingSet 영역이라고 표현한다

**2. Data Structure**

Redis Server를 운영하다보면 발생하는 다양한 정보와 서버 상태를 모니터링하기 위해 수집한 상태 정보를 저장하고 관리하기 위한 메모리 공간이 필요한데, 이를 저장하기 위한 메모리 영역



## 2. 파일 영역

**1. AOF 파일(스냅샷 데이터)**

*  중요한 데이터의 경우, 사용자의 필요에 따라 지속적으로 저장해야할 필요가 있는데, 이를 위해 제공되는 디스크 영역을 말함

**2. DUMP 파일**

AOF 파일과 같이 사용자 데이터를 디스크 상에 저장할 수 있지만, 소량의 데이터를 일시적으로 저장할 때 사용되는 파일을 말함

### 3. 프로세스 영역

**1. Server Process**

*  redis-server.exe 또는 redis-sentinel.exe 실행 코드에 의해 활성화되는 프로세스

*  Redis 인스턴스를 관리해주고, 사용자가 요구한 작업을 수행하는 프로세스

 4개의 멀티 쓰레드로 구성되어있다

* main thread : Redis Server에 수행되는 대부분의 명령어와 이벤트를 처리하는 역할을 수행함
* sub thread 1(BIO-Close-File) : 쓰레드 1은 AOF(Append Only File)에 데이터를 Rewrite할 때 기존 파일은 Close하고 새로운 AOF 파일에 Write할 때 사용됨
*  sub thread 2(BIO-AOF-Resync) : 쓰레드 2는 AOF에 쓰기 작업을 수행할 때 사용됨
*  sub thread 3(BIO-Lazy-Free) : 쓰레드 3은 UNLINK, FLUSHALL, FLUSHDB 명령어를 실행할 때 빠른 성능을 보장하기 위해 백그라운드에서 사용됨

## 레디스 메모리 영역 확인 명령어

### redis multi-thread 확인 - ps -eLF|grep redis

```
ps -eLF|grep redis 
```

* PID 

### info 명령어 - info MEMORY

```shell
127.0.0.1:6379> info MEMORY
```

결과와 설명

```
# Memory 단위는 바이트이다. 
used_memory:1684520                # Redis에 의해 할당된 메모리 총량 (바이트)
used_memory_human:1.61M            # 사람이 읽기 쉬운 형식으로의 할당된 메모리
used_memory_rss:9015296            # 프로세스의 RSS (Resident Set Size) 메모리 크기
used_memory_rss_human:8.60M        # 사람이 읽기 쉬운 형식의 RSS 메모리 크기
used_memory_peak:1863808           # 이 세션의 최대 메모리 사용량
used_memory_peak_human:1.78M       # 사람이 읽기 쉬운 형식의 최대 메모리 사용량
used_memory_peak_perc:90.38%       # 최대 메모리 사용량에 대한 현재 사용량의 백분율
used_memory_overhead:897080        # Redis 내부 구조의 오버헤드 메모리
used_memory_startup:895024         # Redis 시작 시 할당된 메모리 크기
used_memory_dataset:787440         # 데이터 세트에 사용된 메모리
used_memory_dataset_perc:99.74%    # 전체 사용 메모리 중 데이터 세트가 차지하는 비율
allocator_allocated:2731888        # 메모리 할당자가 할당한 바이트
allocator_active:9175040           # 메모리 할당자가 활성화된 바이트
allocator_resident:10813440        # 메모리 할당자가 거주하는 바이트
total_system_memory:2085150720     # 시스템의 총 메모리 크기
total_system_memory_human:1.94G    # 사람이 읽기 쉬운 형식의 시스템 총 메모리 크기
used_memory_lua:31744              # Lua 엔진에 할당된 메모리 크기
used_memory_vm_eval:31744          # VM 구조에 할당된 메모리 크기
used_memory_lua_human:31.00K       # 사람이 읽기 쉬운 형식의 Lua에 할당된 메모리 크기
used_memory_scripts_eval:0         # 스크립트 평가에 사용된 메모리 크기
number_of_cached_scripts:0         # 캐시된 스크립트 수
number_of_functions:0              # 캐시된 함수 수
number_of_libraries:0              # 캐시된 라이브러리 수
used_memory_vm_functions:32768     # VM 함수에 할당된 메모리 크기
used_memory_vm_total:64512         # VM 전체에 할당된 메모리 크기
used_memory_vm_total_human:63.00K  # 사람이 읽기 쉬운 형식의 VM 전체 할당 메모리
used_memory_functions:184          # 함수에 할당된 메모리 크기
used_memory_scripts:184            # 스크립트에 할당된 메모리 크기
used_memory_scripts_human:184B     # 사람이 읽기 쉬운 형식의 스크립트에 할당된 메모리
maxmemory:0                        # 설정된 최대 메모리 크기 (제한이 없으면 0)
maxmemory_human:0B                 # 사람이 읽기 쉬운 형식의 최대 메모리 크기
maxmemory_policy:noeviction        # 최대 메모리에 도달했을 때 적용되는 정책
allocator_frag_ratio:3.36          # 할당된 메모리와 활성 메모리 사이의 비율
allocator_frag_bytes:6443152       # 할당된 메모리와 활성 메모리 사이의 바이트 차이
allocator_rss_ratio:1.18           # 활성 메모리와 거주 메모리 사이의 비율
allocator_rss_bytes:1638400        # 활성 메모리와 거주 메모리 사이의 바이트 차이
rss_overhead_ratio:0.83            # RSS 메모리와 거주 메모리 사이의 비율
rss_overhead_bytes:-1798144        # RSS 메모리와 거주 메모리 사이의 바이트 차이
mem_fragmentation_ratio:5.42       # 메모리 파편화 비율
mem_fragmentation_bytes:7351448    # 메모리 파편화 바이트
mem_not_counted_for_evict:0        # 제거를 위해 고려되지 않은 메모리
mem_replication_backlog:0          # 복제 백로그에 사용된 메모리
mem_total_replication_buffers:0    # 총 복제 버퍼에 사용된 메모리
mem_clients_slaves:0               # 슬레이브 클라이언트에 사용된 메모리
mem_clients_normal:1800            # 일반 클라이언트에 사용된 메모리
mem_cluster_links:0                # 클러스터 링크에 사용된 메모리
mem_aof_buffer:0                   # AOF 버퍼에 사용된 메모리
mem_allocator:jemalloc-5.2.1       # 사용 중인 메모리 할당자의 종류 및 버전
active_defrag_running:0            # 활성 메모리 조각 제거가 실행 중인지 여부 (1이면 실행 중)
lazyfree_pending_objects:0         # 지연된 해제로 인해 해제 대기 중인 객체의 수
lazyfreed_objects:0                # 지연된 해제로 해제된 객체의 수
```

### 실시간 사용중인 메모리 크기 출력 - redis-cli info | grep

```shell
redis-cli -a '패스워드' -r 100 -i 1 info | grep used_memory_human
```

결과

```
used_memory_human:1.62M
```

### 실시간 사용 가능한 시스템 메모리 크기 출력

```sh
redis-cli -a '패스워드' -r 100 -i 1 info | grep total_system_memory_human;
```

### Redis 메모리 영역에 대한 HIT, MISSESS (캐시 히트, 미스)

명령어 

```
# redis-cli 내부에서 보는데, 이러면 모든 stat이 나온다
info status
```

redis-cli 외부에서

```sh
redis-cli -a '패스워드' info stats | grep "keyspace*"
```

또는

```shell
redis-cli -a '패스워드' info stats | grep "keyspace_hits\|keyspace_misses"
```



# 5.2 시스템 & DISK 하드웨어 사양

| **내용**                          | **최소 사양**                             | 권장 사양                           |
| --------------------------------- | ----------------------------------------- | ----------------------------------- |
| # of nodes per cluseter (노드 수) | 3 nodes                                   | >= 3nodes                           |
| # of cores per node (CPU 코어 수) | 4 cores                                   | >= 8 nodes                          |
| RAM                               | 15 GB                                     | >= 30 GB                            |
| Storage                           | SATA(Redis on Flash 모드는 RAM 크기 X 10) | SSD                                 |
| Persistent Storage                | RAM 크기 X 3                              | >= RAM 크기 X 6(>= RAM + Flash) X 5 |
| Network                           | 1G                                        | >= 10G                              |

## 1. 노드 수(# of nodes per cluster)

하나의 Standalone Server를 구축하는 경우, 

* Master Server 1대, Slave Server 1대, FailOver와 LoadBalancing을 위한 Sentinal Server 1대로 구성하는 경우, 최소 3대의 서버가 요구됨

*  Master Server와 Slave Server는 실제 데이터를 저장하는 서버이기 때문에 최적화된 서버 사양이어야 함

*  Sentinal Server는 사용자 데이터를 저장하지 않기 떄문에 최소 사양으로 구성해야 함

*  Master 1대와 Slave 1대는 이중 복제이기 때문에 크리티컬한 비즈니스 환경에서는 필요에 따라 삼중 복제 시스템이 요구될 수도 있다.

>  Standalone 또는 Cluster Server를 구축하는 경우, 하나의 복제 시스템 구축을 위해서는 요구되는 최소 서버 대수는 Master Server 1대, Slave Server 1대, Sentinal Server 1대로 구성되어야 한다. 즉 3대

## 2. CPU Core 수(# of cores per node)

서버당 CPU 코어 수.

* Small 비즈니스 환경  : 4 Core 이하

* Medium 비즈니스 환경 : 4 Core ~ 8 Core 이하

* Big 비즈니스 환경 : 8 Core ~ 16 Core 이하

## 3. RAM 크기

**1) Redis 서버를 위한 최소 권장 사양 : 14~15GB**

하나의 시스템이 총 16GB의 메모리 크기를 가지고 있다면 그중에 약 90~95% 공간을 Redis Server가 사용할 수 있어야 한다

**2) RAM 크기는 사용자 세션 수, 처리하는 데이터양, 처리방법(배치, 실시간), 초당 데이터 발생량에 의해 결정된다**

**3) RAM 크기를 결정할 수 있는 비즈니스 규모에 대한 사전 수집 및 분석결과가 없는 상태에서의 제안 가이드라인**

* Small 비즈니스 환경 : 16GB 이하

* Medium 비즈니스 환경 : 32GB ~ 64GB 이하

* Big 비즈니스 환경 : 64GB ~ 128GB 이상

## 4. 스토리지(Storage) 타입

\- SATA 타입 디스크 저장 장치 : 가장 저렴한 비용으로 데이터를 효율적으로 저장관리할 수 있는 최선의 방법

\- SSD 타입 저장 장치 : 많은 데이터를 빠른 시간 내에 처리해야 하는 것이 목표인 경우 사용을 권장함

그러나 Redis 아키텍처는 RAM 기반이므로 SSD던 SATA던 과도한 비중을 둘 필요가 없다.

## 5. 스토리지 크기(Persistent Storage)

Redis Server에서 처리하려는 

* 초당 발생랑, 
* 저장 보관 빈도(년/월/일), 
* 저장해야할 총 데이터 양

 등을 조사 분석하여 결정해야 한다

**Redis Server를 운영하게 되면 효율적인 운영, 관리를 위해 요구되는 디스크 공간도 반드시 존재해야 한다.**

**스토리지 크기에 대한 가이드라인**

*  최소 스토리지 크기 = 사용자 데이터의 총 크기 + (RAM 크기 x 3)

*  권장 스토리지 크기 = 사용자 데이터의 총 크기 + (RAM 크기 x 6)

| **항목**                | **예1** | 예2   | 예3  | 예4  |
| ----------------------- | ------- | ----- | ---- | ---- |
| 데이터베이스 사이즈(GB) | 10      | 10    | 40   | 40   |
| 샤드 개수               | 4       | 16    | 5    | 15   |
| 샤드 사이즈(GB)         | 2.5     | 0.625 | 8    | 2.67 |
| 권장 디스크 공간(GB)    | 73      | 191   | 328  | 723  |

## 6. 네트워크

\- Redis Server를 운영하기 위해 요구되는 최소 네트워크 환경 : 1G

\- 적극 권장 환경 : 10G 이상



Redis 같은 스케일 아웃 or 분산-복제 기술을 제공하는 서비스는 CPU 보다는 MEMORY 중심이다.

# 5.3 메모리 운영기법

Redis Server도 사용자의 필요에 따라 중요 데이터를 디스크 저장 장치에 DUMP, AOF 형태로 저장할 수 있는데, 

파일 시스템 기반의 저장 관리 메커니즘과는 기본 작동 방식이많이 다르기 때문에 파일 시스템 기반이라고 표현하지 않는다.



Redis는 In-Memory  DB에서 최적의 성능보장을 위해 LRU, LFU 알고리즘을 제공한다.

## 1. LRU(Least Recently Used) 알고리즘

**LRU 알고리즘 :** 가장 최근에 처리된 데이터들을 메모리 영역에 최대한 재배치시키는 알고리즘

사용할 수 있는 메모리 크기는 정해져 있는데, 그 공간에 데이터를 저장해야 한다면 

가장 최근에 입력/수정/삭제/조회된 데이터를 저장하고, 오래 전에 처리된 데이터는 메모리로부터 제거하여 최근 사용된 데이터들이 최대한 메모리 상에 존재할 수 있도록 운영하는 것을 말한다.

**사용자가 처리한 데이터들 중에 오래 전에 처리한 데이터에 비해 상대적으로 최근에 처리한 데이터들이 재사용, 재검색 될 가능성이 단 1%라도 높기 때문에 메모리 영역으로부터 즉시 데이터를 참조할 수 있도록 해줌으로써 성능 개선에 도움을 준다**



**LRU 설정 방법**

redis.conf 파일에 관련 파라미터 설정

```sh
$ vi redis.conf 

maxmemory      3000000000      # Redis 인스턴스를 위한 총 메모리 크기 
maxmemory-sample    5          # LRU 알고리즘에 의한 메모리 운영
```

| **파라미터**     | **설명**                                                     |
| ---------------- | ------------------------------------------------------------ |
| maxmemory        | Redis Server를 위해 활성화할 수 있는 최대 메모리 크기를 제한할 때 사용함 |
| maxmemory-sample | LRU 알고리즘에 대한 메모리 운영 시에 사용됨                  |

### LRU 알고리즘을 적용했을 때 Redis Server의 메모리 상태를 메모리 모니터링하는 방법

```sh
$ redis-cli -p {포트번호} --lru-test {값}
$ redis-cli -p 5000 --lru-test 10000000
```

## 2. LFU(Least Frequently Used) 알고리즘

**LFU 알고리즘** : 가장 최근에 사용된 데이터들을 메모리 영역에 최대한 재배치하는 알고리즘

* 단점 :  모든 데이터들이 재사용 및 재검색되는 것이 아니다.

자주 참조되는 데이터만 배치하고 그렇지 않은 데이터들은 메모리로부터 제거하여 자주 참조되는 데이터들이 배치될 수 있도록 운영하는 방법

**LFU 설정 방법**

redis.conf 파일에 관련 파라미터 설정

```sh
$ vi redis.conf 

lfu-log-factor     10       # LFU 알고리즘에 의한 메모리 운영(Default 10) 
lfu-decay-time     1
```



# 5.4 LazyFree 파라미터

빅데이터에 대한 쓰기 작업이 발생하면 Max 메모리에 돋라하게 되는데, 이때 연속 범위의 키에 대한 삭제 작업을 수행하게 되면 성능 지연 문제가 발생하게 된다

이 문제를 LazyFree 파라미터는 LazyFree 쓰레드를 이용하여 백그라운드 작업을 수행해주기 때문에 빠른 처리가 가능해진다

*  별도의 백그라운드 쓰레드를 통해 입력과 삭제 작업이 지연되지 않고 연속적으로 수행할 수 있도록 해줌
* LazyFree 쓰레드는 Redis 아키텍처 서버 프로세스 스레드중의 1개이다

## 1. LazyFree 파라미터 설정 방법

```sh
$ vi redis.conf

lazyfree-lazy-eviction     no  # yes 권장(unlink로 삭제하고 새 키 저장)
lazyfree-lazy-expire       no  # yes 권장(unlink로 만기된 키 삭제)
lazyfree-lazy-server-del   no  # yes 권장(unlink로 데이터 변경)
slave-lazy-flush           no  # yes 권장(복제 서버가 삭제 후 복제할 때 FlushAll async 명령어로 삭제)
```

**1. lazyfree-lazy-eviction**  - Yes로 사용

*  메모리 영역이 full 되었을 때, 연속적인 범위의 key 값을 삭제하면 기존 메모리 영역에 저장되어 있던 데이터는 del 명령어에 의해 삭제되는데 이는 서버 프로세스의 main thread에 의해 실행되면서 `블록킹(작업 지연 현상)이 발생함`

*  파라미터 값을 yes로 설정하면 del 명령어가 아닌 unlink 명령어가 실행되고 서버 프로세스의 sub thread 3에 의해 백그라운드에서 수행되기 때문에 `블록킹 현상을 피할 수 있게 됨으로써 성능을 향상시킬 수 있다`

**2. lazyfree-lazy-expire** - Yes로 사용

*  메모리 상에 무효화된 키 값을 삭제하는 데 내부적으로 del 명령어가 실행되면서 블록킹 현상이 발생하는데,   파라미터 값을 yes로 설정하면, unlink 명령어가 실행되면서 블로킹 현상을 피할 수 있다

**3) lazyfree-lazy-server-del** -Yes로 사용

메모리 상에 이미 저장되어 있는 키 값에 대해 set 또는 rename 명령어를 실행하면 내부적으로 del 명령어가 실행되면서 블록킹 현상이 발생하는데, 블록킹 현상을 피하기 위해서 파라미터 값을 yes로 설정해야 한다

**4) slave-lazy-flush** - Yes 사용

\- 기존 데이터를 빠른 시간 내에 삭제하고 동시에 다시 복제 작업을 빠르게 수행할 때 사용한다.

\- 파라미터 값을 yes로 설정하면, 빠른 동기화 작업을 수행할 수 있음



# 5.5 데이터 Persistence

Redis에 데이터를 지속적으로 저장하는 방법에는 `SAVE 명령어`를 이용하는 방법과 `AOF 명령어`로 저장하는 방법이 있다

* Redis도 RDB처럼 파일 기반으로 데이터 저장 기능을 제공한다.

## RDB 및 AOF 관련 파라미터

```sh
vi redis.conf

save 		60 		1000		// 60초마다 1,000 Key 저장

appendonly						 	yes		   // AOF 환경 설정
appendonlyname	"appendonly.aof" // AOF 파일명
```

## 1. RDB 파일을 이용하여 저장하는 방법

SAVE 명령어를 이용하여 일정주기(interval)마다 일정한 개수의 key 데이터-셋 값을 디스크 상에 dump.rdb 파일로 저장하는 방법이다.

*  RDB 파일에는 SAVE 명령어를 실행한 시점에 메모리 상에 저장된 모든 데이터는 snapshot 형태로 저장해 준다

```sh
vi redis.conf

save 		60 		1000		// 60초마다 1,000 Key 저장
```

**SAVE 명령어**

```
127.0.0.1:6379> SAVE
```

**장점**

사용자가 저장 주기와 저장 단위를 결정할 수 있고, 시스템 자원이 최소한으로 요구된다

**단점**

데이터를 최종 저장한 이후 새로운 저장이 수행되는 시점 전에 시스템 장애가 발생하는 경우, 데이터 유실이 발생할 수 있기 때문에 지속성이 떨어진다

## 2. AOF(Append Only File) 명령어를 이용하여 저장하는 방법 

디스크 상에 appendonly.aof 파일로 저장하는 방법을 말함

*  redis-shell 상에서 brewriteaof 명령어를 실행한 이후에 입력, 수정, 삭제되는 모든 데이터를 저장해 준다.

**설정**

```sh
vi redis.conf

appendonly yes                     # AOF 환경 설정
appendfilename "appendonly.aof"    # AOF 파일명, 디스크 상에 파일로 데이터가 저장될 파일명 지정
```



**brewriteaof 명령어**

해당 명령어가 실행된 이후 모든 데이터를 자동 저장하는 기능

```sh
127.0.0.1:6379> bgrewirteaof
```

##  RDB 방법과 AOF 방법의 차이

| **AOF(Append Only File**                                    | **RDB(snapshot)**                                            |
| ----------------------------------------------------------- | ------------------------------------------------------------ |
| 시스템 자원이 집중적으로 요구(지속적인 쓰기 작업이 발생)    | 시스템 자원이 최소한으로 요구(특정 시점에 쓰기 작업이 발생)  |
| 마지막 시점까지 데이터 복구가 가능                          | 지속성이 떨어짐(특정 시점마다 저장)                          |
| 대용량 데이터 파일로 복구 작업 시 복구 성능이 떨어짐        | 복구 시간이 빠름                                             |
| 저장 공간이 압축되지 않기 때문에 데이터 양에 따라 크기 결정 | 저장 공간이 압축되기 때문에 최소 필요(별도의 파일 압축이 필요 없음) |

# 5.6 Copy on Write

`메모리 상에 로더된 데이터`를 하나의 부모 프로세스가 참조하고 있는 와중에 자식 프로세스가 `동시에 참조` 관계를 가지게 되는 경우, 서버 프로세스는 별도의 저장 공간에 이를 복제하게 되는 것을 Copy On Write(디스크 영역에 저장) 라고 한다.

Copy On Write가 빈번하게 발생하게 되면 `해당 오퍼레이션은 지연되고, Redis Server 전체의 성능 지연 문제로 이어지게 된다.`

##  Copy On Write가 발생하는 경우

**1. SAVE 파라미터에 의해 주기적으로 RDB 파일을 생성할 때**

SAVE 명령어 또는 관련 파라미터는 Redis WorkingSets 영역에 저장되어 있는 데이터를 디스크의 dump.rdb 파일로 저장할 때 사용된다

**2 BGSAVE 명령어에 의해 RDB 파일을 저장할 때**

SAVE 명령어와 함께 BGSAVE 명령어도 동일한 방법으로 Copy On Write가 발생한다

**3 BGREWRITEAOF 명령어에 의해 AOF 파일을 저장할 때**

SAVE, BGSAVE는 dump.rdb 파일에 쓰기 작업이 발생한다면 BGREWRITE는 appendonly.aof 파일에 쓰기 작업이 발생하는 경우로 Copy On Write가 발생한다

**4 auto-aof-rewirte-percentage 파라미터에 의해 AOF 파일을 재저장할 때**

*  AOF 파일이 가득 채워진 상태에서 계속적으로 데이터를 저장해야 하는 경우, AOF 파일을 비우고 처음부터 다시 쓰기 작업을 수행하는 경우도 발생

성능 이슈가 발생하는 비즈니스 환경에서 해당 파라미터의 적극적인 사용은 권장하지 않는다

**5 Master-Slave, Partition-Replication Server 환경으로 구동할 때**

Master-Slave, Partition-Replication Server는 Master Server의 데이터를 Slave Server에 복제할 때 Copy on Write가 발생한다

# 6.7 Benchmark For Redis

Redis Server는 redis-benchmark.exe 실행코드를 제공하는데 이를 통해 `실제와 같은 데이터를 임의로 발생시켜` 성능 이슈가 발생할 수 있는 `가상 상태를 만들어` 성능 최적화 작업을 수행할 수 있다.

**문법**

```sh
redis-benchmark [-h {host}] [-p {port}] [-c {clients}] [-n {requests}] [-k {boolean}]
```

## 실습

현재 사용중인 메모리 확인

```sh
$ redis-cli -a '패스워드' -r 100 -i 1 info | grep used_memory_human:
used_memory_human:1.62M
used_memory_human:1.61M
used_memory_human:1.61M
used_memory_human:1.61M
```

### Redis Server 성능 테스트 (Batch Job)

```sh
$ redis-benchmark -a '패스워드' -q -n 100000

PING_INLINE: 130039.02 requests per second, p50=0.175 msec
PING_MBULK: 132802.12 requests per second, p50=0.183 msec
SET: 132978.73 requests per second, p50=0.175 msec
GET: 132450.33 requests per second, p50=0.183 msec
INCR: 133333.33 requests per second, p50=0.183 msec
LPUSH: 135685.22 requests per second, p50=0.175 msec
RPUSH: 133689.83 requests per second, p50=0.183 msec
LPOP: 133333.33 requests per second, p50=0.183 msec
RPOP: 134770.89 requests per second, p50=0.175 msec
SADD: 134048.27 requests per second, p50=0.175 msec
HSET: 133511.34 requests per second, p50=0.183 msec
SPOP: 134952.77 requests per second, p50=0.175 msec
ZADD: 133868.81 requests per second, p50=0.175 msec
ZPOPMIN: 135318.00 requests per second, p50=0.175 msec
LPUSH (needed to benchmark LRANGE): 124223.60 requests per second, p50=0.183 msec
LRANGE_100 (first 100 elements): 65274.15 requests per second, p50=0.375 msec
LRANGE_300 (first 300 elements): 26392.19 requests per second, p50=0.911 msec
LRANGE_500 (first 500 elements): 18782.87 requests per second, p50=1.303 msec
LRANGE_600 (first 600 elements): 15900.78 requests per second, p50=1.535 msec
MSET (10 keys): 134770.89 requests per second, p50=0.183 msec
```

### set, lpush 명령어에 대한 성능 평가

```sh
$ redis-benchmark -a '패스워드' -t set,lpush -n 100000 -q
```

### set 명령어에 대한 성능 평가

```sh
$ redis-benchmark -a '패스워드' -t set -r 100000 -n 1000000
```



# 5.8 관리 명령어

| **파라미터**                         | **설명**                                                     |
| ------------------------------------ | ------------------------------------------------------------ |
| info                                 | Redis Server의 현재 상태 확인                                |
| select                               | Redis Server 내에 생성되어 있는 DB로 switch 할 때 사용       |
| dbsize                               | 현재 데이터베이스에 생성되어 있는 Keys 수                    |
| swapdb                               | 현재 데이터베이스에 할당할 swapDB 생성                       |
| flushall / flushdb                   | 현재 생성되어 있는 모든 Keys 및 db 삭제                      |
| client listclient getnameclient kill | 현재 Redis Server에 접속되어 있는 Client 정보 조회Client명 조회해당 Client 제거 |
| time                                 | Redis Server의 현재 시간                                     |



# 5.9 Data Export & Import

**rdb 파일을 import하는 방법**

```sh
$ redis-cli -a '패스워드' --rdb /RDB파일 위치/dump.rdb
```

**appendonly 명령어에 의해 Export된 aof 파일을 Import 하는 방법**

```sh
$ redis-cli -a '패스워드' -n 1000 --pipe < appendonly.aof
```

**CSV 파일 export**

```sh
$ redis-cli -a '패스워드' --csv --scan > 20230815.csv
```

# 5.10 Redis Serialization Protocol & Mass Insertion

## 1. Luke Protocol을 이용한 업로드 방식

일반적으로 대량의 데이터를 Redis Client를 통해 Redis Server로 저장해야 하는 경우 전형적인 Redis 명령어로 작성된 데이터 파일을 생성하여 입력하는 방법을 선택하지만 이와 같은 방법은 대량의 데이터를 저장하는 방법으로는 좋은 방법이 아니다

클라이언트 레벨에 작성된 텍스트 파일의 명령어는 네트워크를 통해 Redis Server로 전송되어야 하고 처리 결과는 다시 클라이언트로 전달되는 방식이기 때문에 빠른 성능을 보장할 수 없기 때문이다.

### Luke Protocl을 이용한 입력 방식 예제

```sh
$ vi luke_test.data
set 1101 JMJOO
set 1102 YHJOO
set 1103 KOHONG

$ ../src/redis-cli -p 5000 flushall
OK

$ cat test_data.txt | ../src/redis-cli -p 5000 --pipe
All data transferred. Waiting for the last reply..
Last reply received from server.
errors: 0, replies: 3


$ ../src/redis-cli -p 5000 --scan
1101
1102
1103
```

## 2. Request Response Protocol을 이용한 업로드 방식

### 1) RERP(Redis Serialization Protocol) 방식 

*  Luke Protocol의 최대 단점은 업로드의 성능 지연 문제가 있는데, 이를 해소하는 방법이다

 원시(Raw) 형식의 Redis  Protocol이 내장된 텍스트 파일을 사전에 작성하여 호출하는 방식

| **구분자** | **용도**                    | **예시**                                                     |
| ---------- | --------------------------- | ------------------------------------------------------------ |
| +          | 문자열(Simple Strings)      | "+JMJOO\r\n"<br /><br />JMJOO는 문자열로, + 기호를 앞에 붙이고 문자열 끝에 \r\n(Carriage Return과 Line-Feed) 시그널을 붙임 |
| :          | 숫자(Integer)               | ":1000\r\n"<br /><br />숫자 1000 앞에 :을 붙인 후 \r\n을 붙임 |
| $          | 대용량 문자열(Bulk Strings) | "$1101\r\nSki Boots\r\n""<br/>$<br />1102\r\n\r\n"           |
| *          | 배열(Array)                 | "*0\r\n""<br />*2\r\n$3\r\nnfoo\r\n$4\r\nbar\r\n""<br />*3\r\n:1\r\n:2\r\n:2\r\n" |
| -          | 에러 값(Errors)             | "-Error message\r\n"                                         |

* 구분자는 저장해야 할 데이터 값 바로 앞에 배치해야 함

*  구분자는 값의 속성을 의미함

*  배열 값을 저장할 때는 * 기호를 사용함

*  특정 에러 메시지를 표현하고 싶을 때는 - 기호를 사용함